---
layout: post
title:  "[Docker] server gave HTTP response to HTTPS client"
date:   2016-12-17 21:33:00 +0900
categories: docker
tags: docker hub insecure http https registry
---

[docker-registry](https://hub.docker.com/_/registry/)를 이용해서 local에 docker 이미지 배포가 필요할 때가 있다. (kubernetes를 연동한다던가..)

kubernetes는 이미지 위치만 설정해서 스스로 다운받기 때문에 다운받을 registry가 필요하다. [docker-hub](https://hub.docker.com/)를 이용해도 되지만, 테스트 중일 때는 로컬이나 테스트서버가 편하니까..

docker-registry를 이용해서 local에 배포를 하면 아래 같은 에러가 발생하면서 실패하는 경우가 있다.

```bash
$ docker push 0.0.0.0:5000/hellonode:v1
The push refers to a repository [0.0.0.0:5000/hellonode]
Get https://0.0.0.0:5000/v1/_ping: http: server gave HTTP response to HTTPS client
```

원인은 push 커맨드는 HTTPS로 진행되는데, 설치한 docker-registry가 HTTP만 지원해서 그렇다. push를 HTTP로 할 수 있도록 docker 설정을 변경해 주면 된다.

docker 설정을 열어서 `Insecure registries`에 HTTP로 사용할 registry 정보를 넣어주고, push push!!

![docker insecure registry config](/assets/img/2016-12-17-docker-http-response-to-https-client.png)

MacOS 용이 아닌 다른 버전은 요기([https://docs.docker.com/registry/insecure/](https://docs.docker.com/registry/insecure/))를 참조.

# 참고
- [https://github.com/docker/docker-registry/issues/936](https://github.com/docker/docker-registry/issues/936)
