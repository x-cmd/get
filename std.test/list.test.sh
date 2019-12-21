eval "$(curl https://x-bash.github.io/boot)"

@src std/list std/utils std/test

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

    # test.expect $(list.first default) "a1"
    # test.expect $(list.first default) "a2"

    list.shift
    test.expect $(list.first default) "b"
    test.expect $(list.top default) "f"

    list.pop
    test.expect $(list.first default) "b"
    test.expect $(list.top default) "e"
}