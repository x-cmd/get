FROM debian:latest

RUN apt update && apt install curl -y && apt clean

ENV ENV=/root/.x-cmd.com/x-bash/boot
RUN eval "$(curl https://x-bash.gitee.io/install)"

