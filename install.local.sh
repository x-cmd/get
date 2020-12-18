# Usage: source install.local.sh

[ -r ./boot ] || {
    echo "./boot file NOT found." >&2
    return 1 || exit 1
}

D="$HOME/.x-cmd.com/x-bash/boot"
mkdir -p "$(dirname $D)"
cp boot "$D"
source "$D"
