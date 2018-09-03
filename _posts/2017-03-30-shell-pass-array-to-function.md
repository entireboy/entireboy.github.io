---
layout: post
title:  "[Shell] Pass array to function - 함수에 배열 전달하기"
date:   2017-03-30 22:53:00 +0900
published: true
categories: [ shell ]
tags: [ shell, bash, script, pass, array, function ]
---

배열을 함수에 전달하고 싶을 때. (역시 매번 까먹어서 정리 =_=;;)

배열을 루프로 돌리고 싶을 때는 [[Shell] Loop though an array - for 루프에 배열 전달하기]({{ site.baseurl }}{% post_url 2017-03-27-shell-loop-through-an-array %})

`SERVERS[@]` 함수에 전달할 때는 골뱅이를 써주고, 함수에서는 전달인자처럼 사용하면 된다. 여기서는 첫번째 전달인자이기 때문에 `${!1}`을 사용하는데, 느낌표를 붙여줘서 배열 요소의 값을 참조하도록 한다.

```bash
function test() {
for SERVER in ${!1}
do
    echo ${SERVER}
done
}

SERVERS=(api01 api02 api03)
test SERVERS[@]
```

아래처럼 쓸 수도 있다. 배열의 요소를 함수에 전달하는 것이 아니라, 배열을 전달한 다음 함수에서 각 요소를 꺼내 사용한다.

```bash
function test() {
for SERVER in ${1}[@]
do
    echo ${!SERVER}
done
}

SERVERS=(api01 api02 api03)
test SERVERS
```

# 참고

- [[Shell] Loop though an array - for 루프에 배열 전달하기]({{ site.baseurl }}{% post_url 2017-03-27-shell-loop-through-an-array %})
