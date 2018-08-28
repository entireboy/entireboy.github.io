---
layout: post
title:  "[Lombok] @Builder와 @NoArgsConstructor 함께 사용하기"
date:   2018-08-28 23:18:00 +0900
published: true
categories: [ java ]
tags: [ java, lombok, annotation, Builder, constructor, NoArgsConstructor, AllArgsConstructor ]
---

> TL;DR. `@Builder`와 `@NoArgsConstructor`를 함께 사용하려면, `@AllArgsConstructor`도 함께 사용하거나 모든 필드를 가지는 생성자를 직접 만들어 줘야 한다. `@Builder`를 사용할 때 `@NoArgsConstructor`뿐만 아니라 손수 만든 다른 생성자가 있다면, 그 때도 모든 필드를 가지는 생성자가 필요하다.