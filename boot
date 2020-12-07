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

x-bash.debug.list(){
    declare -f | grep "()" | grep "\.debug" | cut -d ' ' -f 1
}

x-bash.debug.init(){
    for i in "$@"; do
        eval "$i.debug() { :; }"
    done
}

x-bash.debug.is_enable(){
    [ "$(declare -f "$i.debug" | wc -l)" -gt 4 ]
}

export X_BASH_COLOR_LOG=1
x-bash.logger(){
    local logger=$1 level=$2
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
            printf "\e[;2m%s[%s]: %s\e[0m\n" "$logger" "$level" "$@"
        else
            printf "%s[%s]: %s\n" "$logger" "$level" "$@"
        fi
    fi >&2
    return 0
}

x-bash.debug.enable(){
    for i in "$@"; do
        eval "$i.debug() { x-bash.logger \"$i\" DBG \"\$@\"; }"
        # eval "X_BASH_DEBUG_$i=1"
    done
}

x-bash.debug.disable(){
    x-bash.debug.init "$@"
}

x-bash.debug.enable boot
x-bash.debug.init @src

boot.debug "Start initializing."

# BUG Notice, if we use eval instead of source to introduce the code, the BASH_SOURCE[0] will not be the location of this file.
X_BASH_SRC_PATH="$HOME/.x-cmd.com/x-bash"
if grep "boot.debug" "${BASH_SOURCE[0]}" 1>/dev/null 2>&1; then
    X_BASH_SRC_PATH=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
else
    echo "Script is NOT executed by source. So we have to guess $X_BASH_SRC_PATH as its path" >&2
fi
boot.debug "Setting env X_BASH_SRC_PATH: $X_BASH_SRC_PATH"

cat >"$X_BASH_SRC_PATH/.source.mirror.list" <<A
https://x-bash.github.io
https://x-bash.gitee.io
A

x-bash.mirrors(){
    cat "$X_BASH_SRC_PATH/.source.mirror.list"
}

x-bash.mirrors.write(){
    if [ $# -ne 0 ]; then
        local IFS=$'\n'
        echo "$*" >"$X_BASH_SRC_PATH/.source.mirror.list"
        return 0
    fi
    return 1
}

x-bash.clear(){
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

x-bash.cache(){ echo "$X_BASH_SRC_PATH"; }
x.enable.xrc(){ alias xrc='SRC_LOADER=bash x-bash.src'; }
x.enable.x(){
    X_BASH_X_CMD_PATH="$(command -v x)"
    x(){
        case "$1" in
            rc|src) SRC_LOADER=bash x-bash_.src.one "$@" ;;
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

x+(){ x-bash.src "$@"; }
x?(){ x-bash.src.which "$@"; }

@src(){ x-bash.src "$@"; }

x-bash.src(){
    if [ $# -eq 0 ]; then
        cat >&2 <<A
x-bash.src    x-bash core fun ction.
        Uasge:  x. <lib> [<lib>...]
        Notice, builtin command 'source' format is 'source <lib> [argument...]'"
A
        return 1
    fi
    
    for i in "$@"; do 
        x-bash_.src.one "$i"
        local code=$?
        if [ $code -ne 0 ]; then 
            return $code
        fi
    done
    return 0
}

x-bash.curl(){
    local REDIRECT=/dev/stdout
    if [ -n "$CACHE" ]; then
        if [ -z "$UPDATE" ] && [ -f "$CACHE" ]; then
            @src.debug "x-bash.curl() aborted. Because update is NOT forced and file existed: $CACHE"
            return 0
        fi
        REDIRECT=$TMPDIR.x-bash-temp-download.$RANDOM
    fi

    x.http.get "$1" 1>"$REDIRECT" 2>/dev/null
    local code=$?
    @src.debug "x.http.get $1 return code: $code"
    if [ $code -eq 0 ]; then 
        if [ -n "$CACHE" ]; then
            @src.debug "Copy the temp file to CACHE file: $CACHE"
            mkdir -p "$(dirname "$CACHE")"
            mv "$REDIRECT" "$CACHE"
        fi
    fi
    return $code
}

x-bash.curl.gitx(){   # Simple strategy
    local IFS i=0 ELEM CANS URL="${1:?Provide location like std/str}"
    read -r -d '\n' -a CANS <<<"$(x-bash.mirrors)"
    for ELEM in "${CANS[@]}"; do
        @src.debug "Trying x-bash.curl $ELEM/$1"
        x-bash.curl "$ELEM/$1"
        case $? in
        0)  if [ ! "${CANS[0]}" = "$ELEM" ]; then
                local tmp=${CANS[0]}
                CANS[0]="$ELEM"
                eval "CANS[$i]=$tmp"
                @src.debug "First guess NOW is ${CANS[0]}"
                x-bash.mirrors.write "${CANS[@]}"
            fi
            return 0;;
        4)  return 4;;
        esac
        (( i = i + 1 ))
    done
    return 1
}

x-bash.src.which(){
    if [ $# -eq 0 ]; then
        cat >&2 <<A
x-bash.src.which  Download lib files and print the local path.
        Uasge:  x-bash.src.which <lib> [<lib>...]
        Example: source "$(x-bash.src.which std/str)"
A
        return 1
    fi
    local i code
    for i in "$@"; do
        x-bash_.src.which.one "$i"
        code=$?
        [ $code -ne 0 ] && return $code
    done
}

x-bash_.src.which.one(){
    local RESOURCE_NAME=${1:?Provide resource name};

    local filename method
    method=${RESOURCE_NAME##*\#}
    RESOURCE_NAME=${RESOURCE_NAME%\#*}

    filename=${RESOURCE_NAME##*/}
    @src.debug "Parsed result: $RESOURCE_NAME $filename.$method"

    local TGT
    if [[ "$RESOURCE_NAME" =~ ^\.\.?/ ]] || [[ "$RESOURCE_NAME" =~ ^/ ]]; then
        # We don't know why using ${BASH_SOURCE[2]}, we just test. The first two arguments is ./boot, ./boot, or "" ""
        echo "$(dirname "${BASH_SOURCE[2]}")/$RESOURCE_NAME"
        return
    fi

    if [[ "$RESOURCE_NAME" =~ ^https?:// ]]; then
        TGT="$X_BASH_SRC_PATH/BASE64-URL-$(echo -n "$URL" | base64)"
        if ! CACHE="$TGT" x-bash.curl "$RESOURCE_NAME"; then
            echo "ERROR: Fail to load http resource due to network error or other: $RESOURCE_NAME " >&2
            return 1
        fi

        echo "$TGT"
        return 0
    fi

    local module=$RESOURCE_NAME
    # If it is short alias like str (short for std/str), then search the https://x-bash.github.io/index
    if [[ ! $module =~ \/ ]]; then

        local index_file="$X_BASH_SRC_PATH/index"
        if [[ ! $(find "$index_file" -mtime +1h -print) ]]; then # Trigger update even if index file is old
            @src.debug "Rebuilding $index_file with best effort."
            if ! CACHE="$index_file" x-bash.curl.gitx "index"; then
                if [ -r "$index_file" ]; then
                    @src.debug "To avoid useless retry in internet free situation, touch the index file so next retry will be an hour later."
                    touch "$index_file" # To avoid frequently update if failure.
                fi
            fi
        fi

        if [ ! -f "$index_file" ]; then
            @src.debug "Exit because index file fail to download: $index_file"
            return 1
        fi

        # module="$(grep "$RESOURCE_NAME" "$index_file" | head -n 1)"
        @src.debug "Using index file: $index_file"
        local line name full_name module=""
        while read -r line; do
            if [ "$line" = "" ]; then
                continue
            fi
            name=${line%\ *}
            full_name=${line#*\ }
            @src.debug "Looking up: $name => $full_name"
            if [ "$name" = "$RESOURCE_NAME" ]; then
                module="$full_name"
                break
            fi
        done <"$index_file"

        if [ -z "$module" ]; then
            echo "ERROR: $RESOURCE_NAME NOT found" >&2
            return 1
        fi
        @src.debug "Using module $module"
    fi

    TGT="$X_BASH_SRC_PATH/$module"

    if ! CACHE="$TGT" x-bash.curl.gitx "$module"; then
        echo "ERROR: Fail to load $RESOURCE_NAME due to network error or other. Do you want to load std/$RESOURCE_NAME?" >&2
        return 1
    fi

    echo "$TGT"
}

x-bash_.src.one(){
    # Notice: Using x-bash_.print_code to make sure of a clean environment for script execution
    eval "$(x-bash_.print_code "$@")"
}

x-bash_.print_code(){
    local TGT RESOURCE_NAME=${1:?Provide resource name}; shift

    local filename method
    method=${RESOURCE_NAME##*\#}
    RESOURCE_NAME=${RESOURCE_NAME%\#*}

    filename=${RESOURCE_NAME##*/}

    TGT="$(x-bash_.src.which.one "$RESOURCE_NAME")"
    
    local code=$?
    if [ $code -ne 0 ]; then
        @src.debug "Aborted. Because 'x-bash_.src.which.one $RESOURCE_NAME'return Code is Non-Zero: $code"
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

export -f @src
