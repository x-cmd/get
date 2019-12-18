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

test-work(){
    trap "echo hi; return 0" SIGUSR1

    echo fire 30
    kill -s SIGUSR1 $$
}

test.suite(){
    echo $1
    while read p; do
        :
    done

    echo "tear up"
}

# @final(){
#     local latest_code=$(trap -p return)
#     # Smart as I, using eval to avoid the real return statement being invoked when this function ends.
#     local code="eval \"trap \\\"$1
#     ${latest_code:-trap return}
#     \\\" return\"
#     "
#     # echo "$code"
#     trap "$code" return
# }

# @catch(){
#     local latest_code=$(trap -p ERR)
#     echo after latest
#     local code="$1; ${latest_code:-trap ERR}"
#     # echo CATCH-CODE "$code"
#     trap "$code" ERR
# }

@catch-final(){
    # setup catch
    local latest_err=$(trap -p ERR)
    local catch_code="
        __RET_CODE=$?
        $1
        ${latest_err:-trap ERR}
        return $__RET_CODE
    "
    # echo "$catch_code"
    trap "$catch_code" ERR

    # setup finally
    local latest_return=$(trap -p return)
    # Smart as I, using eval to avoid the real return statement being invoked when this function ends.
    local final_code="eval \"trap \\\"trap ERR
        $2
        ${latest_err:-trap ERR}
        ${latest_return:-trap return}
    \\\" return\"
    "
    # echo "$final_code"
    trap "$final_code" return
}


list.test(){
    list.create testwork

    @catch-final '
        echo catch error: codeline $LINENO
    ' '
        echo before free
        O=testwork list.free
        aaa # Wrong command
        echo after free
    '

    list.push a b c d e f

    test.expect $(list.first default) "a"
    test.expect $(list.top default) "f"

    test.expect $(list.first default) "a1"
    test.expect $(list.first default) "a2"

    list.shift
    test.expect $(list.first default) "b"
    test.expect $(list.top default) "f"

    list.pop
    test.expect $(list.first default) "b"
    test.expect $(list.top default) "e"
}

