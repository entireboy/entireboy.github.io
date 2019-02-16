---
layout: post
title:  "사이드카 패턴(Sidecar pattern)"
date:   2019-02-16 23:18:00 +0900
published: true
categories: [ cloud ]
tags: [ cloud, sidecar, pattern, k8s, kubernetes, aws, azure, centralized logging, logging, log ]
---

사이드카 패턴은 어플리케이션 컨테이너와 독립적으로 동작하는 별도의 컨테이너를 붙이는 패턴이다. 어플리케이션 컨테이너의 변경이나 수정 없이 독립적으로 동작하는 컨테이너를 붙였다 뗐다 할 수 있다.

{% include image.html file='/assets/img/2019-02-16-cloud-sidecar-pattern.jpg' alt='오! 나의의 여신님의 BMW 오스카 리브맨 스페셜' width="400px" %}
`오! 나의 여신님`에서 케이와 베르단디가 타고 다니는 `BMW 오스카 리브맨 스페셜`과 옆에 붙은 사이드카

오토바이나 스쿠터에 붙이는 [사이드카](https://www.google.com/search?q=sidecar&tbm=isch) 이미지를 보면 이해가 쉬울 것이다. 옆에 붙은 사이드카를 붙이든 떼어내든 상관 없이 오토바이는 동일하게 운전할 수 있다.


# 샘플

사이드카 패턴을 사용하기 좋은 샘플들은 이런게 있을 수 있다.

- 보안을 위해 사이드카로 NGINX reverse proxy 등을 붙여서 HTTPS 통신을 한다.
- 성능을 위해 사이드카로 NGINX content cache 등을 붙인다.
- 컨테이너 외부로 로그를 모으기 위해 logstash, fluentd 등을 붙인다. (centralized logging)

모두 어플리케이션 변경 없이, 사이드카를 붙였다 떼거나 교체하기 쉬운 구성 방식이다. 상황에 따라 사이드카를 다른 것으로 변경하거나 버전업 등이 필요할 수도 있다. 예를 들어, content cache를 NGINX 대신 Apache HTTP server로 교체하는 등의 작업을 해도 어플리케이션은 영향을 받지 않는다.


# 장단점

장점

- 상호 의존성을 줄일 수 있다.
- 사이드카 장애 시 어플리케이션이 영향을 받지 않는다. (isolation)
- 사이드카 적용/변경/제거 등의 경우에 어플리케이션은 수정이 필요 없다.
- 어플리케이션과 사이드카를 다른 언어로 만들 수 있다.
- 대부분 같은 스토리지를 공유할 수 있기 때문에 공유에 대한 고민이 적다.

단점

- 어플리케이션이 너무 작은 경우 배 보다 배꼽이 커질 수 있다.
- 프로세스간 통신이 많고 최적화 해야 한다면, 어플리케이션에서 함께 처리하는게 좋을 수 있다.


# 참고

- [The Sidecar Pattern](https://blog.davemdavis.net/2018/03/13/the-sidecar-pattern/)
- [Sidecar pattern - Azure docs](https://docs.microsoft.com/en-us/azure/architecture/patterns/sidecar)
- [How Pods manage multiple Containers - kubernetes Pod Overview doc](https://kubernetes.io/docs/concepts/workloads/pods/pod-overview/#how-pods-manage-multiple-containers)
- [Deploying an NGINX Reverse Proxy Sidecar Container on Amazon ECS](https://aws.amazon.com/ko/blogs/compute/nginx-reverse-proxy-sidecar-container-on-amazon-ecs/)
- [Centralized logging in Kubernetes](https://medium.com/@maanadev/centralized-logging-in-kubernetes-d5a21ae10c6e)
