# 变量赋值

```bash
abc=$(str.upper hi)
local abc=$(str.upper hi)

L=abc str.upper "hi"
local abc; V=abc str.uupper "hi"

str.upper "hi" abc
local abc; str.upper "hi" abc
```

第二种和第三种方案都少采用一次子进程调用
但我们采用了第一种方案，因为更直观，而且与bash原生风格更匹配
