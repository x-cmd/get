# README dev

## 步骤1: Clone本Repo

## 步骤2: 执行install.local.sh

```bash
source install.local.sh
```

*install.local.sh内容如下：*

```bash
D="$HOME/.x-cmd.com/x-bash/boot"
mkdir -p $(dirname $D)
cp boot "$D"
source "$D"
```

## 步骤3: 尝试使用xrc等功能
