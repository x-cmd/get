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

@use(){ 
    init.curl
    mkdir -p "$HOME/.x-cmd.com/x-bash/std"
    mkdir -p "$HOME/.x-cmd.com/x-bash/cloud"
    [ ! -e "$HOME/.x-cmd.com/x-bash/$1" ] && \
        $CURL https://x-bash.github.io/$1 > "$HOME/.x-cmd.com/x-bash/$1" 2>/dev/null && \
    
    [ $? -eq 0 ] && source "$HOME/.x-cmd.com/x-bash/$1"
    # eval "$($CURL https://x-bash.github.io/$1)"
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
    printf "$s" "$*"
}

# Normally, output the progress
@log(){ echo "$*" >&2; } # fine
@fatal(){ echo "$*" >&2; exit; } # error

# Normally, output info, json or yml
@out(){ echo "$*" >&1; }
