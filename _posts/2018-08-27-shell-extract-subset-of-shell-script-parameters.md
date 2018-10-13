---
layout: post
title:  "[Shell] 함수 전달인자 중 일부만 사용하기 (bash)"
date:   2018-08-27 23:18:00 +0900
published: true
categories: [ shell ]
tags: [ shell, bash, script, function, parameter, argument, subset, sublist, extract ]
---

shell script의 함수에 넘겨준 전달인자 중 앞부분 일부를 떼고 나머지만 사용하고 싶은 경우가 있다. `${@}`는 전달인자 전체를 의미하고, `:`로 전달인자를 자를 수 있다.

```bash
#!/bin/bash

subs() {
  echo $@       # 전체
  echo ${@:1}   # 첫번째 인자 부터 - 전체와 동일
  echo ${@:2}   # 두번째 인자 부터 - 첫번째 인자 제외
  echo ${@:2:3} # 두번째 인자 부터 3개
  echo ${@:4}
}

subs 1 2 3 4 5 6 7
```

위 코드를 실행하면 아래와 같은 결과가 나온다.

```bash
$ test.sh
1 2 3 4 5 6 7
1 2 3 4 5 6 7
2 3 4 5 6 7
2 3 4
4 5 6 7
```


# 참고

- [Process all arguments except the first one (in a bash script)](https://stackoverflow.com/questions/9057387/process-all-arguments-except-the-first-one-in-a-bash-script)
- [$@ except the 1st argument](https://unix.stackexchange.com/questions/225943/except-the-1st-argument)
