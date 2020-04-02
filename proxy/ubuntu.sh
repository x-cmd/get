# shellcheck shell=bash

# TODO: get ubuntu versio

setup.apt.src(){

    # TODO: Add whether backup
    
    local s=${1:-ali}
    s="$(echo "$s" | tr "[:upper:]" "[:lower:]")"
    local CONTENT=""
    case "$s" in
        ali) CONTENT='deb http://mirrors.aliyun.com/ubuntu/ bionic main restricted universe multiverse
deb http://mirrors.aliyun.com/ubuntu/ bionic-security main restricted universe multiverse
deb http://mirrors.aliyun.com/ubuntu/ bionic-updates main restricted universe multiverse
deb http://mirrors.aliyun.com/ubuntu/ bionic-proposed main restricted universe multiverse
deb http://mirrors.aliyun.com/ubuntu/ bionic-backports main restricted universe multiverse
deb-src http://mirrors.aliyun.com/ubuntu/ bionic main restricted universe multiverse
deb-src http://mirrors.aliyun.com/ubuntu/ bionic-security main restricted universe multiverse
deb-src http://mirrors.aliyun.com/ubuntu/ bionic-updates main restricted universe multiverse
deb-src http://mirrors.aliyun.com/ubuntu/ bionic-proposed main restricted universe multiverse
deb-src http://mirrors.aliyun.com/ubuntu/ bionic-backports main restricted universe multiverse';;
    esac

    cp /etc/apt/source.list{,.bak} && \
    (
        echo "$CONTENT"
        echo ""
        cat /etc/apt/source.list.bak
    ) > /etc/apt/source.list
}

setup.apt.src "$*"

