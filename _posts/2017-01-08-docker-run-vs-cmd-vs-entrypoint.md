---
layout: post
title:  "[Docker] RUN vs CMD vs ENTRYPOINT in Dockerfile"
date:   2017-01-08 15:57:00 +0900
published: true
categories: [ docker ]
tags: [ docker, build, dockerfile, run, cmd, entrypoint, image ]
---

헷갈리기 쉬운 Docker Dockerfile 명령어, `RUN`, `CMD`, `ENTRYPOINT`. 모두 뭔가 실행하는 명령어이다.

- **RUN**. 새로운 레이어에서 명령어를 실행하고, 새로운 이미지를 생성한다. 보통 패키지 설치 등에 사용된다. e.g. apt-get
- **CMD**. default 명령이나 파라미터를 설정한다. `docker run` 실행 시 실행할 커맨드를 주지 않으면 이 default 명령이 실행된다. 그리고 `ENTRYPOINT`의 파라미터를 설정할 수도 있다. **CMD의 주용도는 컨테이너를 실행할 때 사용할 default를 설정**하는 것이다.
- **ENTRYPOINT**. 컨테이너를 실행할 수 있게 설정한다.

# RUN

보통 이미지 위에 다른 패키지(프로그램)를 설치하고 새로운 레이어를 생성할 때 사용한다. 다음은 ubuntu 이미지 위에 curl을 설치하는 예제이다.

```dockerfile
FROM ubuntu:14.04
RUN apt-get update
RUN apt-get install -y curl
```

`RUN` 명령을 실행할 때 마다 레이어가 생성되고 캐시된다. 따라서, 위와 같이 `RUN` 명령을 따로 실행하면 `apt-get update`는 다시 실행되지 않아서 최신 패키지를 설치할 수 없다. 아래처럼 RUN 명령 하나에 `apt-get update`와  `install`을 함께 실행해 주자.

```dockerfile
FROM ubuntu:14.04
RUN apt-get update && apt-get install -y \
    curl \
    nginx \
&& rm -rf /var/lib/apt/lists/*
```

# CMD

`CMD`는 `docker run` 실행 시 명령어를 주지 않았을 때 사용할 default 명령을 설정하거나, `ENTRYPOINT`의 default 파라미터를 설정할 때 사용한다. `CMD` 명령의 주용도는 컨테이너를 실행할 때 사용할 default를 설정하는 것이다. `CMD` 명령은 3가지 형태가 있다.

- CMD ["executable","param1","param2"] (exec form, this is the preferred form)
- CMD ["param1","param2"] (as default parameters to ENTRYPOINT)
- CMD command param1 param2 (shell form)

```dockerfile
FROM ubuntu
CMD echo "This is a test."
```

위와 같이 Dockerfile을 만들었을 때, `docker run` 실행 시 아무런 커맨드를 주지 않으면 `CMD` 명령이 실행된다.

```bash
$ docker run -it --rm <image-name>
This is a test
$
```

하지만, `echo "Hello"` 라고 실행할 커맨드를 주게 되면 `CMD`는 무시되고 커맨드가 실행된다.

```bash
$ docker run -it --rm <image-name> echo "Hello"
Hello
$
```

`CMD`는 여러번 사용할 수 있지만 가장 마지막에 있는 `CMD` 딱 1개만 남게 된다. (override) `ENTRYPOINT`의 default 파라미터는 아래 `ENTRYPOINT`에서..

# ENTRYPOINT

`ENTRYPOINT`는 2가지 형태를 가지고 있다.

- ENTRYPOINT ["executable", "param1", "param2"] (exec form, preferred)
- ENTRYPOINT command param1 param2 (shell form)

`docker run` 실행 시 실행되는 명령이라고 생각해도 좋을 것 같다.

```dockerfile
FROM ubuntu
ENTRYPOINT ["/bin/echo", "Hello"]
CMD ["world"]
```

위 Dockerfile의 내용을 실행하면 `CMD`에서 설정한 default 파라미터가 `ENTRYPOINT`에서 사용된다. `docker run` 명령 실행 시 파라미터를 주면 `CMD`에서 설정한 파라미터는 사용되지 않는다.

```bash
$ docker run -it --rm <image-name>
Hello world
$ docker run -it --rm <image-name> ME
Hello ME
$
```

shell form 으로 실행해야만 변수 등이 대체(substitution)된다.

```dockerfile
FROM ubuntu
ENTRYPOINT [ "echo", "$HOME" ]
```

```bash
$ docker run -it --rm <image-name>
$HOME
$
```

위처럼 exec form으로 사용하면 `$HOME` 이 그대로 사용되고, 아래처럼 shell form으로 사용하면 변수 등이 변환된다.

```dockerfile
FROM ubuntu
ENTRYPOINT echo $HOME
```

```bash
$ docker run -it --rm <image-name>
/root
$
```

`CMD`와 `ENTRYPOINT`의 조합은 [ENTRYPOINT / CMD combinations](https://docs.docker.com/engine/reference/builder/#/understand-how-cmd-and-entrypoint-interact)에 표로 잘 정리되어 있다.

# 참고

- [http://goinbigdata.com/docker-run-vs-cmd-vs-entrypoint/](http://goinbigdata.com/docker-run-vs-cmd-vs-entrypoint/)
- [https://docs.docker.com/engine/reference/builder/](https://docs.docker.com/engine/reference/builder/)
- [https://docs.docker.com/engine/userguide/eng-image/dockerfile_best-practices/](https://docs.docker.com/engine/userguide/eng-image/dockerfile_best-practices/)
