---
layout: post
title:  "[Spring] repository와 페이징 오류"
date:   2019-03-26 22:18:00 +0900
published: true
categories: [ spring ]
tags: [ spring, jpa, repository, paging, Pageable, PageRequest ]
---

Spring repository에서는 `PageRequest`가 아닌 `Pageable`을 사용해야 한다.

```java
@Repository
public interface DealRepository extends JpaRepository<Deal, Long> {
    Page<Deal> findByBillingDate(LocalDate billingDate, Pageable pageable);
}
```

`Pageable`이 아닌 `PageRequest`를 사용하면 이래 같은 오류가 발생한다.

```java
org.springframework.dao.InvalidDataAccessApiUsageException: At least 2 parameter(s) provided but only 1 parameter(s) present in query.; nested exception is java.lang.IllegalArgumentException: At least 2 parameter(s) provided but only 1 parameter(s) present in query.
    at org.springframework.orm.jpa.EntityManagerFactoryUtils.convertJpaAccessExceptionIfPossible(EntityManagerFactoryUtils.java:373) ~[spring-orm-5.0.12.RELEASE.jar:5.0.12.RELEASE]
    at org.springframework.orm.jpa.vendor.HibernateJpaDialect.translateExceptionIfPossible(HibernateJpaDialect.java:225) ~[spring-orm-5.0.12.RELEASE.jar:5.0.12.RELEASE]
    at org.springframework.orm.jpa.AbstractEntityManagerFactoryBean.translateExceptionIfPossible(AbstractEntityManagerFactoryBean.java:527) ~[spring-orm-5.0.12.RELEASE.jar:5.0.12.RELEASE]
    at org.springframework.dao.support.ChainedPersistenceExceptionTranslator.translateExceptionIfPossible(ChainedPersistenceExceptionTranslator.java:61) ~[spring-tx-5.0.12.RELEASE.jar:5.0.12.RELEASE]
    at org.springframework.dao.support.DataAccessUtils.translateIfNecessary(DataAccessUtils.java:242) ~[spring-tx-5.0.12.RELEASE.jar:5.0.12.RELEASE]

...
Caused by: java.lang.IllegalArgumentException: At least 2 parameter(s) provided but only 1 parameter(s) present in query.
    at org.springframework.util.Assert.isTrue(Assert.java:134) ~[spring-core-5.0.12.RELEASE.jar:5.0.12.RELEASE]
    at org.springframework.data.jpa.repository.query.QueryParameterSetterFactory$CriteriaQueryParameterSetterFactory.create(QueryParameterSetterFactory.java:289) ~[spring-data-jpa-2.0.13.RELEASE.jar:2.0.13.RELEASE]
    at org.springframework.data.jpa.repository.query.ParameterBinderFactory.lambda$createQueryParameterSetter$1(ParameterBinderFactory.java:139) ~[spring-data-jpa-2.0.13.RELEASE.jar:2.0.13.RELEASE]
    at java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:193) ~[na:1.8.0_201]
    at java.util.Spliterators$ArraySpliterator.tryAdvance(Spliterators.java:958) ~[na:1.8.0_201]
    at java.util.stream.ReferencePipeline.forEachWithCancel(ReferencePipeline.java:126) ~[na:1.8.0_201]
    at java.util.stream.AbstractPipeline.copyIntoWithCancel(AbstractPipeline.java:498) ~[na:1.8.0_201]
    at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:485) ~[na:1.8.0_201]
    at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:471) ~[na:1.8.0_201]
```
