
boot.test(){
    local os os_list=(
        debian
        ubuntu
        centos
        busybox
    )

    local s="$(cat $1)"

    for os in "${os_list[@]}"; do
        echo "-------------------"
        echo "OS: $os"
        if docker run --rm "$os" bash --version 1>/dev/null 2>/dev/null; then
            docker run --rm -i "$os" /bin/bash
        else
            docker run --rm -i "$os" /bin/sh
        fi <<<"$s"
    done
}

