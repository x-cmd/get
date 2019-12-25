
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

# 这个库的边界

其实不应该提供纯运行的函数，应该采用@run

1. 提供str, ui, net, set, test
2. （见仁见智）提供基础的bash命令shortcut，例如`git，ali，azure`，用来加快运维速度


```bash
@src str
```

```markdown

std/: 提供facility，基于bash的标准内建命令
    str: string operations

    list: using work
    map: provide map facility
    set: set facility
    fmap: filebased map facility
    fset: filebased set facility
    
    job: concurrent facility based upon jobs

    test: test facility
    ui: facility for ui elements
    utils: other utils


style/: 风格库

其他则是各种命令下的增强
cloud/:
    aws
    az
    ali

net/: facility for linux
    nmap: nmap命令基本封装
    ping: ping封装

db/:
    mongo: mongo client对数据进行备份和转移
    mysql
    postgres
```

| 采用std等树状分级 | 完全扁平化 |
| --- | --- |

树状分级 vs 扁平化

1. （树状）适合代码规模扩展，减少冲突可能
2. （扁平化）缩短输入字符数字，当文件很多时也能减少记忆复杂度
3. （树状）数目更多，更容易管理
4.  (树状) 如果分配得当，也能降低复杂度：std，cloud，net，style

归类：

std: str/job/ui/... bash relative optimization
cloud: aws/ali/az/gae/.../tencent/bae
net: http/nmap/ping/nessus/proxy/nginx
db: postgres/mysql/mongo
word: some file processing

# 案例

1. 编写x-bash自身的测试
2. 编写static-build测试套件，保证基本功能
3. 使用x-bash来实现基本云资源管理功能

## 参考

rebash

https://github.com/jandob/rebash#function-array_get_index

bash-oo-framwork

bash practise
https://cloud.tencent.com/developer/article/1157462

bash guide

http://mywiki.wooledge.org/BashGuide/Practices

