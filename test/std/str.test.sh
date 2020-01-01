# shellcheck shell=bash

str.slice(){
    if [ "$#" -lt 1 ]; then
        echo "Please input a string to slice"
        return 0
    fi
    
    local srcStr=$1
    local start=0
    local end=0
    local resStr=""
    
    case $# in
        1) echo "$srcStr"
        return 1 ;;
        2) end=$2  ;;
        3) start=$2
        end=$3 ;;
        *) echo "Too many parameters"
    esac
    
    if [ "$end" -lt 0 ]; then
        resStr=${srcStr: -end}
    else
        resStr=${srcStr:start:end}
    fi
    
    echo "$resStr"
}

str.trim(){
    local var="$*"
    # remove leading whitespace characters
    var="${var#"${var%%[![:space:]]*}"}"
    # remove trailing whitespace characters
    var="${var%"${var##*[![:space:]]}"}"
}
str.trim_left(){
    local var="$*"
    # remove leading whitespace characters
    var="${var#"${var%%[![:space:]]*}"}"
    echo -n "$var"
}

str.trim_right(){
    local var="$*"
    # remove trailing whitespace characters
    var="${var%"${var##*[![:space:]]}"}"
    echo -n "$var"
}


str.indexof(){
    if [ "$#" -lt 1 ]; then
        echo "Please input a string to slice"
        return 0
    fi
    
    if [ "$#" -gt 2 ]; then
        echo "Too many parameters"
        return 0
    fi
    
    local srcStr=$1
    local indexStr=$2
    local resStr=""
    
    srcStr=$(str.trim "$srcStr")
    indexStr=$(str.trim "$indexStr")

    echo "$srcStr" | grep "$indexStr"

}


str.starts_with(){
    local strSrc=$1
    local begin=$2
    local end=0
    
    if [ "$#" -lt 2 ]; then
        echo "Too many parameters"

    fi
}

str.ends_with(){
    :
}

str.indexof " hello world " "world "
# str.trim "hello world"