FROM debian:latest

RUN apt update && apt install curl -y && apt clean

ADD ./boot /boot
ADD ./install /install
# RUN eval "$(curl https://x-bash.gitee.io/install)"
RUN eval "$(cat /install)"
ENV ENV=/root/.x-cmd.com/x-bash/boot

