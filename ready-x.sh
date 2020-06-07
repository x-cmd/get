command -v x && {
    x(){
        local INSTALL
        read -r -p "X command is not installed. Do you want to install?(Y/n)" INSTALL
        if [ "$INSTALL" = "n" ]; then
            echo "Installation abort."
            return 1
        fi
        curl https://edwinjhlee.github.io/x/install | bash

        x "$@"
    }
}