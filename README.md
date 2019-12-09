# bash

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
