# 致开发者

```bash
X_BASH_DEBUG=boot   # 通过环境变量，开启DEBUG
RELOAD=1 . boot     # 用RELOAD变量，强制更新boot
```

## vscode problem

你的vscode的守护进程，将会记住第一个打开该workspace的bash命名空间。
举个例子：

例如你没有打开vscode，你当前的bash有xrc函数。
采用 `code ~/abc` 打开目录，此时code新建的terminal将有xrc函数。
如果此时你关闭code，但是code的守护进程还在。

你打开另一个空白的bash进程，该bash下没有xrc函数。执行`code ~/abc`打开目录，此时code新建的terminal仍将有xrc函数。

因为，此时打开的workspace仍然是code守护进程所记忆的上一次的bash命名空间。

## 测试环境

1. 采用docker创建干净linux容器，进行测试
2. 编写测试脚本，采用bash运行，观察相关函数是否已经导入。
