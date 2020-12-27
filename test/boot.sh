#! /usr/bin/env bash

RELOAD=1 source "./boot"

xrc std/assert
xrc.which std/assert

bash <<A
command -v "assert" && echo "ERROR: EXPECT NOT contains assert"

xrc std/assert

# assert.file "$(xrc.which std/assert)"

# assert.exists "$X_BASH_SRC_PATH/index"
rm "$X_BASH_SRC_PATH/index"
xrc.update
# assert.exists "$X_BASH_SRC_PATH/index"
A

echo $?

rm -rf ./std
