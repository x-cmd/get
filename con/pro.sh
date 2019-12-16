#!/bin/bash
FIFO=/tmp/fifo1
n=0
[ ! -e "$FIFO" ] && mkfifo $FIFO

# while [[ true ]]; do
#     n=$((n+1))
#     MESSAGE="message $n"
#     echo "$MESSAGE" >$FIFO
# done

for i in `seq 10`; do
    echo "message $i" >$FIFO
done
