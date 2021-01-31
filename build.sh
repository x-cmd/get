
docker build -t xcmd/debian:latest -f debian.Dockerfile .
docker push xcmd/debian:latest

docker build -t xcmd/debian -f debian.Dockerfile .
docker push xcmd/debian

docker build -t xcmd/alpine:latest -f alpine.Dockerfile .
docker push xcmd/alpine:latest

docker build -t xcmd/alpine -f alpine.Dockerfile .
docker push xcmd/alpine

