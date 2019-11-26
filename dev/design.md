# design

1. 提供一个轻量级的bash库，来显著提升bash的可读性
2. 支持云cache
3. 必要时，可以将@use替换成相应的库函数，打包成可用的bash函数；采用


```bash
https://x-bash.github.io
提供bash基本库
```



```bash
eval "$(curl https://x-bash.github.io)"

# str处理
@use str
str.trim_left "  hello"

# math处理
@use math

# json object处理
@use json

@use net

@use ui

@use x

@use crypto

x install network
```

```bash

```

## 规范

1. 多人合作
2. 采用`shellcheck`进行规范检查
3. 逐步增加函数功能

