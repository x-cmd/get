#!/bin/bash

FIFO=/tmp/fifo1
a=1
b=2
while [[ true ]]; do
    [ ! -e $FIFO ] && {
        sleep 3s
        continue
    }

    while read s <$FIFO; do
        echo "num" $b
        echo "New Msg $a: $s"
        sleep 1s
    done
done


