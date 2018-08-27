---
layout: post
title:  "[Shell] 배열 선언/만들기 (빈 배열도..)"
date:   2018-02-18 21:18:00 +0900
published: true
categories: [ shell ]
tags: [ shell, bash, script, create, declare, define, array, list, empty, append ]
---

bash에서 배열을 만들 때는 괄호로 싸면 된다. 또는 각 index에 할당하면 알아서 배열로 취급.. 또는 element를 더해주면 된다.

```bash
# 빈 배열
EMPTY_LIST=()

PLANETS=( "EARTH" "MARS" "VINUS" )
# ${PLANETS[0]} == "EARTH"
# ${PLANETS[1]} == "MARS"
# ${PLANETS[2]} == "VINUS"

PLACES[0]="HERE"
PLACES[1]="THERE"
PLACES[2]="WHERE"

NAMES=()
NAMES+=("ME")    # ${NAMES[0]} == "ME"
NAMES+=("YOU")   # ${NAMES[1]} == "YOU"
NAMES+=("THEM")  # ${NAMES[2]} == "THEM"
```

배열 순환(loop)하는 방법은 [요기]({{ site.baseurl }}{% post_url 2018-02-17-shell-looping-list %})



# 참고

- [Array Loops in Bash](http://stackabuse.com/array-loops-in-bash/)
- [배열 순환(loop)하는 방법]({{ site.baseurl }}{% post_url 2018-02-17-shell-looping-list %})
