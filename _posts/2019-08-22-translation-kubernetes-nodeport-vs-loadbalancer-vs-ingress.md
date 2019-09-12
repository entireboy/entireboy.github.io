---
layout: post
title:  "[발번역] Kubernetes NodePort vs LoadBalancer vs Ingress?? 언제 무엇을 써야 할까??"
date:   2019-08-22 22:18:00 +0900
published: true
categories: [ translation ]
tags: [ clumsylation, translation, translate, kubernetes, k8s, NodePort, LoadBalancer, Ingress, microservices, service, load balancing ]
---

> 본 글은 아래 글을 **[발번역]({{ site.baseurl }}/tags/clumsylation)** 한 내용입니다. 누락되거나 잘못 옮겨진 내용이 있을(~~아니 많을;;~~) 수 있습니다. 어색하거나 잘못된 표현은 <a href="{{ site.baseurl }}/about">알려주세요</a>.
>
> 원글: Kubernetes NodePort vs LoadBalancer vs Ingress? When should I use what?
> <https://medium.com/google-cloud/kubernetes-nodeport-vs-loadbalancer-vs-ingress-when-should-i-use-what-922f010849e0>

최근, 누군가 NodePort와 LoadBalancer, Ingress가 무엇인지 차이를 물어 왔다. 셋 다 클러스터 내부로 외부 트래픽을 가져오는 방법이지만, 모두 다른 방법으로 구현하고 있다. 각각이 어떻게 작동하는지 살펴 보고, 어떤 경우에 사용해야 하는지 알아 보자.

**주의**: 이 글의 모든 내용은 [Google Kubernetes Engine](https://cloud.google.com/gke)에서 동작한다. 만약 다른 클라우드나 minikube를 사용한 온프레미스(on prem), 또는 그 이외의 것을 사용한다면, 조금씩 차이가 있을 수 있다. 그리고 기술적으로 깊게 들어가지 않을 것이다. 더 알고 싶으면, [공식 문서](https://kubernetes.io/docs/concepts/services-networking/service/)가 더 좋을 것이다!!


# ClusterIP

ClusterIP 서비스는 Kubernetes 기본 서비스로, 클러스터 내의 다른 앱이 접근할 수 있게 해준다. ClusterIP는 외부 접근이 되지 않는다.

ClusterIP 서비스 설정 YAML파일은 다음과 같이 생겼다:

```yaml
apiVersion: v1
kind: Service
metadata:  
  name: my-internal-service
spec:
  selector:    
    app: my-app
  type: ClusterIP
  ports:  
  - name: http
    port: 80
    targetPort: 80
    protocol: TCP
```

외부에서 ClusterIP 서비스에 접근할 수 없다면, 왜 이 얘기를 하고 있을까?? Kubernetes 프록시를 통하면 접근할 수 있다는 것이다!!

{% include image.html file='/assets/img/2019-08-22-translation-kubernetes-nodeport-vs-loadbalancer-vs-ingress1.png' alt='k8s ClusterIP sample' width='60%' %}

[Ahmet Alp Blkan](https://medium.com/u/2cac56571879?source=post_page-----922f010849e0----------------------), 그림 고마워요.

Kubernetes 프록시를 시작시키자:

```bash
$ kubectl proxy --port=8080
```

이제, 다음과 같은 Kubernetes API를 통해 서비스에 접근할 수 있다:

> http://localhost:8080/api/v1/proxy/namespaces/<NAMESPACE>/services/<SERVICE-NAME>:<PORT-NAME>/

위에서 정의한 서비스에 접근하기 위해, 다음과 같은 주소를 사용할 수 있다:

> http://localhost:8080/api/v1/proxy/namespaces/default/services/my-internal-service:http/


## 언제 사용해야 할까??

Kubernetes 프록시를 사용해서 서비스에 접근해야 할 몇 가지 경우가 있다.

1. 서비스를 디버깅하거나 어떤 이유로 노트북/PC에서 직접 접근할 때
1. 내부 대시보드 표시 등 내부 트래픽을 허용할 때

이 방식에서는 권한 있는 사용자가 kubectl을 실행해야 하기 때문에, 서비스를 외부에 노출하는데 사용하거나 실서비스에서 사용해서는 **안 된다.**


# NodePort

NodePort 서비스는 서비스에 외부 트래픽을 직접 보낼 수 있는 가장 원시적인(primitive) 방법이다. 이름에서 암시하듯이, NodePort는 모든 Node(VM)에 특정 포트를 열어 두고, 이 포트로 보내지는 모든 트래픽을 서비스로 포워딩한다.

{% include image.html file='/assets/img/2019-08-22-translation-kubernetes-nodeport-vs-loadbalancer-vs-ingress2.png' alt='k8s NodePort sample' width='60%' %}

위 그림은 기술적으로 아주 정확한 그림은 아니지만, NodePort가 어떻게 작동하는지 포인트를 잘 살려서 표현한 것 같다.

NodePort 서비스 설정 YAML은 다음과 같다:

```yaml
apiVersion: v1
kind: Service
metadata:  
  name: my-nodeport-service
spec:
  selector:    
    app: my-app
  type: NodePort
  ports:  
  - name: http
    port: 80
    targetPort: 80
    nodePort: 30036
    protocol: TCP
```

기본적으로 NodePort 서비스는 일반 ClusterIP 서비스와는 2가지 차이가 있다. 우선, 타입이 NodePort 이다. 그리고 node에 어떤 포트를 열어줄지 지정하는 nodePort라는 추가 포트가 있다. 이 포트를 지정하지 않으면, 아무 포트나 선택된다. 대부분의 경우 Kubernetes가 포트를 선택하도록 두어야 한다. [thockin](https://medium.com/u/d399b620658f?source=post_page-----922f010849e0----------------------)이 말했듯이, 어떤 포트가 사용 가능한지에는 많은 주의사항이 있다.


## 언제 사용해야 할까??

이 방법에는 많은 제약사항이 있다.

1. 포트당 한 서비스만 할당할 수 있다.
1. 30000-32767 사이의 포트만 사용할 수 있다.
1. Node나 VM의 IP 주소가 바뀌면, 이를 반영해야 한다.

이런 이유들 때문에 나는 실서비스에서 이 방법으로 서비스를 직접적으로 노출시키는걸 추천하지 않는다. 항상 사용가능한 상태가 아니어도 되는 서비스를 운영하거나 비용에 민감(cost sensitive)하다면, 이 방법이 적합할 것이다. 이런 어플리케이션의 좋은 예는 데모 앱이나 임시로 사용되는 것들이다.


# LoadBalancer

LoadBalancer 서비스는 서비스를 인터넷에 노출하는 일반적인 방식이다. GKE에서는 [Network Load Balancer](https://cloud.google.com/compute/docs/load-balancing/network/)를 작동시켜 모든 트래픽을 서비스로 포워딩하는 단 하나의 IP 주소를 제공한다.

{% include image.html file='/assets/img/2019-08-22-translation-kubernetes-nodeport-vs-loadbalancer-vs-ingress3.png' alt='k8s LoadBalancer sample' width='60%' %}

[Ahmet Alp Blkan](https://medium.com/u/2cac56571879?source=post_page-----922f010849e0----------------------), 그림 고마워요.


## 언제 사용해야 할까??

서비스를 직접적으로 노출하기를 원한다면, LoadBalancer가 기본적인 방법일 것이다. 지정한 포트의 모든 트래픽은 서비스로 포워딩 될 것이다. 필터링이나 라우팅 같은건 전혀 없다. 즉,HTTP, TCP, UDP, Websocket, gRPC 등등 거의 모든 트래픽 종류를 보낼 수 있는 것을 뜻한다.

가장 큰 단점은 LoadBalancer로 노출하고자 하는 각 서비스 마다 자체의 IP 주소를 갖게 된다는 것과, 노출하는 서비스 마다 LoadBalancer 비용을 지불해야 하기 때문에 값이 비싸진다는 것이다.


# Ingress

위의 예와는 다르게, Ingress는 서비스의 한 종류가 아니다. 여러 서비스들 앞에서 "스마트 라우터(smart router)" 역할을 하거나 클러스터의 진입점(entrypoint) 역할을 한다.

여러 능력을 가진 Ingress 컨트롤러 타입이 있어서, Ingress 하나만으로 여러 가지 일을 할 수 있다.

기본 GKE Ingress 컨트롤러는 [HTTP(S) Load Balancer](https://cloud.google.com/compute/docs/load-balancing/http/)를 만들어 준다. 이것은 백엔드 서비스로 경로(path)와 서브 도메인 기반의 라우팅을 모두 지원한다. 예를 들어, foo.yourdomain.com으로 들어오는 모든 트래픽을 foo 서비스로 보낼 수 있고, yourdomain.com/bar/ 경로로 들어오는 모든 트래픽을 bar 서비스로 보낼 수 있다.

{% include image.html file='/assets/img/2019-08-22-translation-kubernetes-nodeport-vs-loadbalancer-vs-ingress4.png' alt='k8s ClusterIP sample' width='80%' %}

[Ahmet Alp Blkan](https://medium.com/u/2cac56571879?source=post_page-----922f010849e0----------------------), 그림 고마워요.

GKE에서 L7 HTTP Load Balancer를 가지는 Ingress object 서비스 설정 YAML은 다음과 같다:

```yaml
apiVersion: extensions/v1beta1
kind: Ingress
metadata:
  name: my-ingress
spec:
  backend:
    serviceName: other
    servicePort: 8080
  rules:
  - host: foo.mydomain.com
    http:
      paths:
      - backend:
          serviceName: foo
          servicePort: 8080
  - host: mydomain.com
    http:
      paths:
      - path: /bar/*
        backend:
          serviceName: bar
          servicePort: 8080
```

## 언제 사용해야 할까??

Ingress는 아마도 서비스를 외부에 노출하는 가장 강력한 방법이겠지만 가장 복잡한 방법일 수 있다. [Google Cloud Load Balancer](https://cloud.google.com/kubernetes-engine/docs/tutorials/http-balancer)와 [Nginx](https://github.com/kubernetes/ingress-nginx), [Contour](https://github.com/heptio/contour), [Istio](https://istio.io/docs/tasks/traffic-management/ingress.html) 등과 같은 많은 Ingress 컨트롤러 타입이 있다. 그리고 SSL 인증서를 서비스에 자동으로 프로비저닝해 주는 [cert-manager](https://github.com/jetstack/cert-manager) 같은 Ingress 컨트롤러 플러그인도 많다.

동일한 (보통 HTTP) L7 프로토콜을 사용하는 여러 서비스들을 같은 IP 주소로 외부에 노출한다면 Ingress가 가장 유용할 것이다. Native GCP integration을 사용한다면 단 하나의 로드 밸런서 비용만 지불하면 되고, ingress는 "스마트"하기 때문에 (SSL, Auth, Routing 과 같은) 생각지못했던 다양한 기능을 활용할 수 있다.
