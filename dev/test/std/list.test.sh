# eval "$(curl https://x-bash.github.io/boot)"

@src std/list std/utils std/test

list.test(){
    list.make testwork

    @catch-final '
        echo catch error: codeline $LINENO
    ' '
        echo before free
        O=testwork list.free
        aaa # Wrong command
        echo after free
    '

    list.push a b c d e f

    assert.eq $(list.first default) "a"
    assert.eq $(list.top default) "f"

    # assert.eq $(list.first default) "a1"
    # assert.eq $(list.first default) "a2"

    list.shift
    assert.eq $(list.first default) "b"
    assert.eq $(list.top default) "f"

    list.pop
    assert.eq $(list.first default) "b"
    assert.eq $(list.top default) "e"
}