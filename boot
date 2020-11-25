# shellcheck shell=bash

if [ -n "$RELOAD" ] || [ -z "$X_BASH_SRC_PATH" ]; then

    if curl --version 1>/dev/null 2>&1; then
        @src.http.get(){
            curl --fail "${1:?Provide target URL}"; 
            local code=$?
            [ $code -eq 28 ] && return 4
            return $code
        }
    elif wget --help 1>/dev/null 2>&1; then
        # busybox and alpine is with wget but without curl. But both are without bash and tls by default
        @src.http.get(){
            wget -qO - "${1:?Provide target URL}"
            local code=$?; 
            [ $code -eq 8 ] && return 4; 
            return $code
        }
    elif x author | grep "Edwin.JH.Lee & LTeam" 1>/dev/null 2>/dev/null; then
        @src.http.get(){
            x cat "${1:?Provide target URL}"
        }
    else
        # If fail, boot init process PANIC.
        echo "Curl, wget or X command NOT found in the system." >&2
        return 127
    fi

    # BUG Notice, if we use eval instead of source to introduce the code, the BASH_SOURCE[0] will not be the location of this file.
    X_BASH_SRC_PATH="$HOME/.x-cmd.com/x-bash"
    if grep "@src.one(){" "${BASH_SOURCE[0]}" 1>/dev/null 2>&1; then
        X_BASH_SRC_PATH=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
    else
        echo "Script is NOT executed by source. So we have to guess $X_BASH_SRC_PATH as its path" >&2
    fi

    X_BASH_SRC_PATH_WEB_URL=( https://x-bash.github.io https://x-bash.gitee.io )
    # X_BASH_SRC_PATH_WEB_URL=( https://x-bash.github.io )

    @src.debug(){
        local IFS=
        [[ "$X_BASH_DEUBG" =~ (^|,)boot($|,) ]] && printf "DBG: %s\n" "$@" >&2
    }

    @src.reload(){
        # shellcheck disable=SC1090
        RELOAD=1 source "${1:?Please provide boot file path}"
    }

    @src.clear(){
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

    @src.cache(){ echo "$X_BASH_SRC_PATH"; }
    @src.bash(){ SRC_LOADER=bash @src "$@"; } # Consider using x.
    @src.which(){ SRC_LOADER=which @src "$@"; }

    @src(){
        if [ $# -eq 0 ]; then
            cat >&2 <<A
@src    x-bash core function.
            Uasge:  @src <lib> [<lib>...]
            Notice, builtin command 'source' format is 'source <lib> [argument...]'"
A
            return 1
        fi
        
        for i in "$@"; do 
            @src.one "$i"
            local code=$?
            [ $code -ne 0 ] && return $code
        done
    }

    @src.curl(){
        local REDIRECT=/dev/stdout
        if [ -n "$CACHE" ]; then
            if [ -z "$UPDATE" ] && [ -f "$CACHE" ]; then
                @src.debug "@src.curl() exits. Because it is NOT forced update and cache file existed in disk: \n $CACHE"
                return
            fi
            REDIRECT=$TMPDIR.x-bash-temp-download.$RANDOM
        fi

        @src.http.get "$1" 1>"$REDIRECT" 2>/dev/null
        local code=$?
        @src.debug "@src.http.get $1 \t return code: $code"
        if [ $code -eq 0 ]; then 
            if [ -n "$CACHE" ]; then
                @src.debug "Copy the temp file to CACHE file: $CACHE"
                mkdir -p "$(dirname "$CACHE")"
                mv "$REDIRECT" "$CACHE"
            fi
        fi
        return $code
    }

    @src.curl.gitx(){   # Simple strategy
        local i ELEM URL="${1:?Provide location like std/str}"
        (( i = 0 ))
        for ELEM in "${X_BASH_SRC_PATH_WEB_URL[@]}"; do
            @src.debug "Trying: @src.curl $ELEM/$1" >&2
            @src.curl "$ELEM/$1"
            case $? in
            0)  local tmp=${X_BASH_SRC_PATH_WEB_URL[0]}
                X_BASH_SRC_PATH_WEB_URL[0]="$ELEM"
                eval "X_BASH_SRC_PATH_WEB_URL[$i]=$tmp"
                return 0;;
            4)  return 4;;
            esac
            (( i = i + 1 ))
        done
        return 1
    }

    @src.which(){
        if [ $# -eq 0 ]; then
            cat >&2 <<A
@src.which  Download lib files and print the local path.
            Uasge:  @src.which <lib> [<lib>...]
            Example: source "$(@src.which std/str)"
A
            return 1
        fi
        local i code
        for i in "$@"; do
            @src.which.one "$i"
            code=$?
            [ $code -ne 0 ] && return $code
        done
    }

    @src.which.one(){
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
            if ! CACHE="$TGT" @src.curl "$RESOURCE_NAME"; then
                echo "ERROR: Fail to load $RESOURCE_NAME due to network error or other. Do you want to load std/$RESOURCE_NAME?" >&2
                return 1
            fi

            echo "$TGT"
            return 
        fi

        local module=$RESOURCE_NAME
        # If it is short alias like str (short for std/str), then search the https://x-bash.github.io/index
        if [[ ! $module =~ \/ ]]; then

            local index_file="$X_BASH_SRC_PATH/index"
            if [[ ! $(find "$index_file" -mtime +1h -print) ]]; then # Trigger update even if index file is old
                @src.debug "Rebuilding $index_file with best effort."
                if ! CACHE="$index_file" @src.curl.gitx "index"; then
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
            local name full_name module=""
            while read -r name full_name; do
                if [ "$name" = "$RESOURCE_NAME" ]; then
                    module="$full_name"
                    break
                fi
            done <"$index_file"

            [ -z "$module" ] && {
                echo "ERROR: $RESOURCE_NAME NOT found" >&2
                return 1
            }
            @src.debug "Using module $module"
        fi

        TGT="$X_BASH_SRC_PATH/$module"

        if ! CACHE="$TGT" @src.curl.gitx "$module"; then
            echo "ERROR: Fail to load $RESOURCE_NAME due to network error or other. Do you want to load std/$RESOURCE_NAME?" >&2
            return 1
        fi

        echo "$TGT"
    }

    @src.one(){
        # Notice: Using @src.__print_code to make sure we have a clean environment for script sourced or execution
        eval "$(@src.__print_code "$@")"
    }

    @src.__print_code(){
        local TGT RESOURCE_NAME=${1:?Provide resource name}; shift

        local filename method
        method=${RESOURCE_NAME##*\#}
        RESOURCE_NAME=${RESOURCE_NAME%\#*}

        filename=${RESOURCE_NAME##*/}

        TGT="$(@src.which.one "$RESOURCE_NAME")"

        local code=$?
        [ $code -ne 0 ] && return $code
    
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

    export -f @src @src.one @src.http.get @src.which @src.curl @src.bash
fi
