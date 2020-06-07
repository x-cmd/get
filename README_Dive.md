## 原理 

1. 引用库前的准备工作：boot

在使用`@src`引用本库提供的便利之前，你需要先引用`boot`这个文件，该文件内定义了`@src`

**采用`x-cmd`**

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
