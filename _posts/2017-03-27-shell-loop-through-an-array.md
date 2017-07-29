---
layout: post
title:  "[Shell] Loop though an array - for 루프에 배열 전달하기"
date:   2017-03-27 22:53:00 +0900
published: true
categories: [ shell ]
tags: [ shell, bash, loop, for, array ]
---

shell script로 for loop을 돌리는 방법은 많다. 그 중 배열로 된 변수를 간단히 돌리는 방법..
(매번 쓸 때 마다 까먹어서 정리 =_=;;)

`${FILES[@]}` for 루프의 `in` 절에 골뱅이를 써주면 된다. 골뱅이는 배열의 각 요소를 뜻한다.

```bash
FILES=("a" "b" "c")
# 배열을 생성할 때 이렇게 각 배열의 index로 값을 지정해 줘도 된다.
#FILES[0]="a"
#FILES[1]="b"
#FILES[2]="c"

for i in ${FILES[@]}
do
    echo "FILES ${i}"
    cat ${i}
done
```

# 참고2

- [[Shell] Pass array to function - 함수에 배열 전달하기]({% post_url 2017-03-30-shell-pass-array-to-function %})
