---
layout: post
title:  "[DbUnit] 여러 데이터셋 사용하기"
date:   2019-12-20 22:18:00 +0900
published: true
categories: [ dbunit ]
tags: [ dbunit, test, dataset, database, setup, teardown, xml, spock ]
---


# DbUnit 으로 여러 데이터셋을 사용하자

여기의 테스트는 spock 테스트이고, groovy 코드이다. `@DatabaseSetup` 등의 annotation에 배열(`[]`)이 사용된걸 중괄호(`{}`, curly braces)로 바꿔주면 (아마) 잘 될 것이다.

DbUnit을 쓰면서 데이터셋을 여러 xml 파일에 나눠담고, 필요할 때 마다 골라서 쓰려고 했다.

```groovy
@DatabaseSetup(connection = "dbUnitDatabaseConnection", value = ["order.xml", "refundedOrder.xml"], type = DatabaseOperation.CLEAN_INSERT)
@DatabaseTearDown(connection = "dbUnitDatabaseConnection", value = ["order.xml", "refundedOrder.xml"], type = DatabaseOperation.DELETE_ALL)
```

그런데, 막상 실행하면 이런 에러가..

```java
org.dbunit.dataset.NoSuchColumnException: order.RESULT_CODE -  (Non-uppercase input column: result_code) in ColumnNameToIndexes cache map. Note that the map's column names are NOT case sensitive.
    at org.dbunit.dataset.AbstractTableMetaData.getColumnIndex(AbstractTableMetaData.java:117) ~[dbunit-2.5.2.jar:na]
    at org.dbunit.dataset.AbstractTable.getColumnIndex(AbstractTable.java:78) ~[dbunit-2.5.2.jar:na]
    at org.dbunit.dataset.DefaultTable.getValue(DefaultTable.java:197) ~[dbunit-2.5.2.jar:na]
    at org.dbunit.dataset.CompositeTable.getValue(CompositeTable.java:119) ~[dbunit-2.5.2.jar:na]
    at org.dbunit.operation.InsertOperation.equalsIgnoreMapping(InsertOperation.java:145) ~[dbunit-2.5.2.jar:na]
    at org.dbunit.operation.AbstractBatchOperation.execute(AbstractBatchOperation.java:163) ~[dbunit-2.5.2.jar:na]
    at org.dbunit.operation.CompositeOperation.execute(CompositeOperation.java:79) ~[dbunit-2.5.2.jar:na]
    at com.github.springtestdbunit.DbUnitRunner.setupOrTeardown(DbUnitRunner.java:183) ~[spring-test-dbunit-1.3.0.jar:na]
    at com.github.springtestdbunit.DbUnitRunner.beforeTestMethod(DbUnitRunner.java:75) ~[spring-test-dbunit-1.3.0.jar:na]
    at com.github.springtestdbunit.DbUnitTestExecutionListener.beforeTestMethod(DbUnitTestExecutionListener.java:185) ~[spring-test-dbunit-1.3.0.jar:na]
    at org.springframework.test.context.TestContextManager.beforeTestMethod(TestContextManager.java:291) ~[spring-test-5.1.10.RELEASE.jar:5.1.10.RELEASE]
    at org.spockframework.spring.SpringTestContextManager.beforeTestMethod(SpringTestContextManager.java:60) [spock-spring-1.2-groovy-2.4.jar:1.2]
    at org.spockframework.spring.SpringInterceptor.interceptSetupMethod(SpringInterceptor.java:50) [spock-spring-1.2-groovy-2.4.jar:1.2]
    at org.spockframework.runtime.extension.AbstractMethodInterceptor.intercept(AbstractMethodInterceptor.java:30) [spock-core-1.2-groovy-2.4.jar:1.2]
    at org.spockframework.runtime.extension.MethodInvocation.proceed(MethodInvocation.java:97) [spock-core-1.2-groovy-2.4.jar:1.2]
    at org.spockframework.runtime.BaseSpecRunner.invoke(BaseSpecRunner.java:475) [spock-core-1.2-groovy-2.4.jar:1.2]
    at org.spockframework.runtime.BaseSpecRunner.runSetup(BaseSpecRunner.java:373) [spock-core-1.2-groovy-2.4.jar:1.2]
    at org.spockframework.runtime.BaseSpecRunner.runSetup(BaseSpecRunner.java:368) [spock-core-1.2-groovy-2.4.jar:1.2]
```


# 여러 데이터셋은 `@DatabaseSetups` 를 사용하자

이리저리 테스트 하다 보니 `@DatabaseSetups`를 사용해서 묶어주면 여러 xml의 데이터셋을 넣어주는걸 볼 수 있었다.

```groovy
@DatabaseSetups([
    @DatabaseSetup(connection = "dbUnitDatabaseConnection", value = ["order.xml"], type = DatabaseOperation.CLEAN_INSERT),
    @DatabaseSetup(connection = "dbUnitDatabaseConnection", value = ["refundedOrder.xml"], type = DatabaseOperation.INSERT),
])
@DatabaseTearDowns([
    @DatabaseTearDown(connection = "dbUnitDatabaseConnection", value = ["order.xml"], type = DatabaseOperation.DELETE_ALL),
    @DatabaseTearDown(connection = "dbUnitDatabaseConnection", value = ["refundedOrder.xml"], type = DatabaseOperation.DELETE_ALL),
])
```

여기서 조심할 점은 `@DatabaseSetup`의 `type`을 모두 `CLEAN_INSERT`으로 설정하면, 두번째 `@DatabaseSetup`으로 데이터셋을 넣을 때 첫번째 `@DatabaseSetup`이 넣은 데이터를 다 지워버린다는 것이다. 첫번째 `@DatabaseSetup`은 `CLEAN_INSERT`로 하고 두번째 이후 부터는 `INSERT`로 해주니 지워지지 않고 추가로 잘 들어간다.
