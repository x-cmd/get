list.create(){
    eval "export $1=()"
    export O=$1
}

list.free(){
    eval "unset $O"
}

list.size(){
    local code="echo \${#$O[@]}"
    eval "$code"
}

list.last_idx(){
    echo $(($(list.size) - 1))
}

list.first(){
    if [ $(list.size) -gt 0 ]; then
        eval "echo \${$O[0]}"
        return 0
    fi

    if [ -z "$1" ]; then
        echo "No element to shift" >&2
        return 1
    fi

    echo $1
    return 0
}

list.shift(){
    if [ $(list.size) -le 0 ]; then
        echo "No element to shift" >&2
        return 1
    fi
    eval "$O=( \"\${$O[@]:1}\" )"
}

list.push(){
    for i in "$@"; do
        # echo $i
        O=$O list._push $i
    done
}

list._push(){
    local code="$O[\${#$O[*]}]=$1"
    # echo $code
    eval "$code"
}

list.pop(){
    local last_idx=$(list.last_idx)
    [ $? -ne 0 ] && return $?
    eval "$O=( \"\${$O[@]:0:$last_idx}\" )"
}

list.top(){
    local last_idx=$(list.last_idx)
    [ $? -ne 0 ] && return $?

    if [ $last_idx -ge 0 ]; then
        eval "echo \${$O[$last_idx]}"
        return 0
    fi

    if [ -z "$1" ]; then 
        echo "No element to pop" >&2
        return 1
    fi
    echo $1
}

list.print(){
    eval "echo \"\${$O[*]}\""
}