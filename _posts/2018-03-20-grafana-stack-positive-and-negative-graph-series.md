---
layout: post
title:  "[Grafana] 위아래(+/-, 양수/음수)로 그래프 그리기"
date:   2018-03-20 23:18:00 +0900
published: true
categories: [ grafana ]
tags: [ grafana, graph, positive, negative, stack, series ]
---

[Grafana](https://grafana.com/)에서 2개의 그래프를 하나는 양수 영역으로 하나는 음수 영역으로 그리고 싶은 경우가 있다. 예를 들어, 서버 network in/out이나 disk의 read/write IO가 그런 경우이다.

{% include image.html file='/assets/img/2018-03-20-grafana-stack-positive-and-negative-graph-series1.png' alt='Stack positive and negative series' %}

위 스냅샷처럼 network in은 위쪽으로 out은 아래쪽으로 그릴 수 있다.

그래프 설정 `Display`탭에 `Series specific overrides`를 설정해 주면 된다. 그래프 라인 마다 특정 설정을 해줄 수 있는 기능이다. 일부 그래프는 라인으로 그리고 일부 그래프는 막대로 그리는 등의 설정을 할 수 있다.

{% include image.html file='/assets/img/2018-03-20-grafana-stack-positive-and-negative-graph-series2.png' alt='Stack positive and negative series setting' %}

여기서는 그래프 `alias`에서 `in`이 포함된 그래프는 그대로 두고, `out`이 포함된 그래프는 `Transform: negative-Y` 세팅해서 음수로 표현하는 설정이다.


# 참고

- [New stacking mode, Stack negative and positive series separately - Grafana github](https://github.com/grafana/grafana/issues/1360)
- [Graph Panel - Grafana docs](http://docs.grafana.org/features/panels/graph/)
