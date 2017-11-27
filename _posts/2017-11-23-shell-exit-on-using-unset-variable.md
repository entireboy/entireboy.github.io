---
layout: post
title:  "[Shell] 선언되지 않은 변수 사용 시 스크립트 종료"
date:   2017-11-23 21:18:00 +0900
published: true
categories: [ shell ]
tags: [ shell, bash, exit, unset, unbound, variable, var, script ]
---

bash 스크립트는 변수를 사용하기 전에 먼저 선언되거나 할당되지 않으면 없는듯이 스쳐지나간다.

만일, 미리 선언되지 않은 변수를 사용했을 때 스크립트를 종료시키고 싶다면 이 옵션을 주면 된다.

```bash
set -o nounset
# 또는
set -u
```

테스트 스크립트를 만들어서 고고!!

```bash
#!/bin/bash
set -o nounset

echo "START"
echo ${UNKNOWN_VAR}
echo "END"
```

결과는 아래처럼 나온다. `UNKNOWN_VAR`를 찾지 못 한다고 하면서 `END`를 출력하지 않고 스크립트를 종료해 버린다.

```bash
$ ./test.sh
START
./test.sh: line 5: `UNKNOWN_VAR`: unbound variable
```


# 참고

- [Best Practices for Writing Bash Scripts](https://kvz.io/blog/2013/11/21/bash-best-practices/)
