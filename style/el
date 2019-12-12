# Setting bash style
# Reference: https://blog.gtwang.org/linux/how-to-make-a-fancy-and-useful-bash-prompt-in-linux-2/

# TODO: Use no tput no CLR_* variables in this file.

export CLR_BOLD="\[$(tput bold)\]"
export CLR_UNDERLINE="\[$(tput smul)\]" # \e[0;4m
export CLD_DIM="\[$(tput dim)\]"

export CLR_RED="\[$(tput setaf 1)\]"
export CLR_GREEN="\[$(tput setaf 2)\]"
export CLR_YELLOW="\[$(tput setaf 3)\]"
export CLR_BLUE="\[$(tput setaf 4)\]"
export CLR_0="\[$(tput sgr0)\]"

export PS1="\n$CLR_BOLD$CLR_YELLOW\$(date +%H:%M:%S)  $CLR_0$CLR_UNDERLINE$CLR_GREEN\$PWD$CLR_0  $CLR_BOLD$CLR_BLUE\u$CLR_0$CLR_BLUE\$([ -z "$PSHOSTNAME" ] || echo "@$PSHOSTNAME")  $CLR_RED$CLR_BOLD\$(git rev-parse --abbrev-ref HEAD 2>/dev/null)\n$CLR_RED\$$CLR_0 "
export PS2='\[\e[0;32m\]~>\[\e[m\]'

export PS3='Enter your choices:'
export PS4='\[\e[0;37;44m\]$LINENO@$0:\[\e[m\]'
#export PROMPT_COMMAND="echo -n [$(date +%H:%M:%S)]"