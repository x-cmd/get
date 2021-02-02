FROM debian:latest

RUN apt update && apt install curl -y && apt clean

# https://askubuntu.com/questions/86139/what-is-the-dash-equivalent-for-bashrc
ENV ENV=/root/.x-cmd.com/x-bash/boot
RUN eval "$(curl https://x-bash.gitee.io/install)"

