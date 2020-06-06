# w

## trap 1

```bash
# 第一种情况：glob模式存在结果
# 第二种情况：glob模式不存在搜索结果
# 此时你会发现两种不同的输出，这是glob模式带来的二义性
echo $HOME/.x-cmd.com/x-bash/*/$RESOURCE_NAME
```
