---
layout: post
title:  "[Database Rider] 테이블명을 대문자(uppercase)로만 참조하려고 하는 문제"
date:   2020-11-09 22:18:00 +0900
published: true
categories: [ querydsl ]
tags: [ database rider, dbunit, dataset, table, name, lowercase, uppercase, case sensitive ]
---

# 문제

Integration test 데이터 셋 설정으로 Database Rider를 사용하는데, 아래와 같은 오류가 발생한다. 이상한 점은 내 테이블 설정은 모두 소문자(lowercase)인 `user`인데, 대문자(uppercase)인`USER`로 참조한다는 것이다.

```bash
java.lang.RuntimeException: Could not create dataset for test '$spock_feature_1_1'.
	at com.github.database.rider.core.RiderRunner.runBeforeTest(RiderRunner.java:47) ~[rider-core-1.17.0.jar:na]
	at com.github.database.rider.spring.DBRiderTestExecutionListener.beforeTestMethod(DBRiderTestExecutionListener.java:25) ~[rider-spring-1.17.0.jar:na]
	.. (중략) ..
Caused by: com.github.database.rider.core.exception.DataBaseSeedingException: Could not initialize dataset: datasets/user.xml
	.. (중략) ..
Caused by: org.dbunit.dataset.DataSetException: Exception while searching the dependent tables.
	.. (중략) ..
Caused by: org.dbunit.util.search.SearchException: org.dbunit.dataset.NoSuchTableException: The table 'USER' does not exist in schema 'null'
	.. (중략) ..
Caused by: org.dbunit.dataset.NoSuchTableException: The table 'USER' does not exist in schema 'null'
	at org.dbunit.database.search.AbstractMetaDataBasedSearchCallback.getNodes(AbstractMetaDataBasedSearchCallback.java:186) ~[dbunit-2.5.3.jar:na]
	at org.dbunit.database.search.AbstractMetaDataBasedSearchCallback.getNodes(AbstractMetaDataBasedSearchCallback.java:149) ~[dbunit-2.5.3.jar:na]
	... 47 common frames omitted
```

엔티티에는 아래처럼 테이블명이 소문자 `user`로 되어 있다.

```kotlin
@Entity
@Table(name = "user")
class User(
  ..
)
```


# 원인 및 해결방법

Database Rider 는 내부적으로 DbUnit 을 사용하고 있다. 그리고 간단하게 DbUnit 을 설정할 수 있는 방법도 제공한다. ([설정 방법](https://database-rider.github.io/database-rider/latest/documentation.html?theme=foundation#_dbunit_configuration))

아래는 `src/test/resources/dbunit.yml` 파일의 기본값이다. yml 파일 대신 `@DBUnit` annotation 을 사용하는 방법도 있다.

```yaml
cacheConnection: true
cacheTableNames: true
leakHunter: false
caseInsensitiveStrategy: !!com.github.database.rider.core.api.configuration.Orthography 'UPPERCASE'
properties:
  batchedStatements:  false
  qualifiedTableNames: false
  schema: ""
  caseSensitiveTableNames: false
  batchSize: 100
  fetchSize: 100
  allowEmptyFields: false
  escapePattern: ""
connectionConfig:
  driver: ""
  url: ""
  user: ""
  password: ""
```

여기서 문제가 되는 포인트는 `caseInsensitiveStrategy`와 `properties.caseSensitiveTableNames`설정이다.
- `properties.caseSensitiveTableNames`: 테이블명의 대소문자 구분 여부. `true`인 경우 대소문자를 구분한다.
- `caseInsensitiveStrategy`: `properties.caseSensitiveTableNames` 값이 `false`일 때만 유효한 설정으로, 대소문자를 어떻게 처리할지 결정하는 값이다. `UPPERCASE`와 `LOWERCASE`만 가능하다.

이 설정들 때문에 `user` 대신 `USER` 라는 이름으로 테이블명을 참조하는 것이다. 간단하게 `LOWERCASE`로 바꿔주면 된다.

```yaml
caseInsensitiveStrategy: !!com.github.database.rider.core.api.configuration.Orthography 'LOWERCASE'
```


# 참고

- [4.2. DBUnit configuration - Database Rider Documentation](https://database-rider.github.io/database-rider/latest/documentation.html?theme=foundation#_dbunit_configuration)
