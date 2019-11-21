#! /usr/bin/env bash

# https://stackoverflow.com/questions/1527049/how-can-i-join-elements-of-an-array-in-bash
# join_ws also works

str.join(){
    local sep=$1
    shift 1
    local bar=$(printf "${sep}%s" "$@")
    bar=${bar:${#sep}}
    echo $bar
}

str.trim(){
    local var="$*"
    # remove leading whitespace characters
    var="${var#"${var%%[![:space:]]*}"}"
    # remove trailing whitespace characters
    var="${var%"${var##*[![:space:]]}"}"   
    echo -n "$var"
}

str.trimLeft(){
    :
}

str.trimRight(){
    :
}

# According to 
str.split(){
    :
}

str.upper(){ echo -n $1 | tr [:lower:] [:upper:]; }
str.lower(){ echo -n $1 | tr [:upper:] [:lower:]; }

# other format using library

## Regular Expression design

OR="\|"

R.wrap(){
    echo -n "\($1\)"
}

R.or(){
    R.wrap $(str.join "\|" "$@")
}

# IP_0_255="[0-9]${OR}\([1-9][0-9]\)${OR}\(1[0-9][0-9]\)${OR}\(2[0-4][0-9]\)${OR}\(25[0-5]\)"
IP_0_255=$( R.or `R.wrap [0-9]` `R.wrap [1-9][0-9]` `R.wrap 1[0-9][0-9]` `R.wrap 2[0-4][0-9]` `R.wrap 25[0-5]` )
IP="\\b${IP_0_255}\.${IP_0_255}\.${IP_0_255}\.${IP_0_255}\\b"
