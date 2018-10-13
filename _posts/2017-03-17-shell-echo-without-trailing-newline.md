---
layout: post
title:  "[Shell] Echo without trailing newline - 줄바꿈 없는 echo"
date:   2017-03-17 22:53:00 +0900
published: true
categories: [ shell ]
tags: [ shell, bash, script, echo, new line, newline ]
---

`echo`명령을 쓰면 우리 눈에는 보이지 않는 줄바꿈 문자(new line)가 마지막에 포함되어 있다. 간혹 줄바꿈 때문에 다른 문자열인 것처럼 취급될 수 있으니, 줄바꿈 문자가 없어서 하는 경우는 `-n` 옵션을 주자.

```bash
$ echo 'ME'
ME
$ echo -n 'ME'
ME%
```

base64로 인코딩 해 보면 차이를 확실히 알 수 있다.

```bash
$ echo 'ME' | base64
TUUK
$ echo -n 'ME' | base64
TUU=
```

`echo`의 man page를 보면 이렇게 쓰여 있다. (물론 설치된 OS에 따라 echo 종류/버전 등에 따라 다를 수 있다.)

> DESCRIPTION
>
> The echo utility writes any specified operands, separated by single blank (' ') characters and followed by a newline ('\n') character, to the standard output.

# 참고
- [echo(1) - Linux man page](https://linux.die.net/man/1/echo)
- [echo man page - Apple Developer](https://developer.apple.com/legacy/library/documentation/Darwin/Reference/ManPages/man1/echo.1.html)
