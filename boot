# shellcheck shell=bash
[ -z "$RELOAD" ] && [ -n "$_X_CMD_COM_X_BASH_BOOT_VERSION" ] && return 0

echo "Initialize the boot enviroment"
_X_CMD_COM_X_BASH_BOOT_VERSION=0.0.0

# TODO: Get rid of this function
@init.curl(){
    if [ -z "$CURL" ]; then
        return
    fi

    if curl --version 1>/dev/null 2>&1; then
        export CURL=curl
        return
    fi

    if command -v x 1>/dev/null 2>&1; then
        export CURL="x cat"
        return
    fi
    
    return 1
}


@src(){
    [ -n "$X_BASH_SRC_PATH" ] && {
        local FILE="$X_BASH_SRC_PATH"/$1
        #shellcheck disable=SC1090
        [ -r "$FILE" ] && source "$FILE" && return 0
    }

    @init.curl

    while [ $# -gt 0 ]; do
        local RESOURCE_NAME=$1

        if [[ "$RESOURCE_NAME" =~ ^http:// ]] || [[ "$RESOURCE_NAME" =~ ^https:// ]]; then
            local URL="$RESOURCE_NAME"
            local TGT
            TGT="$HOME/.x-cmd.com/x-bash/$(echo -n "$URL" | base64)"
        else
            local module=$RESOURCE_NAME

            if [[ ! $module =~ \/ ]]; then
                # Strategy: if there is local file in cache. Use it.
                # Bug...
                local LOCAL_FILE
                # shellcheck disable=SC2086
                LOCAL_FILE="$(find $HOME/.x-cmd.com/x-bash/*/$RESOURCE_NAME 2>/dev/null | head -n 1)"
                [ -r "$LOCAL_FILE" ] && {
                    echo "Try using local file: $LOCAL_FILE" >&2
                    ${X_CMD_COM_PARAM_CMD:-source} "$TGT"
                    return 0
                }

                local index_file="$HOME/.x-cmd.com/x-bash/index"
                # File not exists or file is not modified more than one hour
                # If not found
                if [ ! -r "$index_file" ] || [[ $(find "$index_file" -mtime +1h -print) ]]; then
                    mkdir -p "$(dirname "$index_file")"
                    local content
                    content="$($CURL "https://x-bash.github.io/index" 2>/dev/null)"
                    (echo "$content" | grep "std/str" 1>/dev/null) && echo "$content" >"$index_file"
                fi
                module="$(grep "$RESOURCE_NAME" "$index_file" | head -n 1)"
                [ -z "$module" ] && {
                    echo "$module not found" >&2
                    return 1
                }
                echo "Using $module" >&2
            fi

            local URL="https://x-bash.github.io/$module"
            local TGT="$HOME/.x-cmd.com/x-bash/$module"
        fi

        if [ ! -e "$TGT" ]; then
            mkdir -p "$(dirname "$TGT")"

            $CURL "$URL" >"$TGT" 2>/dev/null || return 1

            if grep ^\<\!DOCTYPE "$TGT" >/dev/null; then
                rm "$TGT"
                echo "Failed to load $RESOURCE_NAME, do you want to load std/$RESOURCE_NAME?" >&2
                return 1
            fi
        fi
        
        ${X_CMD_COM_PARAM_CMD:-source} "$TGT"
        shift
    done
}

@src.clear-cache(){
    rm -rf "$HOME/.x-cmd.com/x-bash"
}

# Consider removing this function, @x will be much better
@run(){
    X_CMD_COM_PARAM_CMD=bash @src "$*"
}
