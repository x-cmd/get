#! /usr/bin/env bash

RELOAD=1 source "./boot"

xrc std/assert
xrc.which std/assert

bash <<A
set -o errexit

command -v "assert" && echo "ERROR: EXPECT NOT contains assert"
xrc std/assert
command -v "assert" || echo "ERROR: EXPECT assert module loading"

# assert.file "$(xrc.which std/assert)"

# assert.exists "$X_BASH_SRC_PATH/index"
rm "$X_BASH_SRC_PATH/index"
# assert.not.exists "$X_BASH_SRC_PATH/index"
xrc.update
# assert.exists "$X_BASH_SRC_PATH/index"
A

[ $? -ne 0 ] && echo "ERROR"

rm -rf ./std
