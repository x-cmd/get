# shellcheck shell=sh

eval "$(
    D="$HOME/.x-cmd/boot"
    if [ ! -f "$D" ] && ! ( mkdir -p "$HOME/.x-cmd" && curl --fail https://sh.x-cmd.com/boot >"$D" 2>/dev/null ); then
        echo "Fail to download boot to local home directory." >&2;  
        return 1
    fi
    cat "$D"

    if [ "$BASH_VERSION" ]; then        CAN="$HOME/.bashrc"
       [ "$(uname)" = "Darwin" ]  &&    CAN="$CAN $HOME/.bash_profile"
    elif [ "$ZSH_VERSION" ]; then       CAN="$HOME/.zshrc"
    elif [ "$KSH_VERSION" ]; then       CAN="$HOME/.kshrc"
    else                                CAN="$HOME/.shinit"
    fi

    X_STR=". \"\$HOME/.x-cmd/boot\" 2>/dev/null || eval \"\$(curl https://get.x-cmd.com/script)\""
    IFS=" "
    for i in $CAN; do
        if grep -F "$X_STR" "$i" >/dev/null; then
            echo "[x-cmd]: Already installed in $i" >&2
        else
            echo "$X_STR" >> "$i"
            echo "[x-cmd]: Successfully Installed in: $i" >&2
        fi
    done
)"
