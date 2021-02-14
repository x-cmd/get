# shellcheck shell=bash

eval "$(
    
    # if command -v x 2>/dev/null 1>&2 && [ "$(x author 2>/dev/null)" = "edwinjhlee & lteam" ]; then
    #     echo "X is detected." >&2
    #     echo "First we have to check whether x-cmd is available." >&2
    #     # shellcheck disable=SC2016
    #     echo 'eval "$(x @bash/boot)"'
    # else
    #     # if [ -n "$LIGHT_MODE" ]; then
    #     echo "D=\"\$HOME/.x-cmd.com/x-bash/boot\" eval '[ -f \$D ] || (mkdir -p \$(dirname \$D) && curl \"https://x-bash.gitee.io/boot\" >\$D) && . \$D'"
    #     # else
    #     #     echo "Setting up x-bash in normal mode." >&2
    #     #     echo "x-cmd is NOT available. Install x-cmd from $X_CMD_URL/install" >&2
    #     #     curl "https://x-cmder.github.io/install" | bash
    #     #     # shellcheck disable=SC2016
    #     #     echo 'source "$(x which @bash/boot)"'
    #     # fi
    # fi
    echo "D=\"\$HOME/.x-cmd.com/x-bash/boot\" eval '[ -f \$D ] || (mkdir -p \$(dirname \$D) && curl \"https://x-bash.gitee.io/boot\" 2>/dev/null >\$D) && . \$D'" | 
    
    (
        read -r X_STR
        # MAC: both. Linux: .bashrc. Msys? Cygwin?
        # To supoort sh
        can="
            $HOME/.rc
            $HOME/.zshrc
            $HOME/.bashrc
            $HOME/.bash_profile
        "

        for i in $can; do
            if grep -F "$X_STR" "$i" >/dev/null; then
                echo "Already installed in $i" >&2
            else
                echo "$X_STR" >> "$i"
                echo "Successfully Installed in: $i" >&2
            fi
        done

        eval "$X_STR" 2>/dev/null

        if [ -n "$ALL" ]; then
            xrc job # Release the concurrent power with job module
            for i in $(curl https://x-bash.gitee.io/index 2>/dev/null); do
                # src the module concurrently. Concurrent number is 6.
                # if [[ $i == */* ]]; thens
                if str_regex "$i" "[^/]+/[^/]+"; then
                    job_put 6 xrc.which "$i" 1>/dev/null 2>/dev/null
                fi
            done
        fi

        echo "$X_STR"
    )
)"
