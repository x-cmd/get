#! /usr/bin/env bash

dos.to.unix(){
    if [ $# -eq 0 ]; then
        sed -e 's/\r//'
    else
        sed -e 's/\r//' -i ${BAK:-""} "$@"
    fi
}

# refer https://en.wikipedia.org/wiki/Unix2dos
# refer https://www.cyberciti.biz/faq/howto-unix-linux-convert-dos-newlines-cr-lf-unix-text-format/
unix.to.dos(){
    if [ $# -eq 0 ]; then
        # test cat abc.txt | sed -e 's/$/\r/' | cat -vet -
        sed -e $'s/$/\r/'
    else
        # test cat abc.txt | sed -e 's/$/\r/' | cat -vet -
        # cat abc.txt | sed -e 's/$/\r/' | cat -vet -
        sed -e 's/$/\r/' -e "$ s/..$//g" -i ${BAK:-""} "$@"
        # sed -e 's/\r*$/\r/' -i ${BAK:-""} "$@"
        # sed -e "s/$/^M/" -i ${BAK:-""} "$@"
    fi
}

# USAGE 1, remove in file: remove_eol_space filepath
# USAGE 2, remove in file and backup with BAK as extensions: BAK='.bak' remove_eol_space filepath
# USAGE 3, remove and output to stdout: cat filepath | remove_eol_space
remove_eol_space(){
    if [ $# -eq 0 ]; then
        sed -e 's/[[:blank:]]*$//g'
    else
        sed -e 's/[[:blank:]]*$//g' -i ${BAK:-""} "$@"
    fi
}

