---
layout: post
title:  "[DbUnit] Nullable column 문제"
date:   2025-01-21 22:18:00 +0900
published: true
categories: [ dbunit ]
tags: [ dbunit, test, dataset, database, setup, xml, database rider ]
---

DBUnit은 테이블의 스키마 정보를 알지 못 한다. 따라서 dataset의 첫번째 데이터를 기준으로 데이터를 준비한다. 만일 아래와 같은 dataset이 있다면, `job` 컬럼은 무시될 것이다.

```xml
<dataset>
  <user id="1" name="thDeng" />
  <user id="2" name="photoDeng" job="photographer" />
</dataset>
```

다음과 같이 `job` 컬럼은 무시된다는 로그를 발견할 수 있고 쿼리문에도 컬럼이 없는 것을 볼 수 있다. 하지만, 사실 테스트 코드에서 로그는 잘 안 보게 되어 무시되는 경우가 많아서 항상 발견이 늦다.

```
org.dbunit.dataset.xml.FlatXmlProducer   : Extra columns (job) on line 1 for table user (global line number is 8). Those columns will be ignored.

o.m.j.i.logging.ProtocolLoggingProxy     : conn=344(M) - 5.062 ms - Query: insert into `user` (`id`, `name`) values (?, ?), parameters [1, 'thDeng']
o.m.j.i.logging.ProtocolLoggingProxy     : conn=344(M) - 5.062 ms - Query: insert into `user` (`id`, `name`) values (?, ?), parameters [2, 'photoDeng']
```

# column sensing

이런 문제를 보완하기 위해 DBUnit은 `columnSensing` 설정이 있다. `columnSensing = true`로 설정하면 모든 xml 파일을 읽어서 새로운 컬럼(위의 예제에서 `job` 컬럼)이 나타나면 자동으로 추가할 수 있다.

```
o.m.j.i.logging.ProtocolLoggingProxy     : conn=344(M) - 5.062 ms - Query: insert into `user` (`id`, `name`, `job`) values (?, ?, ?), parameters [1, 'thDeng', <null>]
o.m.j.i.logging.ProtocolLoggingProxy     : conn=344(M) - 5.062 ms - Query: insert into `user` (`id`, `name`, `job`) values (?, ?, ?), parameters [2, 'photoDeng', 'photographer']
```

# not null column

문제는 not null 설정이 되어 있는 컬럼에서 발생한다.

`user` 테이블의 `job` 컬럼이 `not null` 이고 기본값으로 `unemployed`가 주어졌다고 하자. (아래 DDL은 MySQL 구문이다.)

```
CREATE TABLE `user`
(
    id    BIGINT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,

    name  VARCHAR(100) NOT NULL COMMENT '이름',
    job   VARCHAR(100) NOT NULL DEFAULT 'unemployed' COMMENT '직업'
) ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COMMENT = '사용자 정보';
```

 이 테이블의 `job`컬럼에 DEFAULT 설정이 되어 있으니 `thDeng` 사용자의 `job`이 `unemployed`로 채워지길 기대하게 된다. 

```xml
<dataset>
  <user id="1" name="thDeng" />
  <user id="2" name="photoDeng" job="photographer" />
</dataset>
```

`columnSensing = true`로 설정하고 동일한 dataset을 사용하면 아래와 같은 오류를 만나게 된다.

```
Could not create dataset for test 'save'.
java.lang.RuntimeException: Could not create dataset for test 'save'.
	   .. (생략) ..
Caused by: com.github.database.rider.core.exception.DataBaseSeedingException: Could not initialize dataset: datasets/user.xml, datasets/account.xml, datasets/company.xml
	   .. (생략) ..
Caused by: org.dbunit.DatabaseUnitException: Exception processing table name='user'
	   .. (생략) ..
Caused by: java.sql.BatchUpdateException: (conn=23) Column 'job' cannot be null
	   .. (생략) ..
```

이유는 실행되는 쿼리를 다시 자세히 보면 바로 알 수 있다. 위에서 살펴본 쿼리를 다시 가져왔다.

```
o.m.j.i.logging.ProtocolLoggingProxy     : conn=344(M) - 5.062 ms - Query: insert into `user` (`id`, `name`, `job`) values (?, ?, ?), parameters [1, 'thDeng', <null>]
o.m.j.i.logging.ProtocolLoggingProxy     : conn=344(M) - 5.062 ms - Query: insert into `user` (`id`, `name`, `job`) values (?, ?, ?), parameters [2, 'photoDeng', 'photographer']
```

우리는 아래처럼 `thDeng` 사용자의 `job`컬럼이 없는 insert문이 실행될 것을 기대했지만, 실제로는 값을 `<null>`로 채워서 보내고 있는 것이다.


```
// 기대했던 쿼리
insert into `user` (`id`, `name`) values (?, ?), parameters [1, 'thDeng']
insert into `user` (`id`, `name`, `job`) values (?, ?, ?), parameters [2, 'photoDeng', 'photographer']
```

# 결론

- DBUnit은 dataset의 첫번째 데이터를 기준으로 스키마를 구성한다. 다음 데이터에 컬럼이 추가되면 무시된다.
- 새로운 컬럼이 무시되지 않도록 `columnSensing = true`로 설정하여 xml의 모든 컬럼을 가진 데이터를 만들 수 있다.
- `columnSensing = true`로 설정한 경우 not null 필드는 모든 데이터에 값을 설정하거나 설정하지 않거나 둘 중 하나를 선택해야 한다.


# 참고

참고로 다음은 현재 팀에서 사용 중인 DBUnit 설정이다. [Database Rider](https://database-rider.github.io/)를 사용 중이다. (기록용)

```kotlin
package thdeng.service.test.support

import com.github.database.rider.core.api.configuration.DBUnit
import com.github.database.rider.core.api.configuration.Orthography
import org.springframework.boot.test.context.SpringBootTest
import thdeng.service.config.CampaignCenterServiceConfig

// Database Rider 에서 사용하는 DbUnit 설정 - 자세한 항목은 Database rider 문서 참조
// https://database-rider.github.io/database-rider/latest/documentation.html?theme=foundation#_dbunit_configuration
@DBUnit(
    columnSensing = true,
    cacheConnection = true,
    cacheTableNames = true,
    leakHunter = true,
    caseInsensitiveStrategy = Orthography.LOWERCASE,
    escapePattern = "`?`",
    batchedStatements = true,
    qualifiedTableNames = false,
    caseSensitiveTableNames = false,
    batchSize = 100,
    fetchSize = 100,
    allowEmptyFields = false,
    dataTypeFactoryClass = CustomMySqlDataTypeFactory::class,
)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    classes = [
        CampaignCenterServiceConfig::class,
        CampaignCenterTestConfig::class,
    ],
    properties = ["spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration"]
)
internal annotation class IntegrationTestSupport
```

- [추가 컬럼 - DBUnit FAQ](https://www.dbunit.org/faq.html#differentcolumnnumber)
- [DbUnit and nullable columns](https://billcomer.blogspot.com/2009/05/dbunit-and-nullable-columns.html)
- [DBUnit configuration - Database Rider docs](https://database-rider.github.io/database-rider/latest/documentation.html?theme=foundation#_dbunit_configuration)
