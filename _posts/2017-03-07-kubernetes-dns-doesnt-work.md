---
layout: post
title:  "[Kubernetes] DNS doesn't work - check DNS containers"
date:   2017-03-07 22:53:00 +0900
published: true
categories: [ kubernetes ]
tags: [ kubernetes, k8s, dns, pod, container ]
---

갑자기 kube-dns가 응답을 안 할 때, container가 모두 실행 중인지 체크해 보자.

Kubernetes [DNS Pods and Services](https://kubernetes.io/docs/concepts/services-networking/dns-pod-service/) 문서에 따르면, Kubernetes의 DNS는 3개의 container로 나뉘어져 있다.:
- kubedns
- dnsmasq
- healthz

> The running Kubernetes DNS pod holds 3 containers - kubedns, dnsmasq and a health check called healthz.

![Kubernetes dns containers](/assets/img/2017-03-07-kubernetes-dns-doesnt-work.png)

# 참고

- [DNS Pods and Services - Kubernetes](https://kubernetes.io/docs/concepts/services-networking/dns-pod-service/)
