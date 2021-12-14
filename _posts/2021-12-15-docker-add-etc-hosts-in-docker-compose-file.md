---
layout: post
title:  "[Docker] Compose file에서 /etc/hosts 추가하기"
date:   2021-12-15 22:18:00 +0900
published: true
categories: [ docker ]
tags: [ docker, docker-compose, add, extra, host ]
---

Docker Compose를 사용하면서 `/etc/hosts` 파일에 호스트를 추가하고 싶을 때는 `extra_hosts`에 추가하면 된다.

```yaml
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

# 참고

- [extra_hosts - Compose file version 3 reference](https://docs.docker.com/compose/compose-file/compose-file-v3/#extra_hosts)
- [How to update /etc/hosts file in Docker image during "docker build"](https://stackoverflow.com/questions/38302867/how-to-update-etc-hosts-file-in-docker-image-during-docker-build)
