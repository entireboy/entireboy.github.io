---
layout: post
title:  "[UX/UI] Drag map with 2 fingers on mobile page"
date:   2017-01-26 21:39:00 +0900
published: true
categories: [ ux, ui, map ]
tags: [ ux, ui, google, maps, google maps, map, finger, scroll, move, mobile, screen ]
---

동일한 행동이 겹치는 경우가 있다.

- 모바일 화면에서는 손가락 하나로 스크롤링을 한다. 상하좌우.
- 페이지에 embed되어 있는 지도를 움직일 때, 손가락 하나로 움직인다.

둘이 겹쳐버리면 내가 원하지 않는 동작을 할 때가 있다. 특히, 화면이 작은 모바일 화면에서는, 페이지에 있는 지도가 전체 화면을 가려버리는 경우가 종종 발생한다. 나는 스크롤을 하고 싶지만 계속 지도만 움직인다. 지도가 화면을 모두 차지하고 있으니 손가락 하나로 아무리 비벼봐도, 스크롤을 할 수 없고 지도를 치울 수 없는 뫼비우스의 띠 같은 현상이..

Google Maps([http://maps.google.com/](http://maps.google.com/))는 간단히 지도를 움직이고 싶으면 손가락 2개를 사용하라고 한다. 그리고 이 헬프 메시지가 지도를 가리면 안 되니까, 지도에서 손가락 하나로 스크롤링을 할 때만 메시지를 보여준다. (지도의 가장 큰 목적은 지도를 보여주는거니까 메시지로 가리지 말자.)

![google maps]({{ site.baseurl }}/assets/img/2017-01-26-ux-grag-map-with-2-fingers.png)

그리고, PC화면은??

- 스크롤을 하기 위해 휠을 돌린다.
- 지도의 줌을 하기 위해 휠을 돌린다.

역시나 스크롤을 하고 싶지만 화면 가득 채워진 지도가 줌만 되는 경우가 있다. 슬프지만, 아직까지는 방법이 없나보다. T_T 어떤 UX를 넣으면 모바일 화면에서 손가락 2개처럼 자연스러운 방법으로 원하는 행동을 할 수 있을까??
