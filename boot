# shellcheck shell=bash

# [ -z "$RELOAD" ] && [ -n "$X_BASH_SRC_PATH" ] && return 0 
# Cannot use return or exit. This seems to be the only way.
if [ -n "$RELOAD" ] || [ -z "$X_BASH_SRC_PATH" ]; then
    echo "Initialize the boot enviroment"
    X_BASH_SRC_PATH=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)

    # Judge whether in China. So we could choose using github or gitee
    X_BASH_SRC_PATH_WEB_URL="https://x-bash.github.io"
    # "https://x-bash.gitee.io"

    @src.reload(){
        # shellcheck disable=SC1090
        RELOAD=1 source "${1:?Please provide boot file path}"
    }

    @src.clear-cache(){
        rm -rf "$X_BASH_SRC_PATH"
    }

    @src(){
        for i in "$@"; do @src.one "$i"; done
    }

    # Consider removing this function, @x will be much better
    @src.bash(){
        SRC_LOADER=bash @src "$@"
    }

    @src.curl(){
        local REDIRECT=/dev/stdout
        [ -n "$CACHE" ] && REDIRECT=$TMPDIR/.x-bash-temp-download

        if ! command -v @src.http.get 1>/dev/null 2>&1; then
            # TODO: checking `x author` == Edwin.JH.Lee & LTeam
            if command -v x 1>/dev/null 2>&1; then
                eval '@src.http.get(){ x cat "${1:?Provide target URL}"; }' # If fail, return code is 1
            elif curl --version 1>/dev/null 2>&1; then
                eval '@src.http.get(){ curl --fail "${1:?Provide target URL}"; }' # If fail, return code is 28
            elif wget --help 1>/dev/null 2>&1; then
                # busybox and alpine is with wget but without curl. But both are without bash and tls by default
                eval '@src.http.get(){ wget -qO - "${1:?Provide target URL}"; }' # If fail, return code is 8
            else
                echo "No other Command for HTTP-GET" >&2
                return 127
            fi
        fi

        if @src.http.get "$1" 1>"$REDIRECT" 2>/dev/null; then   
            if [ -n "$CACHE" ]; then
                mkdir -p "$(dirname "$CACHE")"
                mv "$REDIRECT" "$CACHE"
            fi
        fi
    }

    @src.one(){
        local RESOURCE_NAME=${1:?Provide resource name}; shift

        local URL TGT
        if [[ "$RESOURCE_NAME" =~ ^https?:// ]]; then
            URL="$RESOURCE_NAME"
            TGT="$X_BASH_SRC_PATH/BASE64-URL-$(echo -n "$URL" | base64)"
        else
            local module=$RESOURCE_NAME
            if [[ ! $module =~ \/ ]]; then
                # If exists in cache.
                local LOCAL_FILE
                # shellcheck disable=SC2086
                LOCAL_FILE="$(find $X_BASH_SRC_PATH/*/$RESOURCE_NAME 2>/dev/null | head -n 1)"
                if [ -r "$LOCAL_FILE" ]; then
                    echo "INFO: Using local file $LOCAL_FILE" >&2
                    ${SRC_LOADER:-source} "$LOCAL_FILE" "$@"
                    return 0
                fi

                local index_file="$X_BASH_SRC_PATH/index"
                # Trigger update even if index file not modified more than one hour
                if [[ ! $(find "$index_file" -mtime +1h -print) ]]; then
                    echo "INFO: Rebuilding $index_file" >&2
                    CACHE="$index_file" @src.curl "$X_BASH_SRC_PATH_WEB_URL/index"
                fi

                module="$(grep "$RESOURCE_NAME" "$index_file" | head -n 1)"
                [ -z "$module" ] && {
                    echo "ERROR: $RESOURCE_NAME NOT found" >&2
                    return 1
                }
                echo "INFO: Using $module" >&2
            fi

            URL="$X_BASH_SRC_PATH_WEB_URL/$module"
            TGT="$X_BASH_SRC_PATH/$module"
        fi

        if ! CACHE="$TGT" @src.curl "$URL"; then
            echo "ERROR: Fail to load $RESOURCE_NAME due to network error or other. Do you want to load std/$RESOURCE_NAME?" >&2
            return 1
        fi
        
        ${SRC_LOADER:-source} "$TGT" "$@"
    } 2> >(grep -E "${LOG_FILTER:-^ERROR}" >&2)
    # } 2> >(grep -E "${LOG_FILTER:-^(ERROR)|(INFO)}" >&2)
fi
