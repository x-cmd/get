# @use std/list
# @use std/utils

jobqueue.create(){
    eval "export ${1:?Provide queue name}=()"
    eval "export $1_max=${2:-6}"
    export O=$1
}

jobqueue.set_max(){
    eval "export ${O:?Provide queue size}_max=3"
}

jobqueue.get_max(){
    eval "echo \$${O:?Provide queue size}_max"
}

jobqueue.offer(){
    local cur=$(jobs | wc -l)
    local max=$(jobqueue.get_max)
    if [ $cur -le $max ]; then
        (eval "$@") 1>&1 2>&2 &
        return 0
    fi
    return 1
}

jobqueue.put(){
    until jobqueue.offer $@; do
        sleep 3s;
    done
}

jobqueue.clear(){
    eval "export $1=()"
}

jobqueue.test.ping(){
    jobqueue.create queue4ping 100
    for ip in ${1:?Provide ip range like 192.168.6}.{1..255}; do 
        echo $ip;
        jobqueue.put "ping -c 2 $ip && echo $ip >>available.ip.list"; 
    done
    jobqueue.clear
}

