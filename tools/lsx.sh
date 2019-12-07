
COLP(){
  tput setaf "$1"
  shift
  echo -ne "$@"
  tput sgr0
}

COLBP(){
  tput setaf "$1"
  tput bold
#   tput smul
  shift
  echo -ne "$@"
  tput sgr0
}

# with find arguments -x md,sh
# ls shallow wrapper to improve the readability
lsx(){
    # Directory using blue
    # Not the current user files using yellow
    # Time Using More files
    # files more than 1MB using bold

    # while read -r l 
    # do
    #     echo $l;
    # done <<< $(ls -alh)

    ls -alh | while read -r l 
    do
        if [ "${l:0:1}" == "d" ]; then
            # tput smul
            COLBP 4 "$l\n";
        else
            COLP 7 "$l\n";
        fi
    done
}

lsx "$@"
