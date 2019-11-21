#! /usr/env/bin bash

# For some advancing usage. Refer to http://linuxcommand.org/lc3_adv_tput.php
# 

COLP(){
  tput setaf "$1"
  shift
  echo -ne "$@"
  tput sgr0
}
export -f COLP

COLBP(){
  tput setaf "$1"
  tput bold
  shift
  echo -ne "$@"
  tput sgr0
}
export -f COLBP

ERROR(){ COLBP 1 "$@"; }
WARN() { COLBP 3 "$@"; }
INFO() { COLBP 2 "$@"; }
FINE() { COLBP 4 "$@"; }

export -f ERROR WARN INFO FINE

RUN_CMD_WITH_INFO(){
  local INFO=$1
  shift 1
  INFO "======================\n"
  INFO "$INFO\n"
  INFO "======================\n"
  eval "$@"
}

RUN_CMD_WITH_STEP(){
  local STEP=$1
  local INFO="STEP $STEP: $2"
  shift 2
  RUN_CMD_WITH_INFO "$INFO" "$@"
}

export -f RUN_CMD_WITH_INFO RUN_CMD_WITH_STEP
