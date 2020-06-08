# shellcheck shell=bash
# [ -z "$RELOAD" ] && [ -n "$X_BASH_SRC_PATH" ] && return 0 

# Cannot use return or exit. This seems to be the only way.
if [ -n "$RELOAD" ] || [ -z "$X_BASH_SRC_PATH" ]; then

    echo "Initialize the boot enviroment"
    X_BASH_SRC_PATH=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
    X_BASH_SRC_PATH_WEB_URL="https://x-bash.github.io"

    @src.reload(){
        # shellcheck disable=SC1090
        RELOAD=1 source "${1:?Please provide boot file path}"
    }

    @src.curl(){
        # TODO: using variable to optimize
        # if [ -n "$CURL" ]; then
        #     return
        # fi

        if curl --version 1>/dev/null 2>&1; then
            curl "$1" 2>/dev/null
            return
        fi

        if wget --help 1>/dev/null 2>&1; then
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

    @src(){
        while [ $# -gt 0 ]; do
            local RESOURCE_NAME=$1

            if [[ "$RESOURCE_NAME" =~ ^http:// ]] || [[ "$RESOURCE_NAME" =~ ^https:// ]]; then
                local URL="$RESOURCE_NAME"
                local TGT
                TGT="$X_BASH_SRC_PATH/$(echo -n "$URL" | base64)"
            else
                local module=$RESOURCE_NAME

                if [[ ! $module =~ \/ ]]; then
                    # Strategy: if there is local file in cache. Use it.
                    # Bug...
                    local LOCAL_FILE
                    # shellcheck disable=SC2086
                    LOCAL_FILE="$(find $X_BASH_SRC_PATH/*/$RESOURCE_NAME 2>/dev/null | head -n 1)"
                    [ -r "$LOCAL_FILE" ] && {
                        echo "INFO: Using local file $LOCAL_FILE" >&2
                        ${X_CMD_COM_PARAM_CMD:-source} "$LOCAL_FILE"
                        return 0
                    }

                    local index_file="$X_BASH_SRC_PATH/index"
                    # File not exists or file is not modified more than one hour
                    # If not found
                    if [ ! -r "$index_file" ] || [[ $(find "$index_file" -mtime +1h -print) ]]; then
                        echo "INFO: Rebuilding $index_file" >&2
                        mkdir -p "$(dirname "$index_file")"
                        local content
                        content="$(src.curl "$X_BASH_SRC_PATH_WEB_URL/index" 2>/dev/null)"
                        (echo "$content" | grep "std/str" 1>/dev/null) && echo "$content" >"$index_file"
                    fi
                    module="$(grep "$RESOURCE_NAME" "$index_file" | head -n 1)"
                    [ -z "$module" ] && {
                        echo "ERROR: $RESOURCE_NAME not found" >&2
                        return 1
                    }
                    echo "INFO: Using $module" >&2
                fi

                local URL="$X_BASH_SRC_PATH_WEB_URL/$module"
                local TGT="$X_BASH_SRC_PATH/$module"
            fi

            if [ ! -e "$TGT" ]; then
                mkdir -p "$(dirname "$TGT")"

                local content
                content="$(src.curl "$URL" 2>/dev/null)"

                (echo "$content" | grep "shellcheck" 1>/dev/null) || {
                    echo "ERROR: Failed to load $RESOURCE_NAME due to network error or other. Do you want to load std/$RESOURCE_NAME?" >&2
                    echo "Content contains no 'shellcheck': $URL" >&2
                    return 1
                }

                echo "$content" >"$TGT"
            fi
            
            ${X_CMD_COM_PARAM_CMD:-source} "$TGT"
            echo "INFO: Module from $TGT" >&2
            shift
        done
    } 2> >(grep -E "${LOG_FILTER:-^ERROR}" >&2)
    # } 2> >(grep -E "${LOG_FILTER:-^(ERROR)|(INFO)}" >&2)

    @src.clear-cache(){
        rm -rf "$X_BASH_SRC_PATH"
    }

    # Consider removing this function, @x will be much better
    @run(){
        X_CMD_COM_PARAM_CMD=bash @src "$*"
    }

fi
