---
layout: post
title:  "[Hibernate] envers REV(revision number)를 long으로 바꾸기"
date:   2019-06-04 22:18:00 +0900
published: true
categories: [ hibernate ]
tags: [ hibernate, envers, audit, history, rev, revision, long, bigint, integer, int, db, database ]
---

# Hibernate Envers module

Hibernate에 audit을 자동으로 기록해 주는 [envers](https://hibernate.org/orm/envers/) 라는 모듈이 있다. entity를  수정하면 audit 테이블에 바뀐 값을 그대로 기록해 주기 때문에 별도의 history 관리가 필요 없어서 편하다. 단점이라면 한 번 envers를 적용하면 hibernate를 사용하지 않고 데이터를 조작하면 이력을 제대로 파악하기 어렵다는 점이다.

큰 설정 없이 사용하면 envers가 설정된 entity를 추가/수정/삭제 할 때 마다 `REVINFO`라는 테이블에 `REV(revision number)`가 하나씩 증가하고 audit 테이블에 FK로 추가돼서 이력을 확인할 수 있다. 문제는 envers 모듈이 `REV`를 기본적으로 `INT` column에 `java.lang.Integer`를 사용한다는 점이다. `REV`는 DB transaction 단위로 증가하는데, 여러 테이블이 함께 사용되면 `REV` 숫자가 빠르게 소모되어 금방 `Integer.MAX`에 다다를 수 있다.


# Integer.MAX Problem

`REVINFO` 테이블에 있는 `REV`를 강제로 `Integer.MAX`로 바꾸고 entity를 수정하면 아래와 같은 오류와 함께 모든 transaction이 rollback되어 버린다. audit 정보 뿐만 아니라 내 서비스에서 수정한 모든 것들이 rollback된다. audit 넣으려고 추가한 것들 때문에 소듕한 내 서비스가.. T_T

```java
DEBUG 10:18:08.611 [http-nio-8080-exec-6] org.hibernate.SQL -
    /* insert org.hibernate.envers.DefaultRevisionEntity
        */ insert
        into
            REVINFO
            (REVTSTMP)
        values
            (?)
WARN  10:18:08.657 [http-nio-8080-exec-6] w.a.b.api.config.ApiExceptionHandler - An unexpected exception occurred
org.springframework.dao.DataIntegrityViolationException: could not execute statement; SQL [n/a]; constraint [PRIMARY]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement
    at org.springframework.orm.jpa.vendor.HibernateJpaDialect.convertHibernateAccessException(HibernateJpaDialect.java:257)
    at org.springframework.orm.jpa.vendor.HibernateJpaDialect.translateExceptionIfPossible(HibernateJpaDialect.java:223)
    at org.springframework.orm.jpa.JpaTransactionManager.doCommit(JpaTransactionManager.java:540)

.. 어쩌구 저쩌구 ..

Caused by: java.sql.SQLIntegrityConstraintViolationException: (conn=151) Duplicate entry '2147483647' for key 'PRIMARY'
    at org.mariadb.jdbc.internal.util.exceptions.ExceptionMapper.get(ExceptionMapper.java:171)
    at org.mariadb.jdbc.internal.util.exceptions.ExceptionMapper.getException(ExceptionMapper.java:110)
    at org.mariadb.jdbc.MariaDbStatement.executeExceptionEpilogue(MariaDbStatement.java:228)
    at org.mariadb.jdbc.MariaDbPreparedStatementClient.executeInternal(MariaDbPreparedStatementClient.java:216)
    at org.mariadb.jdbc.MariaDbPreparedStatementClient.execute(MariaDbPreparedStatementClient.java:150)
    at org.mariadb.jdbc.MariaDbPreparedStatementClient.executeUpdate(MariaDbPreparedStatementClient.java:183)
    at com.zaxxer.hikari.pool.ProxyPreparedStatement.executeUpdate(ProxyPreparedStatement.java:61)
    at com.zaxxer.hikari.pool.HikariProxyPreparedStatement.executeUpdate(HikariProxyPreparedStatement.java)
    at org.hibernate.engine.jdbc.internal.ResultSetReturnImpl.executeUpdate(ResultSetReturnImpl.java:175)
    ... 111 common frames omitted
Caused by: java.sql.SQLException: Duplicate entry '2147483647' for key 'PRIMARY'
Query is: /* insert org.hibernate.envers.DefaultRevisionEntity */ insert into REVINFO (REVTSTMP) values (?), parameters [1559611088610]
    at org.mariadb.jdbc.internal.util.LogQueryTool.exceptionWithQuery(LogQueryTool.java:153)
    at org.mariadb.jdbc.internal.protocol.AbstractQueryProtocol.executeQuery(AbstractQueryProtocol.java:255)
    at org.mariadb.jdbc.MariaDbPreparedStatementClient.executeInternal(MariaDbPreparedStatementClient.java:209)
    ... 116 common frames omitted
```


# INT/Integer를 BIGINT/Long으로 바꾸기

근본적인 해결방법은 아니지만 `REV`의 타입을 `INT`에서 `BIGINT`로 사이즈 변경하는 정도로 커버가 가능한 시스템이라면, 아래 샘플처럼 `RevisionEntity`를 커스텀하게 만들어서 사용하는 방법도 있다. 샘플에서는 default로 사용하는 revision table(`REVINFO`)과 column명(`REV`, `REVTSTMP`)을 그대로 사용했다. ([DefaultRevisionEntity](https://github.com/hibernate/hibernate-orm/blob/master/hibernate-envers/src/main/java/org/hibernate/envers/DefaultRevisionEntity.java) 참고)

```java
import lombok.*;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@RevisionEntity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "REVINFO")
public class CustomRevisionEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @RevisionNumber
    @EqualsAndHashCode.Include
    @Column(name = "REV")
    private Long id;

    @RevisionTimestamp
    @EqualsAndHashCode.Include
    @Column(name = "REVTSTMP")
    private Long timestamp;


    @Transient
    public Date getRevisionDate() {
        return new Date(timestamp);
    }

    @Override
    public String toString() {
        return String.format("LongRevisionEntity(id = %d, revisionDate = %s)",
            id, DateFormat.getDateTimeInstance().format(getRevisionDate()));
    }

}
```

이제 `REV` column은 `java.lang.Long`으로 바꾸고 `BIGINT`를 사용하면 된다.

```sql
CREATE TABLE REVINFO (
    REV BIGINT(20) NOT NULL AUTO_INCREMENT,
    REVTSTMP BIGINT(20),
    PRIMARY KEY (REV)
) ENGINE=InnoDB;
```

`@RevisionEntity`와 `@RevisionNumber`, `@RevisionTimestamp`만 잘 설정해 주면 된다.

> **NOTE:** 이렇게 `@RevisionEntity`를 만들면 envers가 사용하는 모든 revision 기록은 이 entity를 사용하게 된다.


# 참고

- [Hibernate Envers rev column data type is Integer](https://stackoverflow.com/questions/38589065/hibernate-envers-rev-column-data-type-is-integer)
- [DefaultRevisionEntity.java - hibernate-orm/hibernate-envers](https://github.com/hibernate/hibernate-orm/blob/master/hibernate-envers/src/main/java/org/hibernate/envers/DefaultRevisionEntity.java)
