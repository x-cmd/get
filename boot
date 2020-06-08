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
        # shellcheck disable=SC1090
        source "$(@src.get_resource_filepath "$1")" "$@"
        echo "INFO: Module from $TGT" >&2
    }

    @srcs(){
        for i in "$@"; do @src "$i"; done
    }

    # Consider removing this function, @x will be much better
    @src.bash(){
        bash "$(@src.get_resource_filepath "$1")" "$@"
    }

    # rename http.cat
    @src.curl(){
        # TODO: using variable to optimize
        # if [ -n "$CURL" ]; then
        #     return
        # fi

        if curl --version 1>/dev/null 2>&1; then
            curl --fail "$1" 2>/dev/null
            return
        fi

        if wget --help 1>/dev/null 2>&1; then
            # TODO: provide.
            wget -qO - "$1"
            return
        fi

        if command -v x 1>/dev/null 2>&1; then
            x cat "$1" 2>/dev/null
            return
        fi
        
        # TODO: using nc command?
        echo "No Net Command for HTTP-GET" >&2
        return 1
    }

    # rename http.cat.with_cache
    @src.curl_with_cache(){
        local URL=${1:?Provide original url}
        local TGT=${2:?Provide cache path}
        if [ ! -e "$TGT" ]; then
            mkdir -p "$(dirname "$TGT")"
            src.curl "$URL" 2>/dev/null >"$TGT"
        fi
    }

    @src.get_resource_filepath(){
        local RESOURCE_NAME=${1:?Provide resource name}
        shift

        local URL TGT

        if [[ "$RESOURCE_NAME" =~ ^https?:// ]]; then
            URL="$RESOURCE_NAME"
            TGT="$X_BASH_SRC_PATH/BASE64-URL-$(echo -n "$URL" | base64)"
        else
            local module=$RESOURCE_NAME

            URL="$X_BASH_SRC_PATH_WEB_URL/$module"
            TGT="$X_BASH_SRC_PATH/$module"

            # Is it an alias, which contains no / ?
            if [[ ! $module =~ \/ ]]; then
                # If exists in cache.
                local LOCAL_FILE
                # shellcheck disable=SC2086
                LOCAL_FILE="$(find $X_BASH_SRC_PATH/*/$RESOURCE_NAME 2>/dev/null | head -n 1)"
                echo "!!!" $LOCAL_FILE
                if [ -r "$LOCAL_FILE" ]; then
                    echo "INFO: Using local file $LOCAL_FILE" >&2
                    echo "$LOCAL_FILE"
                    return 0
                fi

                local index_file="$X_BASH_SRC_PATH/index"
                # Trigger update even if index file not modified more than one hour
                if [[ $(find "$index_file" -mtime +1h -print) ]]; then
                    echo "INFO: Rebuilding $index_file" >&2
                    @src.curl_with_cache "$X_BASH_SRC_PATH_WEB_URL/index" "$index_file"
                fi

                module="$(grep "$RESOURCE_NAME" "$index_file" | head -n 1)"
                [ -z "$module" ] && {
                    echo "ERROR: $RESOURCE_NAME NOT found" >&2
                    return 1
                }
                echo "INFO: Using $module" >&2
            fi
        fi

        if @src.curl_with_cache "$URL" "$TGT"; then
            echo "$TGT"
        else
            echo "ERROR: Failed to load $RESOURCE_NAME due to network error or other. Do you want to load std/$RESOURCE_NAME?" >&2
        fi

    } 2> >(grep -E "${LOG_FILTER:-^ERROR}" >&2)
    # } 2> >(grep -E "${LOG_FILTER:-^(ERROR)|(INFO)}" >&2)
fi
