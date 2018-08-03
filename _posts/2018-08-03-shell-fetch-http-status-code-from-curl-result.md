---
layout: post
title:  "[Shell] curl로 호출하고 HTTP status code 확인하기"
date:   2018-08-03 23:18:00 +0900
published: true
categories: [ jenkins ]
tags: [ shell, bash, curl, http, http status code, status code, status, code, response, result ]
---

curl로 URL을 호출한 뒤 HTTP status code를 확인하고 싶다면, `-w` 옵션으로 찍어보면 된다. [curl man page](https://linux.die.net/man/1/curl)를 보면 아래와 같이 curl의 stdout 출력 이후에 `-w` 옵션으로 적어준 내용이 출력된다고 적혀 있다.

> `-w/--write-out <format>`
> Defines what to display on stdout after a completed and successful operation.

[man page](https://linux.die.net/man/1/curl)에는 걸린 시간(`time_total`, `time_namelookup`, `time_connect` 등), 요청/응답 사이즈(`size_download`, `size_upload`, `size_header` 등)처럼 사용할 수 있는 많은 옵션들이 있으니 참고 하자.

다음은 stdout 뒤에 status code, 요청/응답 사이즈 등을 찍는 예제이다.

```bash
$ curl -w " - status code: %{http_code}, sizes: %{size_request}/%{size_download}" "http://blog.leocat.kr/"
<!DOCTYPE html>
    ... 생략 ...
</body>

</html>
- status code: 200, sizes: 78/43631

$ # -w 옵션 없이 호출했을 때의 stdout은 </html>에서 끝난다.
$ curl "http://blog.leocat.kr/"
<!DOCTYPE html>
    ... 생략 ...
</body>

</html>
$
```

stdout 없이 status code만 필요하다면 아래와 같이 `-o` 옵션을 활용할 수도 있다.

```bash
#!/bin/bash
RES_CODE=$(curl -o /dev/null -w "%{http_code}" "http://blog.leocat.kr/")

if [ $STATUS -eq 200 ]; then
  echo "OKOK"
fi
```


# 참고

- [curl(1) - Linux man page](https://linux.die.net/man/1/curl)
- [Use HTTP status codes from curl](https://coderwall.com/p/taqiyg/use-http-status-codes-from-curl)
