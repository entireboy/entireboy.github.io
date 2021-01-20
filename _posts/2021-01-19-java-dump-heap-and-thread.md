---
layout: post
title:  "[Java] Thread dump / Heap dump 뜨기"
date:   2021-01-19 22:18:00 +0900
published: true
categories: [ java ]
tags: [ java, dump, heap, thread, heap dump, thread dump, command ]
---

매번 찾기 귀찮아서 정리

[덤프 분석툴]({{ site.baseurl }}{% post_url 2016-03-07-java-dump-analyze-tool %})


# PID 찾기

```bash
$ ps -eF | grep java
$ jps -mlv
```

# Thread dump

```bash
$ jstack -l  <pid> > <file-path>
$ jstack -l 37320 > /opt/tmp/threadDump.txt

$ kill -3 <pid>
```

# Heap dump

> NOTE: Heap dump 는 부하가 크기 때문에 서비스 중인 어플리케이션에서 실행하면 안 된다.

```bash
$ jmap -dump:live,format=b,file=heapdump.hprof <PID>
```
