---
layout: post
title:  "[Docker] ADD vs COPY in Dockerfile"
date:   2017-01-07 18:02:00 +0900
published: true
categories: [ docker ]
tags: [ docker, build, dockerfile, add, copy, image ]
---

Dockerfile의 `ADD` 명령과 `COPY` 명령은 일반적인 복사 명령이다. 다만, 차이점이라면, `ADD`는 추가적 기능(?)이 더 있다:

- URL을 복사할 source로 사용할 수 있다. remote에 있는 파일을 받아서 복사하게 된다.
- source 파일이 gzip과 같이 일반적으로 잘 알려진 압축형태인 경우, 압축을 풀어준다.
- 압축된 remote 파일인 경우, 압축을 풀어주지는 않는다.

신기하다. :D

# 참고
- [http://stackoverflow.com/questions/24958140/what-is-the-difference-between-the-copy-and-add-commands-in-a-dockerfile](http://stackoverflow.com/questions/24958140/what-is-the-difference-between-the-copy-and-add-commands-in-a-dockerfile)
- [https://docs.docker.com/engine/reference/builder/](https://docs.docker.com/engine/reference/builder/)
- [https://docs.docker.com/engine/userguide/eng-image/dockerfile_best-practices/](https://docs.docker.com/engine/userguide/eng-image/dockerfile_best-practices/)
