#! /usr/bin/env bash

# TODO: Get rid of this function
@init.curl(){
    if [ ! "$CURL" == "" ]; then
        return
    fi

    curl --version 1>/dev/null 2>&1
    if [ $? -eq 0 ]; then
        export CURL=curl
        return
    fi

    x curl --version 1>/dev/null 2>&1
    if [ $? -eq 0 ]; then
        export CURL="x curl"
        return
    fi
    
    return 1
}

@src.one(){
    @init.curl
    local URL="https://x-bash.github.io/$1"
    local TGT="$HOME/.x-cmd.com/x-bash/$1"
    if [[ "$1" =~ ^http:// ]] || [[ "$1" =~ ^https:// ]]; then
        URL="$1"
        TGT="$HOME/.x-cmd.com/x-bash/$(echo $URL | base64)"
    fi
    
    if [ ! -e "$TGT" ]; then
        mkdir -p $(dirname "$TGT")
        $CURL "$URL" >"$TGT" 2>/dev/null
        if grep ^\<\!DOCTYPE "$TGT" >/dev/null; then
            rm "$TGT"
            echo "Failed to load $1, do you want to load std/$1?"
            return 1
        fi
    fi
    
    [ $? -eq 0 ] && ${X_CMD_COM_PARAM_CMD?-source} "$TGT"
}

@src(){ for i in "$@"; do @src.one $1; done }

@src.clear-cache(){
    rm -rf "$HOME/.x-cmd.com/x-bash"
}

@run(){
    X_CMD_COM_PARAM_CMD=bash @src "$*"
}
