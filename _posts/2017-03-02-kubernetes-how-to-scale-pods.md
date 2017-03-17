---
layout: post
title:  "[Kubernetes] How to scale pods"
date:   2017-03-02 22:53:00 +0900
published: true
categories: [ kubernetes ]
tags: [ kubernetes, k8s, scale, pod, replica ]
---

Kubernetes Pod을 스케일링(scale)하기 위해 Replica Set의 `replicas`값을 바꿔줘도 Pod이 생겼다가 원래 개수로 계속 돌아오는 문제가 생겼다.

왜 그럴까 한참을 고민했는데, Deployment로 만든 Pod들은 Deployment의 설정을 바꿔줘야 한다. (당연하다. 바보 =_=) Deployment로 Pod을 생성하면 Deployment가 Replica Set(예전 버전이라면 Replica Controller)을 관리하고, 다시 Replica Set이 Pod들을 관리한다. Deployment를 바꾸지 않고 Replica Set을 변경하면 Deployment가 계속 감시해서 원상태로 되돌려 놓는다.

```bash
$ kubectl get rs/my-test-1170944398
NAME                 DESIRED   CURRENT   READY     AGE
my-test-1170944398   3         3         3         2h
$ # scale 명령을 통해 현재 3개인 pod을 5개로 늘려준다.
$ kubectl scale rs/my-test-1170944398 --replicas=5
replicaset "my-test-1170944398" scaled
$ # 잠시 후 get으로 다시 확인해 보면 3개로 줄어 있다.
$ kubectl get rs/my-test-1170944398
NAME                 DESIRED   CURRENT   READY     AGE
my-test-1170944398   3         3         3         2h
$ # Deployment의 replicas를 변경
$ kubectl scale deployment/my-test --replicas=5
deployment "my-test" scaled
$ kubectl get rs/my-test-1170944398
NAME                 DESIRED   CURRENT   READY     AGE
my-test-1170944398   5         5         5         2h
```

Pod을 Replica Set으로 만들었다면 당연히 Deployment가 없기 때문에 Replica Set의 `replicas`를 변경해 주면 된다. 하지만, Deployment로 만들었다면 Replica Set이 아닌 Deployment의 `replicas`를 변경해 주자.

# 참고

- [Kubernetes: how to scale my pods - Stack Overflow](http://stackoverflow.com/questions/38344896/kubernetes-how-to-scale-my-pods)
