
# queue.create sample
# this=sample queue.push hi world
# this=sample queue.push

# queue.create sample
# T=sample queue.push hi world
# T=sample queue.push

# queue.create sample
# O=sample queue.push hi world
# O=sample queue.push

queue.create(){
    eval "export $1=()"
    export O=$1
}

queue.free(){
    eval "unset $O"
}

queue.size(){
    local code="echo \${#$O[@]}"
    eval "$code"
}

queue.last_idx(){
    echo $(($(queue.size) - 1))
}

queue.first(){
    if [ $(queue.size) -gt 0 ]; then
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

queue.shift(){
    if [ $(queue.size) -le 0 ]; then
        echo "No element to shift" >&2
        return 1
    fi
    eval "$O=( \"\${$O[@]:1}\" )"
}

queue.push(){
    for i in "$@"; do
        # echo $i
        O=$O queue._push $i
    done
}

queue._push(){
    local code="$O[\${#$O[*]}]=$1"
    # echo $code
    eval "$code"
}

queue.pop(){
    local last_idx=$(queue.last_idx)
    [ $? -ne 0 ] && return $?
    eval "$O=( \"\${$O[@]:0:$last_idx}\" )"
}

queue.top(){
    local last_idx=$(queue.last_idx)
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

queue.list(){
    eval "echo \"\${$O[*]}\""
}

# Not useful: https://linuxhint.com/bash_error_handling/

test.expect(){
    # echo Test EQ: $1 $2 >&2
    [ "$1" = "$2" ] && return 0
    echo "${3:-Exepct $2 but get $1}" >&2
    return 1
}

# defer

queue.test(){
    queue.create testwork
    queue.push a b c d e f

    {
        set -o errexit

        # test.expect $(queue.first default) "a"
        test.expect $(queue.first default) "a1"
        test.expect $(queue.first default) "a2"

        test.expect $(queue.first default) "a"
        test.expect $(queue.top default) "f"

        queue.shift
        test.expect $(queue.first default) "b"
        test.expect $(queue.top default) "f"

        queue.pop
        test.expect $(queue.first default) "b"
        test.expect $(queue.top default) "e"
    } || :
    # || {
    #     # Catch
    #     echo "Error Exit"
    #     echo "code" $?
    #     local CODE=$?
    #     # Finaly
    #     queue.free
    #     return $CODE
    # } && {
    #     # Finally
    #     echo "Free"
    #     queue.free
    #     return $CODE
    # }

    
}

