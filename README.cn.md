# x-bash

A light-weight handy bash library to make life easier.

x-cmd虽然是分离设计，但却能大大降低x-bash的复杂度，因此，在x-bash中，与x-cmd交互是第一选择。然而，我们仍然提供了轻便模式，从而满足部分想使用x-bash，但又不愿意使用x-cmd的用户。

安装`x-cmd`：`x || eval "$(curl https://x-cmd.github.io/install)"`

## 安装

```bash
curl https://x-bash.github.io/install 2>/dev/null | bash
```

这个命令将进行初次安装是基本初始化，x-bash将安装在你的用户空间内。

然后，你就可使用x-bash功能

```bash
@src std/str # 可以简写为 @src str
new_str=$(str.join , 1 2 3) # new_str的值为 1,2,3
strmd5=$(str.md5 "hello world")

@src std/list # 可以简写为 @src list
list.new repo_list
repo_list.push "std" "style" "cloud"
repo_list.print
```

### 如果你不想安装，亦可先睹为快

```bash
eval "$(curl https://x-bash.github.io/boot)"
```

然后，你可以运行上述的`@src std/str`等例子

这里介绍一下boot这个文件，该文件定义了`@src`这个核心函数。一旦定义了`@src`，就有了x-bash这个库了。

### 轻便安装模式：不采用x-cmd的安装方式

```bash
curl https://x-bash.github.io/install 2>/dev/null | LIGHT=LIGHT_MODE bash
```

## 引用其他模块

例如，引用aws模块

```bash
@src cloud/aws
@src cloud/ali
@src cloud/az
```

**模块一览表**

| module | description |
| --- | --- |
| std | 标准模块, 简写既能导入。例如 `@src str` == `@src std/str` |
| cloud | effective helpers scripts for major cloud provider |
| docker | using official docker images to setup facilities. Mostly using docker-stack |
| db | db efficiency improvement scripts |
| style | style to make bash prettier and easier to use |
| setup | setup scripts for common softwares |
| mirror | alternate source settings for python, apt, yum, etc. |

**注意：请勿直接引用具体模块**

部分的客户可能会直接引用这个模块

```bash
eval "$(curl https://x-bash.github.io/std/str)" # 或者 eval "$(x @bash/std/str)"
```

这样做可能会缺乏`@src`这个函数，要先引用boot的模块。如此一来，最佳实践是：

```bash
eval "$(x @bash/boot)" # 或者 eval "$(curl https://x-bash.github.io/boot)"
@src std/str
```

一般来说，x-cmd会在用户目录缓存，但如果你没有x-cmd，你只能用curl（没有缓存）

```bash
eval "$(curl https://x-bash.github.io/boot)"
@src std/str
```

如果你没有x-cmd，而且还需要缓存，小生还有一计,

```bash
D="$HOME/.x-cmd.com/x-bash/boot" eval '[ -f $D ] || (mkdir -p $(dirname $D) && curl "https://x-bash.github.io/boot" >$D) && source $D'

@src std/str
```

上述例子的首行代码完成如下操作：

1. 检测本地`"$HOME/.x-cmd.com/x-bash/boot"`是否存在
    - 如果该文件不存在，创建文件夹，并从`https://x-bash.github.io/boot`下载
2. 本地加载`"$HOME/.x-cmd.com/x-bash/boot"`

## 开发者指南

Setting the `X_BASH_SRC_PATH` variable. So the `@src` will reference the files `$X_BASH_SRC_PATH` first.

```bash
# original dev.sh

export X_BASH_SRC_PATH=$(pwd)
```

**采用离线方式运行**

这种方式，是针对bash打包。

```bash
x @bash/pack --entrypoint main.sh
```

此时，我们会生成一个新的folder main-bash

里面的目录结构：

```bash
- /
  - x-bash.x-cmder.com #生成新的目录包含所有x-bash的目录
  - main.with-x-bash-lib.sh
  - main.sh
  - 其他文件
```

`main.with-x-bash-lib.sh` 文件内容

```bash
## auto-generated
X_BASH_SRC_PATH=$(pwd)/x-bash.x-cmder.com
cd abc
bash main.sh "$@'
```

你对这个目录用你想打包的方式打包，运送到其他所需的环境，即可轻松而不需要网络运行。