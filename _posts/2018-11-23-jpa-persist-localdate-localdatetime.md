---
layout: post
title:  "[JPA] JPA 2.1 이하에서 LocalDate/LocalDateTime 사용하기"
date:   2018-11-23 22:18:00 +0900
published: true
categories: [ jpa ]
tags: [ jpa, time, LocalDate, LocalDateTime, converter, convert, java ]
---

JPA 2.1은 Java 8 이전에 나왔기 때문에, Java 8에서 추가된 `LocalDate`와 `LocalDateTime`은 기본으로 지원하지 않는다. `LocalDateTime`을 `Timestamp` 타입에 넣으려고 하면 에러를 뱉는다. 에러로그를 봐서는 바이너리로 저장을 시도하는듯 싶다. (JPA 2.2부터는 `LocalDate`, `LocalDateTime`, `LocalTime`, `OffsetTime`, `OffsetDateTime` 등을 기본 타입으로 지원한다.)

```bash
2018-11-23 16:48:51.900 ERROR 15371 --- [nio-8081-exec-1] o.h.engine.jdbc.spi.SqlExceptionHelper   : Data truncation: Incorrect date value: '\xAC\xED\x00\x05sr\x00\x0Djava.time.Ser\x95]\x84\xBA\x1B"H\xB2\x0C\x00\x00xpw\x07\x03\x00\x00\x07\xE2\x0B\x17x' for column 'joinDate' at row 1
```

`joinDate` 컬럼은 `date` 타입인데 너 왜 바이너리 넣었어?? 잘못된 데이터야. 라고 한다.

간단히 쓸 수 있는 방법으로 converter를 만들어주면 된다.

```java
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Converter(autoApply = true)
public class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, Timestamp> {

    @Override
    public Timestamp convertToDatabaseColumn(LocalDateTime localDateTime) {
        return Optional.ofNullable(localDateTime)
                .map(Timestamp::valueOf)
                .orElse(null);
    }

    @Override
    public LocalDateTime convertToEntityAttribute(Timestamp timestamp) {
        return Optional.ofNullable(timestamp)
                .map(Timestamp::toLocalDateTime)
                .orElse(null);
    }

}
```

```java
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;

@Converter(autoApply = true)
public class LocalDateConverter implements AttributeConverter<LocalDate, Date> {

    @Override
    public Date convertToDatabaseColumn(LocalDate localDate) {
        return Optional.ofNullable(localDate)
                .map(Date::valueOf)
                .orElse(null);
    }

    @Override
    public LocalDate convertToEntityAttribute(Date date) {
        return Optional.ofNullable(date)
                .map(Date::toLocalDate)
                .orElse(null);
    }

}
```

엔티티 필드에 `@Convert`로 원하는 converter를 지정해 주면 끝!!

```java
@Entity
@Table(name = "user")
public class BuyUser {

    @Id
    private String id;

    @NotNull
    private String name;

    @Convert(converter = LocalDateConverter.class)
    private LocalDate joinDate;

}
```

Hibernate를 쓰는 경우 [hibernate-java8](https://mvnrepository.com/artifact/org.hibernate/hibernate-java8) 등을 쓰면 변환을 지원해 주기도 한다. (현재는 deprecated되었고, [hibernate-core](https://mvnrepository.com/artifact/org.hibernate/hibernate-core)를 사용하라고 쓰여 있다.) [Hibernate 5.3](http://hibernate.org/orm/releases/5.3/) 부터는 JPA 2.2를 지원한다.


# 참고

- [Dealing With Java's LocalDateTime in JPA](https://dzone.com/articles/dealing-with-javas-localdatetime-in-jpa)
- [JPA와 LocalDate, LocalDateTime 사용하기](http://blog.eomdev.com/java/2016/01/04/jpa_with_java8.html)
- [How to persist LocalDate and LocalDateTime with JPA](https://thoughts-on-java.org/persist-localdate-localdatetime-jpa/)
- [How To Map The Date And Time API with JPA 2.2](https://thoughts-on-java.org/map-date-time-api-jpa-2-2/)
- [Hibernate release 5.3](http://hibernate.org/orm/releases/5.3/)
