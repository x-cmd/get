# shellcheck shell=sh
# shellcheck disable=SC2039

{

if [ -z "$RELOAD" ] && [ -n "$X_BASH_SRC_PATH" ]; then
    return 0 2>/dev/null || exit 0
fi

if curl --version 1>/dev/null 2>&1; then
    x_http_get(){
        curl --fail "${1:?Provide target URL}"; 
        local code=$?
        [ $code -eq 28 ] && return 4
        return $code
    }
elif wget --help 1>/dev/null 2>&1; then
    # busybox and alpine is with wget but without curl. But both are without bash and tls by default
    x_http_get(){
        wget -qO - "${1:?Provide target URL}"
        local code=$?; 
        [ $code -eq 8 ] && return 4; 
        return $code
    }
elif x author | grep "Edwin.JH.Lee & LTeam" 1>/dev/null 2>/dev/null; then
    x_http_get(){
        x cat "${1:?Provide target URL}"
    }
else
    # If fail, boot init process PANIC.
    echo "Curl, wget or X command NOT found in the system." >&2
    return 127 2>/dev/null || exit 127
fi

X_BASH_SRC_SHELL="sh"
if [ -n "$ZSH_VERSION" ]; then
    X_BASH_SRC_SHELL="zsh"
elif [ -n "$BASH_VERSION" ]; then
    X_BASH_SRC_SHELL="bash"
fi
export X_BASH_SRC_SHELL

# It is NOT set in some cases.
TMPDIR=${TMPDIR:-$(dirname "$(mktemp -u)")/}
export TMPDIR

debug_list(){
    # declare -f | grep "()" | grep "\.debug" | cut -d ' ' -f 1
    local i
    for i in "${!XRC_DBG_@}"; do
        echo "${i:8}" | tr "[:lower:]" "[:upper:]"
    done
}

debug_init(){
    local i var
    for i in "$@"; do
        var="$(echo "XRC_DBG_$i" | tr "[:lower:]" "[:upper:]")"
        eval "$var=\${$var:-\$$var}"
        eval "${i}_debug(){ [ \$$var ] && O=$i LEVEL=DBG _debug_logger \"\$@\"; }"
        alias "${i}.debug"="${i}_debug"
        # alias $i_debug="[ \$$var ] && O=$i LEVEL=DBG _debug_logger"
        # alias $i_debug_enable="$var=true"
        # alias $i_debug_disable="$var=;"
        # alias $i_debug_is_enable="[ \$$var ]"
        [ ! $X_BASH_SRC_SHELL = "sh" ] && {
            eval "export $var" 2>/dev/null
            eval "export -f ${i}_debug 2>/dev/null"  # "$i_debug_enable $i.debug_disable"
        }
    done
    
}

debug_enable(){
    local i var
    for i in "$@"; do
        var="$(echo "XRC_DBG_$i" | tr "[:lower:]" "[:upper:]")"
        eval "$var=true"
    done
}

debug_disable(){
    local i var
    for i in "$@"; do
        var="$(echo "XRC_DBG_$i" | tr "[:lower:]" "[:upper:]")"
        eval "$var="
    done
}

debug_is_enable(){
    local var
    var="$(echo "XRC_DBG_${0:?Module}" | tr "[:lower:]" "[:upper:]")"
    eval "[ \$var ]"
}

export XRC_COLOR_LOG=1
_debug_logger(){
    local logger=${O:-DEFAULT} level=${LEVEL:-DBG} IFS=
    # eval "[ \$$var ] && return 0"

    if [ $# -eq 0 ]; then
        if [ -n "$XRC_COLOR_LOG" ]; then
            # printf "\e[31m%s[%s]: " "$logger" "$level" 
            printf "\e[;2m%s[%s]: " "$logger" "$level"
            cat
            printf "\e[0m\n"
        else
            printf "%s[%s]: " "$logger" "$level"
            cat
            printf "\n"
        fi
    else
        if [ -n "$XRC_COLOR_LOG" ]; then
            printf "\e[;2m%s[%s]: %s\e[0m\n" "$logger" "$level" "$*"
        else
            printf "%s[%s]: %s\n" "$logger" "$level" "$*"
        fi
    fi >&2
    return 0
}

debug_init boot xrc
debug_enable boot

boot_debug "Start initializing."


X_BASH_SRC_PATH="$HOME/.x-cmd.com/x-bash"
# TODO: What if zsh
if [ $X_BASH_SRC_SHELL = bash ]; then
    # BUG Notice, if we use eval instead of source to introduce the code, the BASH_SOURCE[0] will not be the location of this file.
    if grep "boot_debug" "${BASH_SOURCE[0]}" 1>/dev/null 2>&1; then
        X_BASH_SRC_PATH=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
    else
        echo "Script is NOT executed by source. So we have to guess $X_BASH_SRC_PATH as its path" >&2
    fi
fi

export X_BASH_SRC_PATH
boot_debug "Setting env X_BASH_SRC_PATH: $X_BASH_SRC_PATH"

mkdir -p "$X_BASH_SRC_PATH"

STR_REGEX_SEP="$(printf "\001")"
str_regex(){
    # Only dash does not support pattern="${pattern//\\/\\\\}"
    awk -v FS="${STR_REGEX_SEP}" '{
        if (match($1, $2))  exit 0
        else                exit 1
    }' <<A
${1}${STR_REGEX_SEP}${2:?str_regex(): Provide pattern}
A
}

# TODO: After sh migration finished. We will apply following str_regex for bash runtime.
# str_regex(){ [[ "${1:?Provide value}" =~ ${2:?Provide pattern} ]]; }
# str_regex "../" "^\.\.?/" && echo yes
# str_regex "aa/" "^\.\.?/" && echo yes


cat >"$X_BASH_SRC_PATH/.source.mirror.list" <<A
https://x-bash.github.io
https://x-bash.gitee.io
A

boot_debug "Creating $X_BASH_SRC_PATH/.source.mirror.list"

# shellcheck disable=SC2120
xrc_mirrors(){
    local fp="$X_BASH_SRC_PATH/.source.mirror.list"
    if [ $# -ne 0 ]; then
        local IFS=$'\n'
        echo "$*" >"$fp"
    else
        cat "$fp"
    fi
    return 0
}

xrc_clear(){
    if [ -f "${X_BASH_SRC_PATH:?Env X_BASH_SRC_PATH should not be empty.}/boot" ]; then
        if [ "$X_BASH_SRC_PATH" = "/" ]; then
            echo "Env X_BASH_SRC_PATH should not be /" >&2
        else
            rm -rf "$X_BASH_SRC_PATH";
        fi
    else
        echo "'$X_BASH_SRC_PATH/boot' NOT found." >&2
    fi
}

xrc_cache(){ echo "$X_BASH_SRC_PATH"; }
x_activate(){
    X_BASH_X_CMD_PATH="$(command -v x)"
    x(){
        case "$1" in
            rc|src) SRC_LOADER=bash _xrc_one "$@" ;;
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
        _xrc_one "$i"
        local code=$?
        if [ $code -ne 0 ]; then 
            return $code
        fi
    done
    return 0
}

xrc_cat(){
    # shellcheck disable=SC2046
    cat $(xrc_which "$@")
}

xrc_curl(){
    local REDIRECT=/dev/stdout
    if [ -n "$CACHE" ]; then
        if [ -z "$UPDATE" ] && [ -f "$CACHE" ]; then
            xrc_debug "xrc_curl() terminated. Because update is NOT forced and file existed: $CACHE"
            return 0
        fi
        REDIRECT=$TMPDIR.x-bash-temp-download.$RANDOM
    fi

    x_http_get "$1" 1>"$REDIRECT" 2>/dev/null
    local code=$?
    xrc_debug "x_http_get $1 return code: $code"
    if [ $code -eq 0 ]; then 
        if [ -n "$CACHE" ]; then
            xrc_debug "Copy the temp file to CACHE file: $CACHE"
            mkdir -p "$(dirname "$CACHE")"
            mv "$REDIRECT" "$CACHE"
        fi
    fi
    return $code
}

xrc_curl_gitx(){   # Simple strategy
    local IFS i=1 mirror mod="${1:?Provide location like std/str}"
    local mirror_list
    mirror_list="$(xrc_mirrors)"
    for mirror in $mirror_list; do
        xrc_debug "Trying xrc_curl $mirror/$mod"
        xrc_curl "$mirror/$mod"
        case $? in
        0)  if [ "$i" -ne 1 ]; then
                xrc_debug "First guess NOW is $mirror"
                xrc_mirrors "$mirror
$(echo "$mirror_list" | awk "NR!=$i{ print \$0 }" )"
            fi
            return 0;;
        4)  return 4;;
        esac
        i=$((i+1))  # Support both ash, dash, bash
    done
    return 1
}

xrc_which(){
    if [ $# -eq 0 ]; then
        cat >&2 <<A
xrc_which  Download lib files and print the local path.
        Uasge:  xrc_which <lib> [<lib>...]
        Example: source "$(xrc_which std/str)"
A
        return 1
    fi
    local i code
    for i in "$@"; do
        _xrc_which_one "$i"
        code=$?
        [ $code -ne 0 ] && return $code
    done
}

xrc_update(){
    local index_file="${1:-"$X_BASH_SRC_PATH/index"}"
    xrc_debug "Rebuilding $index_file with best effort."
    if UPDATE=1 CACHE="$index_file" xrc_curl_gitx "index/main"; then
        return 0
    fi
    if [ -r "$index_file" ]; then
        xrc_debug "To avoid useless retry under internet free situation, touch the index file so next retry will be an hour later."
        touch "$index_file" # To avoid frequent update if failure. 
    fi
    return 1
}

_xrc_which_one(){
    local RESOURCE_NAME=${1:?Provide resource name};

    local filename method
    method=${RESOURCE_NAME##*\#}
    RESOURCE_NAME=${RESOURCE_NAME%\#*}

    filename=${RESOURCE_NAME##*/}
    xrc_debug "Parsed result: $RESOURCE_NAME $filename.$method"

    local TGT
    if str_regex "$RESOURCE_NAME" "^/"; then
        echo "$RESOURCE_NAME"; return 0
    fi

    if str_regex "$RESOURCE_NAME" "^\.\.?/"; then
        # We don't know why using ${BASH_SOURCE[2]}, we just test. The first two arguments is ./boot, ./boot, or "" ""
        # echo "$(dirname "${BASH_SOURCE[2]}")/$RESOURCE_NAME"
        local tmp
        if tmp="$(cd "$(dirname "$RESOURCE_NAME")" || exit 1; pwd)"; then
            echo "$tmp/$(basename "$RESOURCE_NAME")"
            return 0
        else
            echo "local file not exists: $RESOURCE_NAME" >&2
            return 1
        fi
    fi

    if str_regex "$RESOURCE_NAME" "^https?://" ; then
        TGT="$X_BASH_SRC_PATH/BASE64-URL-$(echo -n "$RESOURCE_NAME" | base64 | tr -d '\r\n')"
        if ! CACHE="$TGT" xrc_curl "$RESOURCE_NAME"; then
            echo "ERROR: Fail to load http resource due to network error or other: $RESOURCE_NAME " >&2
            return 1
        fi

        echo "$TGT"
        return 0
    fi

    local module=$RESOURCE_NAME
    # If it is short alias like str (short for std/str), then search the https://xrc_github.io/index
    if ! str_regex "$module" "/" ; then

        local index_file="$X_BASH_SRC_PATH/index"
        if [ -z "$(find "$index_file" -mmin -60 -print 2>/dev/null)" ]; then # Trigger update even if index file is old
            xrc_update "$index_file"
        fi

        if [ ! -f "$index_file" ]; then
            xrc_debug "Exit because index file is inavailable: $index_file"
            return 1
        fi

        # module="$(grep "$RESOURCE_NAME" "$index_file" | head -n 1)"
        xrc_debug "Using index file: $index_file"
        local line name full_name module=""
        while read -r line; do
            if [ "$line" = "" ]; then
                continue
            fi
            name=${line%\ *}
            full_name=${line#*\ }
            xrc_debug "Looking up: $name => $full_name"
            if [ "$name" = "$RESOURCE_NAME" ]; then
                module="$full_name"
                break
            fi
        done <"$index_file"

        if [ -z "$module" ]; then
            echo "ERROR: $RESOURCE_NAME NOT found" >&2
            return 1
        fi
        xrc_debug "Using module $module"
    fi

    TGT="$X_BASH_SRC_PATH/$module"

    if ! CACHE="$TGT" xrc_curl_gitx "$module"; then
        echo "ERROR: Fail to load $RESOURCE_NAME due to network error or other. Do you want to load std/$RESOURCE_NAME?" >&2
        return 1
    fi

    echo "$TGT"
}

_xrc_one(){
    # Notice: Using _xrc_print_code to make sure of a clean environment for script execution
    eval "$(_xrc_print_code "$@")"
}

_xrc_print_code(){
    local TGT RESOURCE_NAME=${1:?Provide resource name}; shift

    local filename method
    method=${RESOURCE_NAME##*\#}
    RESOURCE_NAME=${RESOURCE_NAME%\#*}

    filename=${RESOURCE_NAME##*/}

    TGT="$(_xrc_which_one "$RESOURCE_NAME")"
    
    local code=$?
    if [ $code -ne 0 ]; then
        xrc_debug "Aborted. Because '_xrc_which_one $RESOURCE_NAME'return Code is Non-Zero: $code"
        return $code
    fi

    local RUN="${SRC_LOADER:-.}"

    case "$RUN" in
    bash)
        if [ -z "$method" ]; then
            echo bash "$TGT" "$@"
        else
            local final_code
            # dash does not support 'source'. Using dot instead
            final_code="$(cat <<A
. "$TGT"

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

# @src(){ xrc "$@"; }
# @src.which(){ xrc_which "$@"; }

alias @src=xrc
alias @src.which=xrc_which

alias xrc.which=xrc_which
alias xrcw=xrc_which

alias xrc.update=xrc_update

alias xrc.cat=xrc_cat
alias xrcc=xrc_cat

# debug +enable -xrc ?xrc # Too many functions.
alias debug.disable=debug_disable
alias debug.enable=debug_enable
alias debug.init=debug_init

if [ ! $X_BASH_SRC_SHELL = "sh" ]; then
    # Notice, it will fail on ash and dash
    export -f \
        _debug_logger \
        str_regex \
        debug_enable debug_init debug_is_enable debug_list \
        x_http_get x_activate \
        _xrc_which_one \
        xrc xrc_cat xrc_which \
        xrc_update \
        xrc_curl xrc_curl_gitx \
        _xrc_one _xrc_print_code \
        xrc_mirrors 2>/dev/null
fi

}
