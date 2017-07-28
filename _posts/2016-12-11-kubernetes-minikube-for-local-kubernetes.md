---
layout: post
title:  "[Kubernetes] Minikube for local Kubernetes"
date:   2016-12-11 19:26:00 +0900
published: true
categories: [ kubernetes ]
tags: [ kubernetes, k8s, minikube, kubectl, install, local ]
---

kubernetes([http://kubernetes.io/](http://kubernetes.io/))를 사용하려면 여러대의 master node와 slave node가 필요하다. local에서는 여러 node를 띄우기 어려우니 테스트하기 위해 single node로 사용할 수 있는 [minikube](http://kubernetes.io/docs/getting-started-guides/minikube/)라는 툴을 제공한다.

minikube는 Linux, MacOS, Windows 모든 OS를 지원하는데, [VirtualBox](https://www.virtualbox.org/wiki/Downloads)나 [VMware Fusion](https://www.vmware.com/products/fusion) 등이 필요하다. 각 OS별 설치는 [kubernetes 홈페이지](http://kubernetes.io/docs/getting-started-guides/minikube/)나 [github 페이지](https://github.com/kubernetes/minikube)에서 확인하면 되고, 여기서는 MacOS 기준으로 설명한다.

아래 명령을 실행해서 minikube 커맨드를 받아 `/usr/loca/bin` 경로에 추가한다. `/usr/loca/bin`은 path가 잡혀 있으니 언제든 minikube 명령어를 실행할 수 있다.

```bash
$ curl -Lo kubectl http://storage.googleapis.com/kubernetes-release/release/v1.3.0/bin/darwin/amd64/kubectl && chmod +x kubectl && sudo mv kubectl /usr/local/bin/
```

`minikube start` 명령으로 VirtualBox나 VMWare Fusion으로 실행하면 된다. 처음 실행할 때 minikube 이미지를 받아와서 설치하기 때문에 시간이 조금 걸린다.

```bash
$ minikube start
Starting local Kubernetes cluster...
Downloading Minikube ISO
36.00 MB / 36.00 MB [==============================================] 100.00% 0s
Kubectl is now configured to use the cluster.
```

{% include google-ad-content %}

kubernetes cli인 `kubectl`을 이용해서 실행 중인 kubernetes cluster 정보를 얻어올 수 있다. MacOS는 [homebrew](http://brew.sh/) 등으로 kubectl을 간단히 설치할 수 있다.

```bash
$ brew update
      ...
$ brew install kubectl
      ...
$ kubectl cluster-info
Kubernetes master is running at https://192.168.99.100:8443
KubeDNS is running at https://192.168.99.100:8443/api/v1/proxy/namespaces/kube-system/services/kube-dns
kubernetes-dashboard is running at https://192.168.99.100:8443/api/v1/proxy/namespaces/kube-system/services/kubernetes-dashboard
```

kubernetes는 대부분 `kubectl`을 통해서 컨트롤하지만, 간단한건 UI를 통해서도 가능하다.

```bash
$ minikube dashboard
Opening kubernetes dashboard in default browser...
```

위 커맨드를 실행하면 브라우저로 아래와 같은 dashboard 화면을 볼 수 있다.

![minikube dashboard](/assets/img/2016-12-11-kubernetes-minikube-for-local-kubernetes.png)

# 참고

- [http://kubernetes.io/docs/getting-started-guides/minikube/](http://kubernetes.io/docs/getting-started-guides/minikube/)
- [https://github.com/kubernetes/minikube/releases](https://github.com/kubernetes/minikube/releases)
- [https://github.com/kubernetes/minikube/blob/v0.12.2/README.md](https://github.com/kubernetes/minikube/blob/v0.12.2/README.md)
- [http://blog.kubernetes.io/2016/07/minikube-easily-run-kubernetes-locally.html](http://blog.kubernetes.io/2016/07/minikube-easily-run-kubernetes-locally.html)
