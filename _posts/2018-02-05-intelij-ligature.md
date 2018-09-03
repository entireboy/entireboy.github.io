---
layout: post
title:  "[IntelliJ] ligature (기호/연산자 묶음??)"
date:   2018-02-05 22:18:00 +0900
published: true
categories: [ intellij ]
tags: [ intellij, operator, ligature, font ]
---

아래의 빨간 네모 안에 있는 것처럼 표기가 되면, 한눈에 알아보기 쉬워진다.

{% include image.html file='/assets/img/2018-02-05-intelij-ligature1.png' alt='기호 묶음' %}

[IntelliJ 2016에 추가](https://blog.jetbrains.com/idea/2016/06/intellij-idea-2016-2-eap-case-only-renames-in-git-ligatures-background-images-and-more/)된 ligature 기능을 사용하면 아래와 같은 기호들이 위처럼 이쁘게 모여서 보인다. [ligature를 영어사전에서](http://small.dic.daum.net/word/view.do?wordid=ekw000097027&q=ligature) 찾아보면 묶기, 잡아매기, 연결선 등의 뜻을 가지고 있다. 여러 기호를 묶어서 하나로 보여주는 의미로 지어진 이름이 아닐까 싶다.

{% include image.html file='/assets/img/2018-02-05-intelij-ligature2.png' alt='기호 묶음' %}

IntelliJ 2016.2 버전 이상이면 설정할 수 있고, `Preferences` -> `Editor` -> `Colors & Fonts` -> `Font` -> `Enable font ligature`에 체크를 하면 된다.

단, ligature를 지원하는 폰트로 글꼴을 선택해야 한다. [FiraCode](https://github.com/tonsky/FiraCode), [Hasklig](https://github.com/i-tu/Hasklig), [Monoid](https://github.com/larsenwork/monoid), [PragmataPro](http://www.fsd.it/shop/fonts/pragmatapro/) 등이 있다.

{% include image.html file='/assets/img/2018-02-05-intelij-ligature3.png' alt='기호 묶음 설정' %}


# 참고

- [Enabling IntelliJ's fancy ≠ (not equal) operator
](https://stackoverflow.com/questions/41774046/enabling-intellijs-fancy-%E2%89%A0-not-equal-operator)
- [IntelliJ IDEA 2016.2 EAP: Case-Only Renames in Git, Ligatures, Background Images, and More](https://blog.jetbrains.com/idea/2016/06/intellij-idea-2016-2-eap-case-only-renames-in-git-ligatures-background-images-and-more/)
- [IntelliJ products instructions](https://github.com/tonsky/FiraCode/wiki/Intellij-products-instructions)
