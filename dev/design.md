# design

1. 提供一个轻量级的bash库，来显著提升bash的可读性
2. 支持云cache
3. 必要时，可以将@src替换成相应的库函数，打包成可用的bash函数；采用

```bash
https://x-bash.github.io
提供bash基本库
```

```bash
eval "$(curl https://x-bash.github.io)"

# str处理
@src str
str.trim_left "  hello"

# math处理
@src math

# json object处理
@src json

@src net

@src ui

@src x

@src crypto

x install network
```

```bash

```

## 规范

1. 多人合作
2. 采用`shellcheck`进行规范检查
3. 逐步增加函数功能

## 设计理念

bash不适合嵌套函数的使用，这样会带来难以定位的bug。
我们认为bash的风格应该是，尽量扁平，事实上，bash这么多年没有函数库是正常的，因为不可避免会带来潜在的复杂性。

本函数库的设计，目标是尽量提供简单明了的api方案。

本函数库尽量与原生bash的风格保持一致，但在此之上，我们略有变化；例如我们大量使用O环境变量

```bash
# Python Style
list.create students teachers
O=students list.push Chollet Marcus LeCun
O=teachers list.push Richard Steward William
O=stduents list.print
O=teachers list.print

# C Style
list.create students teachers
list.push students Chollet Marcus LeCun
list.push teachers Richard Steward William
list.print stduents
list.print teachers

# OO Style: Problem is, adding too many functions in bash
list.create students teachers
students.push Chollet Marcus LeCun
teachers.push Richard Steward William
students.print
teaches.print

list.new students teachers
O=students list.push Chollet Marcus LeCun
O=teachers list.push Richard Steward William
O=stduents list.print
O=teachers list.print
```

