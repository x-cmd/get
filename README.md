# bash

## 安装

### 引用并且安装在`~/.bashrc`上

```bash
INSTALL=1 eval "$(curl https://x-bash.github.io/boot)"
```

### 直接引用

```bash
eval "$(curl https://x-bash.github.io/boot)"

# 如果需要设置启动bash时自动加载
@install_in_bashrc
```

### 手动配置`~/.bashrc`文件，自动加载

在`~/.bashrc`后面加入：

```bash
D="$HOME/.x-cmd.com/x-bash/boot" eval '[ -e $D ] || mkdir -p $(dirname $D) && curl "https://x-bash.github.io/boot" >$D && source $D'
```

上述代码完成如下操作：

1. 检测本地`"$HOME/.x-cmd.com/x-bash/boot"`是否存在
    - 如果该文件不存在，从`https://x-bash.github.io/boot`下载
2. 本地加载`"$HOME/.x-cmd.com/x-bash/boot"`


## 引用库

```bash
eval "$(curl https://x-bash.github.io/boot)"
@std str # equivalent to '@use std/str'
@use std/ui
```

最佳实践：(Not Implemented)

```bash
eval "$(x bash)"
或者
source $(x which bash)
```

## 引用其他模块

例如，引用str模块

```bash
@use std/str
# 或者
@std str # 引用str模块
```

引用aws模块

```bash
@use cloud/aws
@use cloud/ali
@use cloud/az
```
