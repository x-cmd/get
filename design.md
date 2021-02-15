# 文件夹位置

## 对于脚本

```bash
. ~/.boot.x-cmd

PATH=$HOME/.x-cmd/$PATH

eval "$(curl https://get.x-cmd.com)"

# script
eval "$(cat $HOME/.x-cmd/boot 2>/dev/null)" || eval "$(curl https://get.x-cmd.com/script)"




eval "$(curl https://get.x-cmd.com/global)"
```

