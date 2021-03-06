---
layout: post
title:  "[DbUnit] 여러 데이터셋 사용하기"
date:   2019-12-20 22:18:00 +0900
published: true
categories: [ dbunit ]
tags: [ dbunit, test, dataset, database, setup, teardown, xml, spock ]
---


# DbUnit 으로 여러 데이터셋을 사용하자

> 여기의 테스트는 spock 테스트이고, groovy 코드이다. `@DatabaseSetup` 등의 annotation에 배열(`[]`)이 사용된걸 중괄호(`{}`, curly braces)로 바꿔주면 (아마) 잘 될 것이다.

DbUnit을 쓰면서 데이터셋을 여러 xml 파일에 나눠담고, 필요할 때 마다 골라서 쓰려고 했다.

```groovy
@DatabaseSetup(connection = "dbUnitDatabaseConnection",
               value = ["order.xml", "refundedOrder.xml"],
               type = DatabaseOperation.CLEAN_INSERT)
@DatabaseTearDown(connection = "dbUnitDatabaseConnection",
                  value = ["order.xml", "refundedOrder.xml"],
                  type = DatabaseOperation.DELETE_ALL)
```

그런데, 막상 실행하면 이런 에러가..

```java
org.dbunit.dataset.NoSuchColumnException: order.REFUNDED_AT -  (Non-uppercase input column: refunded_at) in ColumnNameToIndexes cache map. Note that the map's column names are NOT case sensitive.
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


# 원인: Column 이 다른 데이터셋 사용

이런저런 테스트를 해보니, 두 데이터셋의 column이 서로 다르면 이런 현상이 생기기도 한다.

```xml
<!-- order.xml -->
<?xml version="1.0" encoding="UTF-8"?>
<dataset>
  <order id="1" user_id="123" sales_price="10000"
         ordered_at="2019-12-20 00:00:00.000000" />
  <order_item id="1" order_id="1" item_id="100" />
  
  <order id="2" user_id="123" sales_price="20000"
         ordered_at="2019-12-20 00:00:00.000000" />
  <order_item id="2" order_id="2" item_id="200" />
  <order_item id="3" order_id="2" item_id="300" />
</dataset>


<!-- refundedOrder.xml -->
<?xml version="1.0" encoding="UTF-8"?>
<dataset>
  <order id="101" user_id="123" sales_price="10000"
         ordered_at="2019-12-20 00:00:00.000000" refunded_at="2019-12-20 00:10:00.000000" />
  <order_item id="101" order_id="101" item_id="100" />
</dataset>
```

예를 들어, `order.xml` 파일에는 환불이 없는 주문 데이터만 담겨 있고, `refundedOrder.xml` 파일에는 환불도 포함되어 있다고 하자. 환불 데이터는 다른 테이블에 저장되고 `order` 테이블에는 환불이 된 경우 최초 환불시간을 `refunded_at` column에 넣는다. DbUnit 특성상 column을 `NULL`로 채우기 위해서는 xml 파일에서 필드를 제거해야 한다. 그래서 `order.xml` 파일에는 `refunded_at` 필드가 없고, `refundedOrder.xml` 파일에는 `refunded_at` 필드가 존재하는 상황이 되어 위와 같은 오류 메시지를 볼 수 있다.


# 여러 데이터셋은 `@DatabaseSetups` 를 사용하자

이리저리 찾고 테스트 하다 보니 `@DatabaseSetups`를 찾을 수 있었다. 여러 xml의 데이터셋을 넣어주니, 어머 너무 잘 동작한다. +_+d 필드가 서로 달라도 잘 동작한다.

```groovy
@DatabaseSetups([
    @DatabaseSetup(connection = "dbUnitDatabaseConnection",
                   value = ["order.xml"],
                   type = DatabaseOperation.CLEAN_INSERT),
    @DatabaseSetup(connection = "dbUnitDatabaseConnection",
                   value = ["refundedOrder.xml"],
                   type = DatabaseOperation.INSERT),
])
@DatabaseTearDowns([
    @DatabaseTearDown(connection = "dbUnitDatabaseConnection",
                      value = ["order.xml"],
                      type = DatabaseOperation.DELETE_ALL),
    @DatabaseTearDown(connection = "dbUnitDatabaseConnection",
                      value = ["refundedOrder.xml"],
                      type = DatabaseOperation.DELETE_ALL),
])
```

단, 여기서 **조심할 점**은 `@DatabaseSetup`의 `type`을 모두 `CLEAN_INSERT`으로 설정하면, 두번째 `@DatabaseSetup`으로 데이터셋을 넣을 때 첫번째 `@DatabaseSetup`이 넣은 데이터를 다 지워버린다는 것이다. 첫번째 `@DatabaseSetup`은 `CLEAN_INSERT`로 하고 두번째 이후 부터는 `INSERT`로 해주니 지워지지 않고 추가로 잘 들어간다.
