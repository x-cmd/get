# shellcheck shell=bash

if [ -n "$RELOAD" ] || [ -z "$X_BASH_SRC_PATH" ]; then
    # BUG Notice, if we use eval instead of source to introduce the code, the BASH_SOURCE[0] will not be the location of this file.
    X_BASH_SRC_PATH="$HOME/.x-cmd.com/x-bash"
    if grep "@src.one(){" "${BASH_SOURCE[0]}" 1>/dev/null 2>&1; then
        X_BASH_SRC_PATH=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
    else
        echo "Script is NOT executed by source. So we have to guess $X_BASH_SRC_PATH as its path" >&2
    fi

    # X_BASH_SRC_PATH_WEB_URL=( https://x-bash.github.io https://x-bash.gitee.io )
    X_BASH_SRC_PATH_WEB_URL=( https://x-bash.github.io )

    @src.reload(){
        # shellcheck disable=SC1090
        RELOAD=1 source "${1:?Please provide boot file path}"
    }

    @src.clear(){ 
        if [ -f "$X_BASH_SRC_PATH/boot" ]; then
            rm -rf "$X_BASH_SRC_PATH";
        else
            echo "'$X_BASH_SRC_PATH/boot' NOT found."
        fi
    }

    @src.cache(){ echo "$X_BASH_SRC_PATH"; }
    @src.bash(){ SRC_LOADER=bash @src "$@"; } # Consider using x.

    @src(){
        [ $# -eq 0 ] && cat >&1 <<A
@src    x-bash core function.
        Uasge:  @src <lib> [<lib>...]
        Notice, builtin command 'source' format is 'source <lib> [argument...]'"
A
        for i in "$@"; do 
            @src.one "$i";
            local code=$?
            [ $code -ne 0 ] && return $code
        done
    }

    @src.curl(){
        local REDIRECT=/dev/stdout
        if [ -n "$CACHE" ]; then
            [ -z "$UPDATE" ] && [ -f "$CACHE" ] && return
            REDIRECT=$TMPDIR.x-bash-temp-download.$RANDOM
        fi

        if ! command -v @src.http.get 1>/dev/null 2>&1; then
            # TODO: checking `x author` == "Edwin.JH.Lee & LTeam"
            # if command -v x 1>/dev/null 2>&1; then
            #     eval '@src.http.get(){ x cat "${1:?Provide target URL}"; }' # If fail, return code is 1
            # el
            if curl --version 1>/dev/null 2>&1; then
                eval '@src.http.get(){ curl --fail "${1:?Provide target URL}"; local code=$?; [ $code -eq 28 ] && return 4; return $code; }' # If fail, return code is 28
            elif wget --help 1>/dev/null 2>&1; then
                # busybox and alpine is with wget but without curl. But both are without bash and tls by default
                eval '@src.http.get(){ wget -qO - "${1:?Provide target URL}"; local code=$?; [ $code -eq 8 ] && return 4; return $code;  }' # If fail, return code is 8
            else
                echo "No other Command for HTTP-GET" >&2
                return 127
            fi
        fi

        @src.http.get "$1" 1>"$REDIRECT" 2>/dev/null
        local code=$?
        echo -e "@src.http.get $1 \t code is $code" >&2
        if [ $code -eq 0 ]; then 
            if [ -n "$CACHE" ]; then
                mkdir -p "$(dirname "$CACHE")"
                mv "$REDIRECT" "$CACHE"
            fi
        fi
        return $code
    }

    @src.curl.gitx(){   # Simple strategy
        local i URL="${1:?Provide location like std/str}"
        (( i = 0 ))
        for ELEM in "${X_BASH_SRC_PATH_WEB_URL[@]}"; do
            echo "@src.curl $ELEM/$1" >&2
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

    @src.one(){
        local RESOURCE_NAME=${1:?Provide resource name}; shift

        local TGT
        if [[ "$RESOURCE_NAME" =~ ^\.\.?/ ]] || [[ "$RESOURCE_NAME" =~ ^/ ]]; then
            # We don't why using ${BASH_SOURCE[2]}, we just test. The first two arguments is ./boot, ./boot, or "" ""
            TGT="$(dirname "${BASH_SOURCE[2]}")/$RESOURCE_NAME"
            # shellcheck disable=SC1090
            source "$TGT"
            return
        fi


        if [[ "$RESOURCE_NAME" =~ ^https?:// ]]; then
            TGT="$X_BASH_SRC_PATH/BASE64-URL-$(echo -n "$URL" | base64)"
            if ! CACHE="$TGT" @src.curl "$RESOURCE_NAME"; then
                echo "ERROR: Fail to load $RESOURCE_NAME due to network error or other. Do you want to load std/$RESOURCE_NAME?" >&2
                return 1
            fi
            
            ${SRC_LOADER:-source} "$TGT" "$@"
            return 
        fi

        local module=$RESOURCE_NAME
        if [[ ! $module =~ \/ ]]; then

            if [ -z "$UPDATE" ]; then # Exists in cache.
                local LOCAL_FILE
                # shellcheck disable=SC2086
                LOCAL_FILE="$(find $X_BASH_SRC_PATH/*/$RESOURCE_NAME 2>/dev/null | head -n 1)"
                if [ -r "$LOCAL_FILE" ]; then
                    echo "INFO: Using local file $LOCAL_FILE" >&2
                    ${SRC_LOADER:-source} "$LOCAL_FILE" "$@"
                    return 0
                fi
            fi

            local index_file="$X_BASH_SRC_PATH/index"
            if [[ ! $(find "$index_file" -mtime +1h -print) ]]; then # Trigger update even if index file is old
                echo "INFO: Rebuilding $index_file" >&2
                CACHE="$index_file" @src.curl.gitx "index"
            fi

            if [ ! -f "$index_file" ]; then
                echo "Exit because file fail to download: $index_file"
                return 1
            fi

            module="$(grep "$RESOURCE_NAME" "$index_file" | head -n 1)"
            [ -z "$module" ] && {
                echo "ERROR: $RESOURCE_NAME NOT found" >&2
                return 1
            }
            echo "INFO: Using $module" >&2
        fi

        TGT="$X_BASH_SRC_PATH/$module"

        if ! CACHE="$TGT" @src.curl.gitx "$module"; then
            echo "ERROR: Fail to load $RESOURCE_NAME due to network error or other. Do you want to load std/$RESOURCE_NAME?" >&2
            return 1
        fi
        
        ${SRC_LOADER:-source} "$TGT" "$@"
    # }
    } 2> >(grep -E "${LOG_FILTER:-^ERROR}" >&2)
    # } 2> >(grep -E "${LOG_FILTER:-^(ERROR)|(INFO)}" >&2)

    export -f @src
    export -f @src.one
    export -f @src.curl
    export -f @src.bash
fi
