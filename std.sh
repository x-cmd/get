#! /usr/bin/env bash

init.curl(){
    if [ ! "$CURL" == "" ]; then
        return
    fi

    curl --version
    if [ $? -eq 0 ]; then
        export CURL=curl
        return
    fi

    x curl --version
    if [ $? -eq 0 ]; then
        export CURL="x curl"
        return
    fi
    
    return 1
}

# import docker
# import aws
# import std/aws

# eval "$(x bash/std)"
# eval "$(curl https://x-cmder.github.io/bash)"

import-bash(){
    init.curl
    eval "$($CURL https://x-cmder.github.io/bash/$1)"
}
@use(){ import-bash "$@"; }

import-std(){ import-bash "std/$1"; }
@std(){ import-std "$@"; }

import-all(){
    import-std color
    import-std regex
    import-std str
    import-std text
}
@stdall(){ import-all; }

# Not good
X_CMD_COM_RETURN=""
@ret(){
    export X_CMD_COM_RETURN="$@"
}

@res(){
    echo "$X_CMD_COM_RETURN"
}


# Using X, or using curl to include method

# WCSS #80
title(){
    if [ $# -eq 0 ]; then
        echo "Usage: $0 <title to display>" >&2
    else
        echo -e "\033]0;$@\007"
    fi
}

rot13(){
    tr '[a-zA-Z]' '[n-za-mN-ZA-M]'
}

rot(){
    local rot=${1:-13}
    local letter='abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789=-+'
    local before=${letter:0:$rot}
    local after=${letter:$rot}
    tr $letter $after$before
}

rot.encode(){ rot "$*"; }
rot.decode(){ local ROT=${1:-13}; rot $(( 65 - ROT )); }
rot.brb.encode(){ base64 | rot.encode $1 | base64; }
rot.brb.decode(){ base64 -d | rot.decode $1 | base64 -d; }

# WCSS #8
echon(){
    printf "$s" "$*"
}

# Normally, output the progress
@log(){ echo "$*" >&2; } # fine
@fatal(){ echo "$*" >&2; exit; } # error

# Normally, output info, json or yml
@out(){ echo "$*" >&1; }


# @valid.float()
# @valid.int()
valid.float(){
    [[ "$a" =~ ^[\ \t]+[0-9]+(.[0-9]+)?[\ \t]+$ ]]
    return $?
}

valid.int(){
    [[ "$a" =~ ^[\ \t]+[0-9]+[\ \t]+$ ]]
    return $?
}

# rand

# TODO: padding zero in the beginning
rand.int(){
    local ST=${1:?start number}
    local EN=${2:?end number}

    local SIZE=${3:-0}

    printf "%0${SIZE}d" $(( ( RANDOM % ($EN-$ST) )  + $ST ))
}

rand.float(){
    noop
}

rand.str(){
    noop
}

# fp

# In linux, `readlink -f <filepath>`. Not works in BSD system
# https://superuser.com/questions/330199/how-get-full-path-to-target-of-link

function readlink.fullpath(){
    local TGT=${1:?"Provide name"}

    local ORI=$(pwd)
    local A=$(readlink $TGT)
    cd $(dirname $TGT)
    cd $(dirname $A)
    local RES=$(pwd)
    cd $ORI

    echo $RES/$(basename $A)
}

# path
alias path.dirname=dirname
alias path.basename=basename
path.extension(){
    local filepath=$(basename ${1:?Provide file path})
    local ret=${filepath#*.}
    # to handle the situation: `path.extensions abc`, ret="abc", should return ""
    [ "$ret" == "$filepath" ] || echo $ret
}

path.filename(){
    local filepath=$(basename ${1:?Provide file path})
    echo ${filepath%.*}
}

str.trim(){
    local var="$*"
    # remove leading whitespace characters
    var="${var#"${var%%[![:space:]]*}"}"
    # remove trailing whitespace characters
    var="${var%"${var##*[![:space:]]}"}"   
    echo -n "$var"
}

crypto.base64(){
    echo $(str.trim $(echo -n ${1:?Provide string} | base64))
}

crypto.unbase64(){
    echo $(str.trim $(echo -n ${1:?Provide string} | base64 -d))
}

# kv

map.open(){
    export KV_MAP=${1:?map}
    touch $KV_MAP
}

map.keys(){
    for e in $(cat ${KV_MAP:?"Please invoke map.init()"} | cut -d ' ' -f 1); do
        crypto.unbase64 $e
    done
}

map.values(){
    for e in $(cat ${KV_MAP:?"Please invoke map.init()"} | cut -d ' ' -f 2); do
        crypto.unbase64 $e
    done
}

map.put(){
    echo "$(crypto.base64 $1) $(crypto.base64 $2)" >> $KV_MAP
    #TODO: sort and unique
    cat $KV_MAP | sort
}

map.get(){
    local MAP=${KV_MAP:?"Please invoke map.init()"}
    local TARGET=$(crypto.base64 ${1:?Provide target})
    # notice the following not works: for e in $(cat $MAP); do 
    cat $MAP | while read e 
    do
        # echo "111" $e
        local KEY=$(echo $e | cut -d ' ' -f 1)
        if [ "$KEY" == "$TARGET" ]; then
            local VALUE=$(echo $e | cut -d ' ' -f 2)
            # echo $KEY $VALUE
            crypto.unbase64 $VALUE
            return
        fi
    done
}

arr.indexof(){
    local target="$1"
    shift
    local a=0
    for i in "$@"; do
        a=$((a + 1))
        if [ "$i" == "$target" ]; then
            echo $a
            return 0
        fi
    done
    echo -1
    return 1
}

# Cookbook 1.15
repeat(){ while :; do "$@"; sleep ${INTERVAL:-3}; done }
repeat.until.success(){ while :; do "$@" && return; sleep ${INTERVAL:-3}; done }




