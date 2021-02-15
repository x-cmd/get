# 文件夹位置

## 对于脚本

```bash
. ~/.boot.x-cmd

PATH=$HOME/.x-cmd/$PATH

# Installation in user space
eval "$(curl https://get.x-cmd.com)"

# script
eval "$(cat $HOME/.x-cmd/boot 2>/dev/null)" || eval "$(curl https://get.x-cmd.com/script)"
```

# 有待设计的全局模式

```bash

# If sudo ?
# Change it in /usr/bin

cat >/bin/xrc <<A
if [ -z "$X_CMD_PATH" ]; then
    if [ -f "$HOME/.x-cmd/boot" ]; then
        eval "$(cat "$HOME/.x-cmd/boot")"
    else
        eval "$(curl https://get.x-cmd.com)"
    fi
fi
A

ln -s /bin/x /bin/xrc

```

