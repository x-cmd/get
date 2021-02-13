
# Reference: https://hackthology.com/a-job-queue-in-bash.html

# Reference: https://blog.garage-coding.com/2016/02/05/bash-fifo-jobqueue.html#sec-3-1-1

queue.create(){
    queue="${TMPDIR}concurrent.$RANDOM"
    mkdir -p $queue
    mkfifo $queue/fifo
    queue.set_max $1
    queue.daemon $queue & # 1>$queue/stdout 2>$queue/stderr &
}

queue.daemon(){
    # cat ${queue:?"env queue is empty"}/fifo | while read command; do
    while read -r command; do
        echo "Start: Get Command: $command" >&2
        if [ "$command" = "exit" ]; then
            echo "Deamon ends" >&2
            return 0
        fi

        while :; do
            local current_consumer_num=$(( $(ls $queue | wc -l ) - 4  ))
            local max=$(cat $queue/max)

            echo "a" $current_consumer_num >&2
            echo "b" $max >&2

            if [ "$current_consumer_num" -lt "$max" ]; then
                break
            fi
            echo "continue sleep" >&2
            sleep 3s
        done

        echo "Get Command: $command" >&2
        # (
        #     local NAME=$queue/$RANDOM
        #     touch $NAME
        #     eval "$command"
        #     rm $NAME
        # ) &
        # ) 1>>$queue/stdout 2>>$queue/stderr &
    done <${queue:?"env queue is empty"}/fifo
}

queue.set_max(){
    local max=${1:-3}
    echo $max >${queue:?"env queue is empty"}/max
}

queue.add_job(){
    if [ ! -e ${queue:?"env queue is empty"}/fifo ]; then
        echo "$queue/fifo doesn't exists. It is provided in first argument." >&2
        return 1
    fi
    echo "$*" >$queue/fifo
}

queue.shutdown(){
    echo "exit" >${queue:?"env queue is empty"}/fifo
    rm $queue/*
    rm -f $queue
}

queue.clear(){
    rm -rf "${TMPDIR}concurrent.*"
}
