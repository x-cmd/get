git remote remove gitee 2>/dev/null
git remote add gitee git@gitee.com:x-bash/x-bash.git
git push gitee

git remote remove github 2>/dev/null
git remote add github git@github.com:x-bash/x-bash.github.io.git
git push github

git remote remove gitee-mirror 2>/dev/null
git remote add gitee-mirror git@gitee.com:x-bash/x-bash-boot-mirror.git
git push gitee-mirror

git push origin