---
layout: post
title:  "[Database Rider] JSON 타입 사용하기 (DbUnit)"
date:   2021-03-09 22:18:00 +0900
published: true
categories: [ dbunit ]
tags: [ database rider, dbunit, dataset, data, type, factory, property, config ]
---

# 문제

MySQL에서 `JSON` 타입이나 `GEOMETRY` 타입으로 되어 있는 column을 Database Rider(또는 DbUnit)로 사용하려면 다음과 같은 오류가 발생한다.

```
2021-03-09 18:40:51.980  WARN 9317 --- [   SpecRunner-1] o.dbunit.dataset.AbstractTableMetaData   : Potential problem found: The configured data type factory 'class org.dbunit.dataset.datatype.DefaultDataTypeFactory' might cause problems with the current database 'MySQL' (e.g. some datatypes may not be supported properly). In rare cases you might see this message because the list of supported database products is incomplete (list=[derby]). If so please request a java-class update via the forums.If you are using your own IDataTypeFactory extending DefaultDataTypeFactory, ensure that you override getValidDbProducts() to specify the supported database products.
2021-03-09 18:40:51.980  WARN 9317 --- [   SpecRunner-1] org.dbunit.util.SQLHelper                : ad_targeting.conditions data type (1111, 'JSON') not recognized and will be ignored. See FAQ for more information.
2021-03-09 18:40:51.985  WARN 9317 --- [   SpecRunner-1] o.s.test.context.TestContextManager      : Caught exception while invoking 'beforeTestMethod' callback on TestExecutionListener [com.github.database.rider.spring.DBRiderTestExecutionListener@107fbca8] for test method [public java.lang.String kr.leocat.test.service.domain.adtargeting.repository.OrderRepositoryIntegrationTest$ByteBuddy$rkxW0aqO.save()] and test instance [kr.leocat.test.service.domain.adtargeting.repository.OrderRepositoryIntegrationTest@3886bfec]

java.lang.RuntimeException: Could not create dataset for test 'save'.

  ... (생략) ...

Caused by: org.dbunit.dataset.NoSuchColumnException: ad_targeting.CONDITIONS -  (Non-uppercase input column: conditions) in ColumnNameToIndexes cache map. Note that the map's column names are NOT case sensitive.
	at org.dbunit.dataset.AbstractTableMetaData.getColumnIndex(AbstractTableMetaData.java:117) ~[dbunit-2.7.0.jar:na]
	at org.dbunit.operation.AbstractOperation.getOperationMetaData(AbstractOperation.java:89) ~[dbunit-2.7.0.jar:na]
	at org.dbunit.operation.AbstractBatchOperation.execute(AbstractBatchOperation.java:151) ~[dbunit-2.7.0.jar:na]
	at org.dbunit.operation.CompositeOperation.execute(CompositeOperation.java:79) ~[dbunit-2.7.0.jar:na]
	at com.github.database.rider.core.dataset.DataSetExecutorImpl.createDataSet(DataSetExecutorImpl.java:163) ~[rider-core-1.23.0.jar:na]
	... 31 common frames omitted
```


# 원인 및 해결방법

DbUnit은 `JSON`이라는 타입을 몰라서 발생하는 문제이고, DataTypeFactory 설정을 해주면 된다. `GEOMETRY` 타입도 마찬가지이다.

(spock 에서 사용했던 groovy 코드)

```groovy
package kr.leocat.test.dbunit

import org.dbunit.dataset.datatype.DataType
import org.dbunit.dataset.datatype.DataTypeException
import org.dbunit.ext.mysql.MySqlDataTypeFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class CustomMySqlDataTypeFactory extends MySqlDataTypeFactory {

    Logger logger = LoggerFactory.getLogger(CustomMySqlDataTypeFactory.class)

    @Override
    DataType createDataType(int sqlType, String sqlTypeName) throws DataTypeException {

        logger.debug("createDataType(sqlType={}, sqlTypeName={}) - start", String.valueOf(sqlType), sqlTypeName)

        if ("JSON".equalsIgnoreCase(sqlTypeName)) {
            return DataType.CLOB
        }

        if ("GEOMETRY".equalsIgnoreCase(sqlTypeName)) {
            return DataType.BINARY
        }

        return super.createDataType(sqlType, sqlTypeName)
    }
}
```

아래는 동일한 내용의 Kotest 용 Kotlin 코드이다.

```kotlin
package kr.leocat.test.dbunit

import org.dbunit.dataset.datatype.DataType
import org.dbunit.ext.mysql.MySqlDataTypeFactory
import org.slf4j.LoggerFactory

/**
 * DB Unit의 MySqlDataTypeFactory에는 JSON 타입이 존재하지 않아서 JSON데이터를 입력할때 에러가 발생한다.
 * 따라서, JSON 타입의 경우에는 CLOB으로 매핑하여 에러 해결.
 *
 * **NOTE:** 지우지 말 것!! `dbunit.yml` 파일에서 사용 중!!
 */
class CustomMySqlDataTypeFactory : MySqlDataTypeFactory() {

    override fun createDataType(sqlType: Int, sqlTypeName: String?): DataType {
        log.debug("createDataType(sqlType=$sqlType, sqlTypeName=$sqlTypeName) - start")

        return when {
            sqlTypeName.equals("JSON", ignoreCase = true) -> DataType.CLOB
            sqlTypeName.equals("GEOMETRY", ignoreCase = true) -> DataType.BINARY
            else -> super.createDataType(sqlType, sqlTypeName)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(CustomMySqlDataTypeFactory::class.java)
    }
}
```

이 DataTypeFactory 를 DbUnit 설정에 추가해 주면 끗.

```yaml
cacheConnection: true
cacheTableNames: true
leakHunter: false
caseInsensitiveStrategy: !!com.github.database.rider.core.api.configuration.Orthography 'LOWERCASE'
properties:
  datatypeFactory: !!kr.leocat.test.dbunit.CustomMySqlDataTypeFactory { }
	...
```


# 참고

- [3.3.2. DBUnit Configuration - Database Rider Documentation](https://github.com/database-rider/database-rider#332-dbunit-configuration)
