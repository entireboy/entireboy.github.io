---
layout: post
title:  "[Shell] 디렉토리 별 디스크 사용량 확인"
date:   2017-09-22 21:18:00 +0900
published: true
categories: [ shell ]
tags: [ shell, bash, disk, usage, directory, folder, check ]
---

디스크 정리 같은 것들이 필요할 때, 디렉토리 별로 디스크 사용량 확인이 필요하다.

`du` 명령을 사용하면 되는데, 옵션 없이 du만 사용하면 파일 단위로 보여주기 때문에 어떤 디렉토리가 많이 사용하고 있는지 모아서 보기가 어렵다. 이럴 때 `--max-depth=1`을 줘서 바로 아래 디렉토리까지만 모아서 보거나, `-s`/`--summary` 옵션으로 요약 정보를 보는 것도 좋다. 더 하위의 디렉토리도 보고 싶다면 `--max-depth`를 늘려주면 된다.

```bash
$ # 현재위치에서 1 depth 아래 폴더 단위로 사용량 요약
$ du -sh *
$ du -h --max-depth=1

$ # 이런저런 활용
$ du -sh sub/*
$ du -sh PREFIX*
$ du -sh *POSTFIX
$ du -h --max-depth=1 .
$ du -h --max-depth=2 /usr/local
```

팁으로.. 디스크 마운트 별로 사용량 확인은 `df` 명령으로..

```bash
$ df -h
```


# 참고

- [리눅스에서 디렉토리별 용량 확인 방법](https://slipp.net/questions/159)
