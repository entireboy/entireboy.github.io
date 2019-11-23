---
layout: post
title:  "[Docker] Docker 데몬 시작 시 마다 container 함께 시작시키기"
date:   2019-11-20 22:18:00 +0900
published: true
categories: [ docker ]
tags: [ docker, restart, container, run, daemon, exit, exit code ]
---

로컬에서 개발할 때 출근 하고 랩탑을 켜고 Docker desktop을 실행시키면 매번 `docker run`명령을 통해 자주 쓰는 container를 실행시켜줘야 한다. 주로 로컬 DB가 있는 경우 이런 귀찮음이 엄청나다. 이럴 때는 `--restart` 옵션을 주면 docker desktop을 실행시킬 때 마다 container를 항상 같이 띄울 수 있다.

혹은 서버에서 docker container가 죽는 경우 재시작을 할 수 있도록 설정할 수도 있다.

```bash
$ docker run --name mysql57 \
    -p 3306:3306 \
    -e MYSQL_ROOT_PASSWORD=th.deng \
    -e MYSQL_ROOT_HOST='%' \
    --restart=unless-stopped \
    -d \
    mysql/mysql-server:5.7 \
    --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci
```

`--restart` 옵션은 4가지가 있다.:

- `no`: container를 재시작 시키지 않는다. (default)
- `on-failure[:max-retries]`: container가 정상적으로 종료되지 않은 경우(exit code가 0이 아님)에만 재시작 시킨다. `max-retries`도 함께 주면 재시작 최대 시도횟수를 지정할 수 있고, 테스트 서버 등과 같은 리모트에 설정하면 좋을 것 같다.
- `always`: container를 항상 재시작시킨다. exit code 상관 없이 항상 재시작 된다.
- `unless-stopped`: container를 `stop`시키기 전 까지 항상 재시작 시킨다.


# 참고

- [Restart policies (--restart) - Docker run reference](https://docs.docker.com/engine/reference/run/#restart-policies---restart)
