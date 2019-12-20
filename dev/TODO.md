
# 20191121

1. checkout
2. 合并std文档，成一个文件，不需要依赖x或者curl
3. 采用

设计api函数的要点：

1. 与常用函数，习惯和理念是否产生冲突
2. 在大部分环境中，不需要太多的预装和依赖
3. 在大部分环境中，tab的自动补全可用；不需要额外依赖更多的工具

# Which is better


`@src` vs `@src`

`@src.cache.clear`

`@install_in_bashrc`
vs

`@install`, `@upgrade`, `@reload`

# 使用习惯

```bash
alias @std="@src std/"
@src std/str
@std str

@src cloud/ali
```

# 这个库的边界？

1. 提供str, ui, net, set, test
2. （见仁见智）提供基础的bash命令shortcut，例如`git，ali，azure`，用来加快运维速度


```bash
@src str

@std str
```

