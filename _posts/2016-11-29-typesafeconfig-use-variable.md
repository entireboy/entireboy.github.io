---
layout: post
title:  "[Typesafe Config] 변수 사용하기"
date:   2016-11-29 23:41:00 +0900
categories: [ typesafe, config ]
tags: [ typesafe config, typesafe, config, variable, java ]
---

[Typesafe Config](https://github.com/typesafehub/config)를 사용하는 프로젝트에서 시스템의 environment variable 이나 Java system properties를 사용할 수 있다. 아래처럼 `${VAR}` 형식으로 써주면 된다.

```javascript
http {
  base-url=${BASE_URL}
  port=8080
}
```

아래와 같이 java 명령의 `-D` 옵션을 주거나, 시스템 환경변수를 넣어주면 된다.

```bash
java -DBASE_URL=myhost.com MyClass
```

`${VAR}`와 비슷하게 `${?VAR}`를 사용할 수 있다. `${?VAR}`는 해당 변수에 설정되어 있는 경우에만 실행이 된다. 아래처럼 설정되어 있을 때 `-D` 옵션 등으로 `HTTP_PORT` 변수를 설정해 주지 않으면 `http.port`는 값이 없게 된다.

```javascript
http {
  port=${?HTTP_PORT}
}
```

조심할 점은 **아래처럼 ""안에 들어 있으면 치환되지 않으니 조심**하자.

```javascript
http {
  base-url="http://localhost:${HTTP_PORT}/some/path"
}
```

# 참고
- [[Typesafe Config] config의 default값 설정](/notes/2016/11/30/typesafeconfig-default-value)
- [https://github.com/typesafehub/config](https://github.com/typesafehub/config)
- [http://blog.michaelhamrah.com/2014/02/leveraging-typesafes-config-library-across-environments/](http://blog.michaelhamrah.com/2014/02/leveraging-typesafes-config-library-across-environments/)
