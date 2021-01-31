FROM alpine:latest

RUN apk add curl

ENV ENV=/root/.x-cmd.com/x-bash/boot
RUN eval "$(curl https://x-bash.gitee.io/install)"

