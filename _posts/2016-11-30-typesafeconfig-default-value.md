---
layout: post
title:  "[Typesafe Config] config의 default값 설정"
date:   2016-11-30 00:05:00 +0900
published: true
categories: [ typesafe, config ]
tags: [ typesafe config, typesafe, config, variable, default, java ]
---

[Typesafe Config](https://github.com/typesafehub/config)는 Scala [sbt](http://www.scala-sbt.org/)처럼 라인단위로 실행이 되면서, 설정이 되는 형태의 config이다. (Maven처럼 선언적으로 설정하는 방식이 아니다.)

때문에, 하나의 key를 여러번 설정할 수도 있다. 아래 같이 설정하면, `http.port`의 값은 9090이 된다. (8080으로 설정된 다음, 다시 9090으로 덮어 써진다.)

```javascript
http {
  port=8080
  port=9090
}
```

이 방법과 [변수설정](/notes/2016/11/29/typesafeconfig-use-variable)을 이용해서 default값을 설정할 수 있다.

```javascript
http {
  host=localhost
  host=${?HTTP_HOST}
  port=8080
  port=${?HTTP_PORT}
}
```

`${VAR}`와는 다르게 `${?VAR}`는 변수에 설정된 값이 있는 경우에만 실행이 된다. java 명령 실행 시 `-D` 옵션으로 `HTTP_PORT`의 값을 주지 않는 경우 `http.port`는 8080이 되고, `HTTP_PORT` 값을 설정해 주면 다시 한번 덮어써져서 설정된 값으로 사용 가능하다. 시스템의 environment variables로 설정해 줘도 된다.

```bash
java -DHTTP_PORT=9090 MyClass
java MyClass
```

# 참고
- [[Typesafe Config] 변수 사용하기](/notes/2016/11/29/typesafeconfig-use-variable)
- [https://github.com/typesafehub/config](https://github.com/typesafehub/config)
- [http://blog.michaelhamrah.com/2014/02/leveraging-typesafes-config-library-across-environments/](http://blog.michaelhamrah.com/2014/02/leveraging-typesafes-config-library-across-environments/)
