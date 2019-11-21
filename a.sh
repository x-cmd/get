#! /usr/bin/env bash

eval "$(x @bash/std/str)"

str.join "," "a" "b" "c"


eval "$(x @bash/aws)"
@std aws str
# @import @bash/aws

aws.ec2.create
