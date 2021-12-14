---
layout: post
title:  "[Docker] Docker-in-Docker(dind) 환경에서 redis cluster 구성"
date:   2021-12-16 22:18:00 +0900
published: true
categories: [ docker ]
tags: [ docker, docker-compose, dind, docker in docker, redis, cluster, config, setting ]
---

Docker 환경에서 간편하게 redis cluster를 구성할 수 있는 [Grokzen/docker-redis-cluster](https://github.com/Grokzen/docker-redis-cluster)가 있다. 설정 자체도 많지 않아서 쉽게 사용할 수 있다.

```yaml
version: '3.8'
services:
  redis-cluster:
    image: grokzen/redis-cluster:6.2.1
    environment:
      IP: '0.0.0.0'             # https://github.com/Grokzen/docker-redis-cluster#important-for-mac-users
    ports:
      - '7000-7005:7000-7005'   # The cluster is 6 redis instances running with 3 master & 3 slaves, one slave for each master. They run on ports 7000 to 7005.
```


# macOS 사용자 필수 설정

환경변수 `IP`를 설정해 주어야 한다. 설정해 주지 않으면 `hostname`으로 사용되고, 접속이 안 된다.

```yaml
services:
  redis-cluster:
    environment:
      IP: '0.0.0.0'             # https://github.com/Grokzen/docker-redis-cluster#important-for-mac-users
```

현재 팀에서는 로컬(macOS)에서도 사용하고 GitLab CI/CD에서도 사용하기 때문에 `IP` 설정을 해주었다.


# Docker-in-Docker(dind) 환경

GitLab CI/CD에서 integration test를 동시에 여러개를 실행시키기 위해 테스트에 필요한 환경을 dind 형태로 사용한다. (참고: [Use the Docker executor with the Docker image (Docker-in-Docker)](https://docs.gitlab.com/ee/ci/docker/using_docker_build.html#use-the-docker-executor-with-the-docker-image-docker-in-docker))

이 경우 GitLab CI가 동작하는 docker에서 redis cluster로 접속이 어렵다. 접속할 때 seed node 정보로 접속을 하게 되고, 처음 접속을 하면 cluster의 node 정보를 얻어온다. 이 때 cluster는 자기 hostname을 반환해 주는데 dind 모드에서는 이 hostname으로 접속이 불가능 하다. 이 때 redis.conf 파일에 `cluster-announce-ip` 설정을 해주면 cluster node 정보를 얻어올 때 이 IP를 알려주게 된다. NAT 설정이 되어 있거나 port 포워딩이 된 경우 사용하는 설정들이다.

- cluster-announce-ip
- cluster-announce-port
- cluster-announce-tls-port
- cluster-announce-bus-port

그런데, [Grokzen/docker-redis-cluster](https://github.com/Grokzen/docker-redis-cluster)을 사용하다보니 이 `redis.conf` 파일을 설정하기가 어렵다. 이 docker 이미지는 [entrypoint](https://github.com/Grokzen/docker-redis-cluster/blob/master/docker-entrypoint.sh)에서 설정한 master node와 replica 개수를 통해서 자동으로 `redis.conf` 파일을 생성하고 있다. 따라서, 직접 `redis.conf` 파일을 외부에서 mount 시켜주면 파일이 리셋되기 때문에, `redis-cluster.tmpl` 파일을 수정해 주어야 한다.

```
# redis/redis-cluster.tmpl

bind ${BIND_ADDRESS}
port ${PORT}
cluster-enabled yes
cluster-config-file nodes.conf
cluster-node-timeout 5000
appendonly yes
dir /redis-data/${PORT}
cluster-announce-ip my-redis
```

마지막에 `cluster-announce-ip` 설정을 추가해 주고 이 파일을 mount해주면 된다.

```yaml
version: '3.8'

services:
  redis-cluster:
    image: grokzen/redis-cluster:6.2.1
    environment:
      IP: '0.0.0.0'             # https://github.com/Grokzen/docker-redis-cluster#important-for-mac-users
    ports:
      - '7000-7005:7000-7005'   # The cluster is 6 redis instances running with 3 master & 3 slaves, one slave for each master. They run on ports 7000 to 7005.
    volumes:
      - './redis/redis-cluster.tmpl:/redis-conf/redis-cluster.tmpl'
    extra_hosts:
      - 'my-redis:127.0.0.1'
```

이 때, [extra_hosts]({{ site.baseurl }}{% post_url 2021-12-15-docker-add-etc-hosts-in-docker-compose-file %}) 설정으로 `/etc/hosts`에 host를 추가해 주어야 하는데, custer를 구성하는 node들끼리 서로 연결할 때도 `cluster-announce-ip`를 사용하기 때문이다. 설정해 주지 않으면, 서로 연결을 맺지 못 하고 각 node들이 따로 놀고 있는 모습을 볼 수 있다.

이제 끝이다. GitLab CI/CD에서 `my-redis:7000`(또는 `7001`, `7002`, ..)으로 접속하면 된다.


# 참고

- [Grokzen/docker-redis-cluster](https://github.com/Grokzen/docker-redis-cluster)
- [redis.conf - 6.2 버전](https://raw.githubusercontent.com/redis/redis/6.2/redis.conf)
- [extra_hosts]({{ site.baseurl }}{% post_url 2021-12-15-docker-add-etc-hosts-in-docker-compose-file %})
- [GitLab CI/CD - Use the Docker executor with the Docker image (Docker-in-Docker)](https://docs.gitlab.com/ee/ci/docker/using_docker_build.html#use-the-docker-executor-with-the-docker-image-docker-in-docker)
