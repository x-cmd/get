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

# WCSS #8
echon(){
    printf "$s" "$*"
}

# Normally, output the progress
@log(){ echo "$*" >&2; } # fine
@fatal(){ echo "$*" >&2; exit; } # error

# Normally, output info, json or yml
@out(){ echo "$*" >&1; }



