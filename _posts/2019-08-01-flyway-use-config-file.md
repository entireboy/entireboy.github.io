---
layout: post
title:  "[Flyway] 환경별 config 파일 사용하기"
date:   2019-08-01 22:18:00 +0900
published: true
categories: [ flyway ]
tags: [ flyway, db, database, version control, gradle, plugin, command, config ]
---

# Flyway와 gradle

[Flyway](https://flywaydb.org/)를 gradle plugin과 함께 사용하면 `build.gralde` 파일에 아래와 비슷한 설정으로 사용할 것이다.

```groovy
flyway {
    url = "jdbc:mysql://localhost:3306/my_service"
    user = "root"
    password = "root"
    locations = ["filesystem:${file('src/flyway/my-service').absolutePath}"]
    encoding = 'UTF-8'
    outOfOrder = true // 여러브랜치에서 서로 다른 날짜로 만들어도 작동하도록
    validateOnMigrate = true
}
```

로컬에 설정할 때는 간단한 아래 명령으로 flyway를 실행하면 되기 때문에 편하다.

```bash
$ ./gradlew flywayMigrate
# 또는
$ ./gradlew :my-module:flywayMigrate
```


# 여러 환경/설정에서 사용하기

실서비스 용 DB는 flyway를 사용하지 않겠지만, dev나 beta DB는 로컬이나 jenkins 등으로 flyway를 실행시킬 것이다. 같은 코드(쿼리)로 flyway를 다른 DB에서 실행시키기 위해 config file을 각 설정 별로 사용하면 된다. 예를 들어, beta 환경은 아래와 같은 내용으로 `src/flyway/beta.conf` 파일을 만들어 두고, 실행 시에 이 파일을 지정할 수 있다.

```
flyway.url=jdbc:mysql://my-db.leocat.kr/my_service_beta
flyway.user=rootuser
flyway.password=rootuser
flyway.locations=filesystem:my-module/src/flyway/my-service
flyway.encoding=UTF-8
flyway.outOfOrder=true
flyway.validateOnMigrate=true
```

gradle plugin을 사용한다면 `-Dflyway.configFiles` 옵션을 주면 되고, flyway 명령을 사용한다면 `-configFiles` 옵션을 주면 된다.

```bash
$ ./gradlew -Dflyway.configFiles=src/flyway/beta.conf flywayMigrate
$ ./gradlew -Dflyway.configFiles=src/flyway/beta.conf :my-module:flywayMigrate
$ flyway -configFiles=src/flyway/beta.conf migrate
```


# 참고

- [Flyway](https://flywaydb.org/)
- [Flyway Gradle Plugin](https://flywaydb.org/documentation/gradle/)
- [Flyway Command-line tool](https://flywaydb.org/documentation/commandline/)
- [Config Files - Flyway doc](https://flywaydb.org/documentation/configfiles)
