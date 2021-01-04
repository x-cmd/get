# shellcheck shell=bash

if [ -z "$RELOAD" ] && [ -n "$X_BASH_SRC_PATH" ]; then
    return 0 || exit 0
fi

if curl --version 1>/dev/null 2>&1; then
    x.http.get(){
        curl --fail "${1:?Provide target URL}"; 
        local code=$?
        [ $code -eq 28 ] && return 4
        return $code
    }
elif wget --help 1>/dev/null 2>&1; then
    # busybox and alpine is with wget but without curl. But both are without bash and tls by default
    x.http.get(){
        wget -qO - "${1:?Provide target URL}"
        local code=$?; 
        [ $code -eq 8 ] && return 4; 
        return $code
    }
elif x author | grep "Edwin.JH.Lee & LTeam" 1>/dev/null 2>/dev/null; then
    x.http.get(){
        x cat "${1:?Provide target URL}"
    }
else
    # If fail, boot init process PANIC.
    echo "Curl, wget or X command NOT found in the system." >&2
    return 127 2>/dev/null || exit 127
fi

x.debug.list(){
    declare -f | grep "()" | grep "\.debug" | cut -d ' ' -f 1
}

x.debug.init(){
    for i in "$@"; do
        eval "command -v $i.debug 1>/dev/null || $i.debug() { :; }"
        eval "$i.debug.enable(){ x.debug.enable $i; }"
        eval "$i.debug.disable(){ $i.debug() { :; }; }"
        eval "export -f $i.debug $i.debug.enable $i.debug.disable"
    done
}

x.debug.is_enable(){
    [ "$(declare -f "$i.debug" | wc -l)" -gt 4 ]
}

export X_BASH_COLOR_LOG=1
xrc_.logger(){
    local logger=$1 level=$2 IFS=
    shift 2
    if [ $# -eq 0 ]; then
        if [ -n "$X_BASH_COLOR_LOG" ]; then
            printf "\e[31m%s[%s]: " "$logger" "$level" 
            cat
            printf "\e[0m\n"
        else
            printf "%s[%s]: " "$logger" "$level"
            cat
            printf "\n"
        fi
    else
        if [ -n "$X_BASH_COLOR_LOG" ]; then
            printf "\e[;2m%s[%s]: %s\e[0m\n" "$logger" "$level" "$*"
        else
            printf "%s[%s]: %s\n" "$logger" "$level" "$*"
        fi
    fi >&2
    return 0
}

x.debug.enable(){
    local i
    for i in "$@"; do
        [ "@src" = "$i" ] && i=xrc
        eval "$i.debug() { xrc_.logger \"$i\" DBG \"\$@\"; }"
        eval "export -f $i.debug"
        # eval "X_BASH_DEBUG_$i=1"
    done
}

x.debug.disable(){
    x.debug.init "$@"
}

x.debug.init boot xrc
x.debug.enable boot


boot.debug "Start initializing."

# BUG Notice, if we use eval instead of source to introduce the code, the BASH_SOURCE[0] will not be the location of this file.
X_BASH_SRC_PATH="$HOME/.x-cmd.com/x-bash"
if grep "boot.debug" "${BASH_SOURCE[0]}" 1>/dev/null 2>&1; then
    X_BASH_SRC_PATH=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
else
    echo "Script is NOT executed by source. So we have to guess $X_BASH_SRC_PATH as its path" >&2
fi
export X_BASH_SRC_PATH
boot.debug "Setting env X_BASH_SRC_PATH: $X_BASH_SRC_PATH"

mkdir -p "$X_BASH_SRC_PATH"

cat >"$X_BASH_SRC_PATH/.source.mirror.list" <<A
https://x-bash.github.io
https://x-bash.gitee.io
A

boot.debug "Creating $X_BASH_SRC_PATH/.source.mirror.list"

# shellcheck disable=SC2120
xrc.mirrors(){
    local fp="$X_BASH_SRC_PATH/.source.mirror.list"
    if [ $# -ne 0 ]; then
        local IFS=$'\n'
        echo "$*" >"$fp"
    else
        cat "$fp"
    fi
    return 0
}

xrc.clear(){
    if [ -f "${X_BASH_SRC_PATH:?Env X_BASH_SRC_PATH should not be empty.}/boot" ]; then
        if [ "$X_BASH_SRC_PATH" == "/" ]; then
            echo "Env X_BASH_SRC_PATH should not be /" >&2
        else
            rm -rf "$X_BASH_SRC_PATH";
        fi
    else
        echo "'$X_BASH_SRC_PATH/boot' NOT found." >&2
    fi
}

xrc.cache(){ echo "$X_BASH_SRC_PATH"; }
x.activate(){
    X_BASH_X_CMD_PATH="$(command -v x)"
    x(){
        case "$1" in
            rc|src) SRC_LOADER=bash xrc_.one "$@" ;;
            # java | jar);;
            # python | py);;
            # javascript | js);;
            # typescript | ts);;
            # ruby | rb);;
            # lua);;
            *) "$X_BASH_X_CMD_PATH" "$@" ;;
        esac
    }
}

@src(){ xrc "$@"; }
@src.which(){ xrc.which "$@"; }

xrc(){
    if [ $# -eq 0 ]; then
        cat >&2 <<A
xrc     x-bash core function.
        Uasge:  xrc <lib> [<lib>...]
        Notice, builtin command 'source' format is 'source <lib> [argument...]'"
A
        return 1
    fi
    
    for i in "$@"; do 
        xrc_.one "$i"
        local code=$?
        if [ $code -ne 0 ]; then 
            return $code
        fi
    done
    return 0
}

xrc.curl(){
    local REDIRECT=/dev/stdout
    if [ -n "$CACHE" ]; then
        if [ -z "$UPDATE" ] && [ -f "$CACHE" ]; then
            xrc.debug "xrc.curl() terminated. Because update is NOT forced and file existed: $CACHE"
            return 0
        fi
        REDIRECT=$TMPDIR.x-bash-temp-download.$RANDOM
    fi

    x.http.get "$1" 1>"$REDIRECT" 2>/dev/null
    local code=$?
    xrc.debug "x.http.get $1 return code: $code"
    if [ $code -eq 0 ]; then 
        if [ -n "$CACHE" ]; then
            xrc.debug "Copy the temp file to CACHE file: $CACHE"
            mkdir -p "$(dirname "$CACHE")"
            mv "$REDIRECT" "$CACHE"
        fi
    fi
    return $code
}

xrc.curl.gitx(){   # Simple strategy
    local IFS i=0 ELEM CANS URL="${1:?Provide location like std/str}"
    read -r -d '\n' -a CANS <<<"$(xrc.mirrors)"
    for ELEM in "${CANS[@]}"; do
        xrc.debug "Trying xrc.curl $ELEM/$1"
        xrc.curl "$ELEM/$1"
        case $? in
        0)  if [ ! "${CANS[0]}" = "$ELEM" ]; then
                local tmp=${CANS[0]}
                CANS[0]="$ELEM"
                eval "CANS[$i]=$tmp"
                xrc.debug "First guess NOW is ${CANS[0]}"
                xrc.mirrors "${CANS[@]}"
            fi
            return 0;;
        4)  return 4;;
        esac
        (( i = i + 1 ))
    done
    return 1
}

xrc.which(){
    if [ $# -eq 0 ]; then
        cat >&2 <<A
xrc.which  Download lib files and print the local path.
        Uasge:  xrc.which <lib> [<lib>...]
        Example: source "$(xrc.which std/str)"
A
        return 1
    fi
    local i code
    for i in "$@"; do
        xrc_.which.one "$i"
        code=$?
        [ $code -ne 0 ] && return $code
    done
}

xrc.update(){
    local index_file="${1:-"$X_BASH_SRC_PATH/index"}"
    xrc.debug "Rebuilding $index_file with best effort."
    if UPDATE=1 CACHE="$index_file" xrc.curl.gitx "index"; then
        return 0
    fi
    if [ -r "$index_file" ]; then
        xrc.debug "To avoid useless retry under internet free situation, touch the index file so next retry will be an hour later."
        touch "$index_file" # To avoid frequent update if failure. 
    fi
    return 1
}

xrc_.which.one(){
    local RESOURCE_NAME=${1:?Provide resource name};

    local filename method
    method=${RESOURCE_NAME##*\#}
    RESOURCE_NAME=${RESOURCE_NAME%\#*}

    filename=${RESOURCE_NAME##*/}
    xrc.debug "Parsed result: $RESOURCE_NAME $filename.$method"

    local TGT
    if [[ "$RESOURCE_NAME" =~ ^\.\.?/ ]] || [[ "$RESOURCE_NAME" =~ ^/ ]]; then
        # We don't know why using ${BASH_SOURCE[2]}, we just test. The first two arguments is ./boot, ./boot, or "" ""
        echo "$(dirname "${BASH_SOURCE[2]}")/$RESOURCE_NAME"
        return
    fi

    if [[ "$RESOURCE_NAME" =~ ^https?:// ]]; then
        TGT="$X_BASH_SRC_PATH/BASE64-URL-$(echo -n "$URL" | base64)"
        if ! CACHE="$TGT" xrc.curl "$RESOURCE_NAME"; then
            echo "ERROR: Fail to load http resource due to network error or other: $RESOURCE_NAME " >&2
            return 1
        fi

        echo "$TGT"
        return 0
    fi

    local module=$RESOURCE_NAME
    # If it is short alias like str (short for std/str), then search the https://xrc.github.io/index
    if [[ ! $module =~ \/ ]]; then

        local index_file="$X_BASH_SRC_PATH/index"
        if [[ ! $(find "$index_file" -mmin 60 -print 2>/dev/null 1>&2) ]]; then # Trigger update even if index file is old
            xrc.update "$index_file"
        fi

        if [ ! -f "$index_file" ]; then
            xrc.debug "Exit because index file is inavailable: $index_file"
            return 1
        fi

        # module="$(grep "$RESOURCE_NAME" "$index_file" | head -n 1)"
        xrc.debug "Using index file: $index_file"
        local line name full_name module=""
        while read -r line; do
            if [ "$line" = "" ]; then
                continue
            fi
            name=${line%\ *}
            full_name=${line#*\ }
            xrc.debug "Looking up: $name => $full_name"
            if [ "$name" = "$RESOURCE_NAME" ]; then
                module="$full_name"
                break
            fi
        done <"$index_file"

        if [ -z "$module" ]; then
            echo "ERROR: $RESOURCE_NAME NOT found" >&2
            return 1
        fi
        xrc.debug "Using module $module"
    fi

    TGT="$X_BASH_SRC_PATH/$module"

    if ! CACHE="$TGT" xrc.curl.gitx "$module"; then
        echo "ERROR: Fail to load $RESOURCE_NAME due to network error or other. Do you want to load std/$RESOURCE_NAME?" >&2
        return 1
    fi

    echo "$TGT"
}

xrc_.one(){
    # Notice: Using xrc_.print_code to make sure of a clean environment for script execution
    eval "$(xrc_.print_code "$@")"
}

xrc_.print_code(){
    local TGT RESOURCE_NAME=${1:?Provide resource name}; shift

    local filename method
    method=${RESOURCE_NAME##*\#}
    RESOURCE_NAME=${RESOURCE_NAME%\#*}

    filename=${RESOURCE_NAME##*/}

    TGT="$(xrc_.which.one "$RESOURCE_NAME")"
    
    local code=$?
    if [ $code -ne 0 ]; then
        xrc.debug "Aborted. Because 'xrc_.which.one $RESOURCE_NAME'return Code is Non-Zero: $code"
        return $code
    fi

    local RUN="${SRC_LOADER:-source}"

    case "$RUN" in
    bash)
        if [ -z "$method" ]; then
            echo bash "$TGT" "$@"
        else
            local final_code
            final_code="$(cat <<A
source "$TGT"

if typeset -f "$filename.$method" 1>/dev/null; then
    $filename.$method $@
else
    $method $@
fi
A
)"
        echo "echo \"$final_code\" | bash"
        fi ;;
    *)
        echo "$RUN" "$TGT" "$@";;
    esac
}

export -f \
    xrc_.logger \
    x.debug.disable x.debug.enable x.debug.init x.debug.is_enable x.debug.list \
    x.http.get x.activate \
    xrc_.which.one \
    @src @src.which \
    xrc xrc.which \
    xrc.update \
    xrc.curl xrc.curl.gitx \
    xrc_.one xrc_.print_code \
    xrc.mirrors


