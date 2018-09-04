---
layout: post
title:  "[발번역] Apache Kafka를 사용한 컨슈머 재시도 설계 - Retrying consumer architecture in the Apache Kafka"
date:   2018-09-10 23:18:00 +0900
published: true
categories: [ translation ]
tags: [ clumsylation, translation, translate, kafka, consumer, retry, architecture ]
---

> 본 글은 아래 글을 **[발번역]({{ site.baseurl }}/tags/clumsylation)** 한 내용입니다. 누락되거나 잘못 옮겨진 내용이 있을(~~아니 많을;;~~) 수 있습니다.
>
> 원글: Retrying consumer architecture in the Apache Kafka by Łukasz Kyć
> <https://blog.pragmatists.com/retrying-consumer-architecture-in-the-apache-kafka-939ac4cb851a>

 

{% include image.html file='/assets/img/2018-09-10-translation-retrying-consumer-architecture-in-the-apache-kafka1.jpg' alt='mailbox' %}


{% include image.html file='/assets/img/2018-09-10-translation-retrying-consumer-architecture-in-the-apache-kafka2.png' alt='mailbox' %}


{% include image.html file='/assets/img/2018-09-10-translation-retrying-consumer-architecture-in-the-apache-kafka3.png' alt='mailbox' %}

# 옮긴이 각주

[^provisioning]: 프로비저닝(Provisioning) [wikipedia](https://en.wikipedia.org/wiki/Provisioning_(telecommunications))
