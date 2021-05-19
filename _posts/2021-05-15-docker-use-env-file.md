---
layout: post
title:  "[Docker] docker-compose 파일에서 환경설정(.env file) 사용하기"
date:   2021-05-15 22:18:00 +0900
published: true
categories: [ docker ]
tags: [ docker, docker-compose, env, environment, config ]
---

docker-compose를 사용할 때 환경 마다 다른 설정값을 사용하고 싶을 때가 있다.

개발할 때 쉽게 사용할 환경으로 docker-compose를 사용해서 로컬에서 elasticsearch(ES)와 DB 등을 사용하고 있는데, ES가 메모리도 엄청 먹고 CPU도 계속 쓰는 것을 발견했다. docker-compose 파일을 수정해서 CPU와 메모리 사용을 제한하고 싶은데, 이 파일을 CI 서버에서도 사용하고 있어서 각 환경 마다 다른 설정을 쓸 수 있게 해야 했다.

이럴 때 사용할 수 있는 것이 `.env`파일.

`.env`파일에 아래처럼 설정값을 저장해 두고, `docker-compose.yml`파일에서는 `${ES_CPUS}`처럼 사용하면 된다.

```
# .env 파일
# --env-file 옵션을 주지 않으면 자동으로 사용됨
ES_CPUS=1
ES_MEMORY=1G
```

```yaml
version: '3.8'

services:
  elasticsearch:
    image: elasticsearch:7.9.3
    container_name: product_system_elasticsearch_7.9
    restart: always
    ports:
      - "9200:9200"
    environment:
      - "discovery.type=single-node"
    network_mode: bridge
    deploy:
      resources:
        limits:
          cpus: "${ES_CPUS}"
          memory: "${ES_MEMORY}"
  localstack:
    ..
  mysql:
    ..
```

config 옵션으로 적용될 설정을 확인해 볼 수 있다.

```bash
$ docker-compose config
services:
  elasticsearch:
    container_name: product_system_elasticsearch_7.9
    deploy:
      resources:
        limits:
          cpus: 1.0
          memory: 1G
    environment:
      discovery.type: single-node
    image: elasticsearch:7.9.3
    .. (생략) ..
```


# 다른 환경에서 사용할 .env 파일

각 환경에서 사용할 파일을 `.env` 파일 이외에 별도로 생성한다.

```
# .env.ci 파일
# --env-file 옵션으로 사용
ES_CPUS=2
ES_MEMORY=6G
```

`--env-file` 옵션으로 설정 파일을 지정할 수 있다. 역시 아래처럼 적용된 설정을 확인할 수 있다.

```bash
$ docker-compose --env-file .env.ci config
services:
  elasticsearch:
    container_name: product_system_elasticsearch_7.9
    deploy:
      resources:
        limits:
          cpus: 2.0
          memory: 6G
    environment:
      discovery.type: single-node
    image: elasticsearch:7.9.3
    .. (생략) ..
```


# 참고

- [Environment variables in Compose](https://docs.docker.com/compose/environment-variables/)
- [Declare default environment variables in file](https://docs.docker.com/compose/env-file/)
