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

    @srcs(){
        for i in "$@"; do @src "$i"; done
    }

    # Consider removing this function, @x will be much better
    @src.bash(){
        SRC_LOADER=bash @src "$@"
    }

    # rename http.cat
    @src.curl(){
        # TODO: using variable to optimize
        local webfile=${1:?Provide url of web file}

        if command -v x 1>/dev/null 2>&1; then
            x cat "$webfile" 2>/dev/null
            return  # If fail, return code is 2
        fi

        if curl --version 1>/dev/null 2>&1; then
            curl --fail "$webfile" 2>/dev/null   # If fail, return code is 28
            return
        fi

        # busybox and alpine is with wget but without curl. But both are without bash and tls by default
        if wget --help 1>/dev/null 2>&1; then
            wget -qO - "$webfile"   # If fail, return code is 8
            return
        fi

        echo "No other Command for HTTP-GET" >&2
        return 1
    }

    # rename http.cat.with_cache
    @src.curl_with_cache(){
        local URL=${1:?Provide original url} TGT=${2:?Provide cache path}
        if [ ! -e "$TGT" ]; then
            mkdir -p "$(dirname "$TGT")"
            local CONTENT
            CONTENT=$(@src.curl "$URL" 2>/dev/null) && echo "$CONTENT" > "$TGT"
        fi
    }

    @src(){
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
                    @src.curl_with_cache "$X_BASH_SRC_PATH_WEB_URL/index" "$index_file"
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

        if ! @src.curl_with_cache "$URL" "$TGT"; then
            echo "ERROR: Fail to load $RESOURCE_NAME due to network error or other. Do you want to load std/$RESOURCE_NAME?" >&2
            return 1
        fi
        
        ${SRC_LOADER:-source} "$TGT" "$@"
    } 2> >(grep -E "${LOG_FILTER:-^ERROR}" >&2)
    # } 2> >(grep -E "${LOG_FILTER:-^(ERROR)|(INFO)}" >&2)
fi
