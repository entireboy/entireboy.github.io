---
layout: post
title:  "[Kubernetes] DNS doesn't work"
date:   2017-02-13 22:53:00 +0900
published: true
categories: [ kubernetes ]
tags: [ kubernetes, k8s, dns, check, container, pod ]
---

간단한 웹서버 python 버전.

사실 뭐 설치할 것도 없고, 이 명령어만 실행시켜주면 현재 경로가 웹서버로 제공된다. `/`로 접근할 때 `index.html` 같은 파일이 있으면 응답으로 내려주니 긋긋

```bash
$ cat index.html
HELLO!!!
$ python -m SimpleHTTPServer
Serving HTTP on 0.0.0.0 port 8000 ...
10.10.10.10 - - [20/Jan/2017 16:44:01] "GET / HTTP/1.1" 200 -
10.10.10.10 - - [20/Jan/2017 16:46:22] "GET / HTTP/1.1" 200 -
```

```bash
$ curl http://localhost:8080
HELLO!!!
$
```

port number는 parameter로 주면 된다

```bash
$ python -m SimpleHTTPServer 9000
Serving HTTP on 0.0.0.0 port 9000 ...
```

# 참고

- [https://docs.python.org/2/library/simplehttpserver.html](https://docs.python.org/2/library/simplehttpserver.html)
