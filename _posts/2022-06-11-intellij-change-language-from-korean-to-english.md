---
layout: post
title:  "[IntelliJ] 한글화 적용된 메뉴 영문으로 되돌리기"
date:   2022-06-11 22:18:00 +0900
published: true
categories: [ intellij ]
tags: [ intellij, language, plugin, language pack, korean ]
---

갑자기 IntelliJ 메뉴가 한글화 되어서 한국어로 보일 때, 영문으로 되돌리려면 Plugins에서 [한국어 언어 팩](https://plugins.jetbrains.com/plugin/13711-korean-language-pack------)을 제거하면 된다.

IntelliJ가 정상 동작을 하지 않거나 추가 설정이 필요할 때 검색을 하면 대부분 영문 메뉴명으로 나오기 때문에 한국어 언어 팩은 걸림돌이 될 때가 많다.

{% include image.html file='/assets/img/2022/2022-06-11-intellij-change-language-from-korean-to-english1.png' alt='Remove Korean language pack' %}


# 언애팩 추천 안 보이게 설정

아래 이미지처럼 툴팁으로 한글화(localized)된 IntelliJ IDEA를 설치할지 물어보는 알림(Notifications)이 온다.


{% include image.html file='/assets/img/2022/2022-06-11-intellij-change-language-from-korean-to-english2.png' alt='Turn off language pack plugin recommendation tool window' %}

이 때 알림 오른쪽 위에 있는 설정(톱니바퀴)를 눌러서 알림 자체를 꺼버리면 혹시라도 잘못 설치되는 경우를 방지할 수 있다. 스크린샷에는 안 보이지만 알림 풍선 오른쪽 위에 마우스를 올리면 톱니바퀴 버튼이 보인다. 이 버튼을 눌러서 `Notifications 설정`창을 열고,`Recommended language plugin available` 선택

- `Show in tool window` 체크를 해제
- `Popup type`으로 `No popup` 선택


# 참고
- [Korean Language Pack / 한국어 언어 팩 - IntelliJ plugins](https://plugins.jetbrains.com/plugin/13711-korean-language-pack------)
