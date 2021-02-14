#! /usr/bin/env bash

docker build --no-cache -t xcmd/debian:latest -f debian.Dockerfile .
docker push xcmd/debian:latest

docker build --no-cache -t xcmd/debian -f debian.Dockerfile .
docker push xcmd/debian

docker build --no-cache -t xcmd/alpine:latest -f alpine.Dockerfile .
docker push xcmd/alpine:latest

docker build --no-cache -t xcmd/alpine -f alpine.Dockerfile .
docker push xcmd/alpine

