---
layout: post
title:  "[Shell] bash 스크립트에서 변수 선언여부 체크"
date:   2017-11-25 21:18:00 +0900
published: true
categories: [ shell ]
tags: [ shell, bash, check, set, var, variable, script ]
---

bash 스크립트에서 변수가 선언되어 있지 않으면 스크립트를 종료시키는 설정을 할 수 있다. 이 설정이 되어 있는 경우 변수 선언여부를 체크하지 않고 사용하면 원치 않는 스크립트 종료가 일어날 수 있다.

이 때 변수 선언여부를 체크하려면 `-v`로 test하면 된다. `test` 관련 help 페이지는 아래 명령으로 확인할 수 있다. (`-v` 옵션은 bash 4.2 이상 버전에서 사용할 수 있다.)

```Bash
$ help test
test: test [expr]
    Evaluate conditional expression.

    Exits with a status of 0 (true) or 1 (false) depending on the evaluation of EXPR.

    .. 어쩌구 저쩌구 ..

    Other operators:

      -o OPTION      True if the shell option OPTION is enabled.
      `-v VAR True if the shell variable VAR is set`
      ! EXPR         True if expr is false.
      EXPR1 -a EXPR2 True if both expr1 AND expr2 are true.
      EXPR1 -o EXPR2 True if either expr1 OR expr2 is true.
```

주의할 점은 `-v`의 변수로 변수명을 적어줘야 한다는 것이다.

```bash
#!/bin/bash
echo "START"
#BUILD_USER_ID="whoAmI"
if [[ -v BUILD_USER_ID && -n ${BUILD_USER_ID} ]]
then
  echo "set and not blank"
fi
echo "END"
```

```bash
$ ./test
START
END
$ # 주석을 풀어서 `BUILD_USER_ID`가 선언된 경우 아래처럼 출력된다.
$ ./test.sh
START
set and not blank
END
```



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
