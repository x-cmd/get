#! /usr/bin/env bash

usage="Usage: foo [-r] [-O outfile] infile"

while getopts ro: opt 
do
    case "$opt" in
        r)  rflag=1;;
        o)  oflag=1; ofile=$OPTARG;;
        \?) echo "$usage"; exit 1;;
    esac
done

if [ $OPTIND -gt $# ]; then
    echo "Needs input file"
    echo "$usage"
    exit 2
fi

echo $OPTIND $#

shift $(( OPTIND - 1 ))
infile=$1

echo $rflag $oflag $ofile "infile: $infile"
