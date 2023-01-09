#! /usr/bin/env bash

# RELOAD=1 source "./boot"

xrc std/assert
xrc.which std/assert

bash <<A
set -o errexit

command -v "assert" && echo "ERROR: EXPECT NOT contains assert"
xrc std/assert
command -v "assert" || echo "ERROR: EXPECT assert module loading"

assert.file.readable "$(xrc.which std/assert)"
echo "$X_BASH_SRC_PATH/index"

xrc.update
echo assert.file.readable "$X_BASH_SRC_PATH/index"
assert.file.readable "$X_BASH_SRC_PATH/index"

rm "$X_BASH_SRC_PATH/index"
assert.nofile "$X_BASH_SRC_PATH/index"

xrc.update
assert.file.readable "$X_BASH_SRC_PATH/index"
A

[ $? -ne 0 ] && echo "ERROR"

rm -rf ./std


