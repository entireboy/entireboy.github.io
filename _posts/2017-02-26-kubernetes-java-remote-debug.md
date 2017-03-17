---
layout: post
title:  "[Kubernetes] Java remote debug"
date:   2017-02-26 22:53:00 +0900
published: true
categories: [ kubernetes ]
tags: [ kubernetes, k8s, docker, java, remote, debug, debugging ]
---

간혹 Kubernetes에서 돌고 있는 java container에 접속해서 디버깅을 하고 싶을 때가 있다. remote debugging.. 이럴 때면 Kubernetes와 Docker 네트웍이 참 답답하다. T_T

1\. Java 명령을 실행할 때 `-agentlib` 옵션을 함께 준다.

```bash
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 MyApp
```

Java 옵션 `-agentlib`을 Docker의 argument로 전달해도 되는 경우 아래처럼 container args로 전달해도 된다.

2\. Kubernetes container를 생성할 때 remote debug로 접속할 port를 열어준다.

```yaml
spec:
  containers:
  - args: [ "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005" ]
    command: []
    image: "my.docker.hub/me/my-image:1.0.0"
    name: "my-service"
    ports:
    - containerPort: 5005
      name: "jvm-debug"
```

# 참고

- [Java Remote Debug for Applications Running in Kubernetes](http://blog.christianposta.com/kubernetes/java-remote-debug-for-applications-running-in-kubernetes/)
