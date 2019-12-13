#! /usr/bin/env bash

init.curl(){
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

@use(){

    # if [ -e "$1" ]; then
    #     source "$1"
    #     return 1
    # fi

    init.curl
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
    
    [ $? -eq 0 ] && source "$TGT"
}

@use.clear-cache(){
    rm -rf "$HOME/.x-cmd.com/x-bash"
}

@std(){ 
    @use "std/$1"
}

@stdall(){ 
    @std ui
    @std str
}

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
    printf "%s" "$*"
}

# Normally, output the progress
@log(){ echo "$*" >&2; } # fine
@fatal(){ echo "$*" >&2; exit; } # error

# Normally, output info, json or yml
@out(){ echo "$*" >&1; }

@install_in_bashrc(){
    local STR="D=\"\$HOME/.x-cmd.com/x-bash/boot\" eval '[ -e \$D ] || mkdir -p $(dirname $D) && curl \"https://x-bash.github.io/boot\" >\$D && source \$D' "
    if grep "https://x-bash.github.io/boot" "$HOME/.bashrc" >/dev/null; then
        @log Already install
    else
        echo $STR >> $HOME/.bashrc
        @log Installed in "$HOME/.bashrc"
    fi
    # grep "$STR" "$HOME/.bashrc" || echo $STR >> $HOME/.bashrc
}

if [ ! -z "$INSTALL" ]; then
    @install_in_bashrc
fi
