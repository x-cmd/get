# Design of catch and final

我们在这个领域花了相当多的时间

我们必须要从以下角度出发：

1. 采用`subshell`方案不可取，因为，这些操作都无法影响直接影响当前shell的内存
2. 不能污染函数内的被调用者，和调用者

第一点，排除了一种简单的解决方案

解决第二点，依赖两点：

1. trap本身定义会影响函数的当前作用域和调用者，不会影响函数所调用的函数
2. 我们主要使用将原来的trap代码，在触发后或者正常返回的时候执行，这样保证函数返回后，在调用者那里不受影响。因此，在实现catch的时候，除了定义ERR事件，还要定义RETURN事件

RETURN事件的处理还有一个问题，如果封装在方法内。但尴尬之处是，这个定义的方法退出时，就会触发第一次调用。

还有一个点需要考虑，return的处理函数如果出错，会调用catch，这不合一般的习惯，所以我们临时禁止了ERR事件触发器。而catch的代码内如果出现ERR，ERR的触发函数会自动屏蔽ERR事件的发起，防止循环调用。

采用alias（而非function）来实现defer的最大理由是，我们要考虑读取调用者原有RETURN的代码，并在RETURN时使用；如果我们采用function来实现，在defer函数内，无法获取调用者的RETURN trap信息。

```bash
work(){ trap -p return; }
abc(){ trap "echo HI" return; work; }
abc
# work中的`trap -p return`无输出
```

例如，以下实现是错误的，"latest_return"无论如何都是空的

```bash
function @trap.return(){
    # setup finally
    local latest_return
    latest_return=$(trap -p return)
    # O="return-queue" list.push 
    # Smart as I, using eval to avoid the real return statement being invoked when this function ends.
    local final_code="eval \"
        $2
        ${latest_err:-trap ERR}
        ${latest_return:-trap return}
    \\\" return\"
    "
    # echo "$final_code"
     # shellcheck disable=SC2064
    trap "$final_code" return
}
```

正确的实现：

```bash
# shellcheck disable=SC2142
alias @trap.return='
if local _X_CMD_RETURN_STACK 2>/dev/null; then
    declare -F _eval_stack 1>/dev/null || _eval_stack(){
        for i in "${_X_CMD_RETURN_STACK[@]}"; do
            eval "$i"
        done
    }

    declare -F _add_stack 1>/dev/null || _add_stack(){
        _X_CMD_RETURN_STACK=(  "$1" "${_X_CMD_RETURN_STACK[@]}"  )
    }

    [ -z "$_X_CMD_RETURN_STACK" ] && {
        _X_CMD_RETURN_STACK=()
        local _LATEST_RETURN=$(trap -p RETURN)
        trap "
            _eval_stack
            ${_LATEST_RETURN:-trap RETURN}
        " RETURN
    }
else
    echo "Only available in function scope." >&2
fi

_add_stack'
```
