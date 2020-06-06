# x-bash

A light-weight handy bash library to make life easier.

## 安装

### 先睹为快：引用并且安装在`~/.bashrc`上

```bash
eval "$(curl https://x-bash.github.io/install)"
```

1. 安装最新版的x-cmd（关于`x-cmd`的介绍）
2. 采用x-cmd的方式来安装`x-bash`

### 不采用x-cmd的安装方式

```bash
eval "$(curl https://x-bash.github.io/light-install)"
```

## 引用库前的准备工作：boot

在使用`@src`引用本库提供的便利之前，你需要先引用`boot`这个文件，该文件内定义了`@src`

### 采用`x-cmd`

安装`x-cmd`：`x || eval "$(curl https://x-cmd.github.io/install)"`

```bash
eval "$(x @bash/boot)"

@src std/str
strmd5=$(str.md5 "hello world")
```

### 不采用`x-cmd`

如果你不喜欢x，有两种方式：

方式一：可以采用最干净的方式，每次运行都需要下载boot

```bash
eval "$(curl https://x-bash.github.io/boot)"

@src std/str
strmd5=$(str.md5 "hello world")
```

方式二：按需下载boot，自动缓存

```bash
D="$HOME/.x-cmd.com/x-bash/boot" eval '[ -f $D ] || (mkdir -p $(dirname $D) && curl "https://x-bash.github.io/boot" >$D) && source $D'

@src std/str
strmd5=$(str.md5 "hello world")
```

首行代码完成如下操作：

1. 检测本地`"$HOME/.x-cmd.com/x-bash/boot"`是否存在
    - 如果该文件不存在，创建文件夹，并从`https://x-bash.github.io/boot`下载
2. 本地加载`"$HOME/.x-cmd.com/x-bash/boot"`

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

## 开发者指南

Setting the X_BASH_SRC_PATH variable. So the `@src` will reference the files `$X_BASH_SRC_PATH` first.

```bash
# original dev.sh

export X_BASH_SRC_PATH=$(pwd)
```
