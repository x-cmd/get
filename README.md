# bash

## 安装

### 先睹为快：引用并且安装在`~/.bashrc`上

```bash
eval "$(curl https://x-bash.github.io/install)"
```

如果你是`x-cmd`用户

```bash
eval "$(x bash/boot)" && @run install
```

### 直接引用

```bash
eval "$(curl https://x-bash.github.io/boot)"
```

```bash
eval "$(x @bash/boot)"
```

### 配置`~/.bashrc`文件，实行自动加载boot

**使用脚本自动配置**

```bash
@run install
```

**手动配置`~/.bashrc`文件，自动加载**

在`~/.bashrc`后面加入：

```bash
D="$HOME/.x-cmd.com/x-bash/boot" eval '[ -f $D ] || (mkdir -p $(dirname $D) && curl "https://x-bash.github.io/boot" >$D) && source $D'
```

上述代码完成如下操作：

1. 检测本地`"$HOME/.x-cmd.com/x-bash/boot"`是否存在
    - 如果该文件不存在，创建文件夹，并从`https://x-bash.github.io/boot`下载
2. 本地加载`"$HOME/.x-cmd.com/x-bash/boot"`


## 引用库

```bash
eval "$(curl https://x-bash.github.io/boot)"
@std str # equivalent to '@src std/str'
@src std/ui
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
@src std/str
# 或者
@std str # 引用str模块
```

引用aws模块

```bash
@src cloud/aws
@src cloud/ali
@src cloud/az
```
