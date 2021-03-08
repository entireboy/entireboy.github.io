---
layout: post
title:  "[AWS] S3에서 307(Temporary Redirect) 응답"
date:   2021-03-07 22:18:00 +0900
published: true
categories: [ aws ]
tags: [ aws, s3, 307, response, http, status, code, temporary redirect, propagation, cloud front, cdn ]
---

Amazon CloudFront와 S3를 이용하면 아래와 같은 형태로 간단하게 정적 컨텐츠들을 담은 CDN을 구성할 수 있다.

{% include image.html file='/assets/img/2021-03-07-aws-307-temporary-redirect-response-from-s3.png' alt='CDN with Amazon CloudFront and Amazon S3' %}

그런데 설정하고 처음 접속하면 CloudFront URL로 접속할 때 S3 주소로 redirect 되는 것을 볼 수 있다. 이는 S3 버킷을 처음 생성하면 모든 AWS 리전에 전파되는데 최대 24시간이 걸리기 때문이다. 몇 시간만 기다리자.


# 참고

- [Amazon S3에서 HTTP 307 임시 리디렉션 응답을 받는 이유는 무엇입니까?](https://aws.amazon.com/ko/premiumsupport/knowledge-center/s3-http-307-response/)
