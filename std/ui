#! /usr/env/bin bash

# For some advancing usage. Refer to http://linuxcommand.org/lc3_adv_tput.php

# relis on tput command

COLP() {
  tput setaf "$1"
  shift
  echo -ne "$@"
  tput sgr0
}

COLBP() {
  tput setaf "$1"
  tput bold
  shift
  echo -ne "$@"
  tput sgr0
}

ERROR() { COLBP 1 "$@"; }
WARN() { COLBP 3 "$@"; }
INFO() { COLBP 2 "$@"; }
FINE() { COLBP 4 "$@"; }

@log() { COLBP 4 "$@"; }
@warn() { COLBP 3 "$@"; }
@err() { COLBP 1 "$@"; }
# @info() { COLBP 2 "$@"; }


@echop(){
  [ '' != "$FG" ] && tput setaf "$FG"
  [ '' != "$BG" ] && tput setab "$BG"
  tput bold
  echo "$@"
  tput sgr0
}

## tput screen facility

ui.save_screen() { tput smcup; }
ui.restore_screen(){ tput rmcup; }
ui.screen() {
  ui.save_screen
  eval "$@"
  ui.restore_screen
}

@screen(){ ui.screen "$@"; }

ui.banner() {
  # echo -n ${BG:-$UI_BG_BLUE}${FG:-$UI_FG_WHITE}
  local FG=$(tput setaf ${1:-$UI_WHITE})
  local BG=$(tput setab ${2:-$UI_BLUE})
  echo "$FG$BG"
  clear
  cat
}

UI_BLACK=0
UI_RED=1
UI_GREEN=2
UI_YELLOW=3
UI_BLUE=4
UI_MAGNETA=5
UI_CYAN=6
UI_BLACK=7

UI_BG_BLUE="$(tput setab 4)"
UI_BG_BLACK="$(tput setab 0)"
UI_FG_GREEN="$(tput setaf 2)"
UI_FG_WHITE="$(tput setaf 7)"

RUN_CMD_WITH_INFO() {
  local INFO=$1
  shift 1
  INFO "======================\n"
  INFO "$INFO\n"
  INFO "======================\n"
  eval "$@"
}

RUN_CMD_WITH_STEP() {
  local STEP=$1
  local INFO="STEP $STEP: $2"
  shift 2
  RUN_CMD_WITH_INFO "$INFO" "$@"
}

# x con "**Hello World**"
# x con "# Hello"
# x con "**hello**"
# x con "~hello~"
# x con ""

