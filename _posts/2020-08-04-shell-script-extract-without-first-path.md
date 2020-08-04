---
layout: post
title:  "[Shell] 최상위 경로 제외하고 tar 풀기"
date:   2020-08-04 21:18:00 +0900
published: true
categories: [ shell ]
tags: [ shell, tar, extract, remove, without, first, parent, path ]
---

압축파일에 내가 원하지 않는 경로가 포함되어 있을 때가 있다.

예를 들면, pinpoint 2.0.4 버전의 압축파일은 `pinpoint-agent-2.0.4` 경로 하위에 모든 파일이 포함되어 있다.

```bash
$ tar tf pinpoint-agent-2.0.4.tar.gz
pinpoint-agent-2.0.4/
pinpoint-agent-2.0.4/profiles/
pinpoint-agent-2.0.4/profiles/local/
...
```

압축을 풀고 `mv` 명령으로 옮겨도 되지만, 그 경로명을 알지 못 해 옮기지 못 할 수도 있다. 이럴 때는 `--strip` 옵션을 주면 상위 경로를 제외하고 압축을 풀 수 있다.

`--strip` 옵션 없이 압축을 풀면 아래처럼 압축이 풀리지만,

```bash
$ tar xvf xvfz pinpoint-agent-2.0.4.tar.gz
  ...
$ ls
pinpoint-agent-2.0.4        pinpoint-agent-2.0.4.tar.gz
$ 
```

`--strip` 옵션을 주고 풀면 그 숫자만큼 경로를 제외하고 압축을 풀게 된다. `--strip 1` 옵션을 주면 상위 1개의 경로를 제외하고 압축을 푼다.

```bash
$ tar xvf xvfz pinpoint-agent-2.0.4.tar.gz --strip 1
  ...
$ ls
VERSION                      pinpoint-agent-2.0.4.tar.gz  profiles
boot                         pinpoint-bootstrap-2.0.4.jar script
build.info                   pinpoint-bootstrap.jar       tools
lib                          pinpoint.config
logs                         plugin
$
```


# 참고

- [Extract without First Directory](https://www.marksanborn.net/linux/extract-without-first-directory/)
