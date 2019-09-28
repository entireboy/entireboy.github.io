---
layout: post
title:  "[slf4j] lombok @Slf4j로 추가된 logger가 호출되는지 테스트"
date:   2019-10-01 22:18:00 +0900
published: false
categories: [ java ]
tags: [ java, test, logger, logging, lombok, slf4j, spock, groovy, mock ]
---

Lombok의 `@Slf4j`를 달아주면 `log` 필드가 생기고, 편하게 logger를 불러서 로깅할 수 있다. 문제는 실행하기 전에는 존재하지 않기 때문에 테스트를 할 때 logger가 호출됐는지 테스트하기가 어렵다는 것이다. 그 logger를 테스트하려면 요렇게..

내 머리는 못 믿으니 기록용으로.. (샘플은 spock이기 때문에 groovy 코드)


# 테스트용 logger 만들기

소스코드(`/src/main/{LANG}`)가 아닌 테스트코드(`/src/test/{LANG}`)에 logger를 만든다.

```groovy
package kr.leocat.test.support

import org.junit.rules.ExternalResource
import org.slf4j.Logger

import java.lang.reflect.Field
import java.lang.reflect.Modifier

class Slf4jLogger extends ExternalResource {
    Field logField
    Logger logger
    Logger originalLogger

    Slf4jLogger(Class logClass, Logger logger) {
        logField = logClass.getDeclaredField("log")
        this.logger = logger
    }

    @Override
    protected void before() throws Throwable {
        logField.accessible = true

        Field modifiersField = Field.getDeclaredField("modifiers")
        modifiersField.accessible = true
        modifiersField.setInt(logField, logField.getModifiers() & ~Modifier.FINAL)

        originalLogger = (Logger) logField.get(null)
        logField.set(null, logger)
    }

    @Override
    protected void after() {
        logField.set(null, originalLogger)
    }
}
```

객체를 열어(?)서 `private`으로 되어 있는 기존의 `log` 필드의 logger 객체를 별도로 저장해 두고, 새 logger를 꽂아 준다. 새 logger는 mocking된 객체를 넣어주고 호출횟수(cardinality)를 체크한다. 테스트가 끝나면 다시 기존 logger로 바꿔 준다.


# Logger 바꿔 꽂기

테스트코드에서 cardinality를 체크할 mocking된 logger를 만들고 바꿔 꽂는다. 그리고 `then`절에서 logger가 한 번 호출됐는지 체크한다.

```groovy
package kr.leocat.test.logger

import kr.leocat.test.support.Slf4jLogger
import org.junit.Rule
import org.slf4j.Logger
import spock.lang.Specification

class PersonTest extends Specification {

    @Rule
    Slf4jLogger slf4jLogger = new Slf4jLogger(Person.class, Mock(Logger))

    def "call my name"() {
        given:
        Person person = new Person(name: "이씨")

        when:
        person.callMyName()

        then:
        1 * slf4jLogger.logger.info("My name is {}", "이씨")
    }

}
```

테스트할 클래스는 요기 아래..

```java
package kr.leocat.test.logger;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class Person {

    private String name;

    public void callMyName() {
        log.info("My name is {}", name);
    }

}
```
