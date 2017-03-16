---
layout: post
title:  "[Spring] injection 받은 list를 원하는 순서로 정렬하기"
date:   2016-03-08 21:00:00 +0900
published: true
categories: [ spring, injection ]
tags: [ spring, java, dependency injection, DI, injection, list, ordering, sort ]
---

간혹 injection 받은 list가 내가 원하는 순서이길 바랄 때가 있다. validation을 할 때 A validation을 하면 그 결과가 확 줄어버리는 경우, 다른 validation 보다 A validation을 먼저 하고 싶을 것이다. 예를 들어, 아래 샘플에서 이름으로 validation을 하는 `NameValidation`이 그런 형태라 가장 먼저 실행하고 싶다면..

```java
@Autowired
private List<Validator> validators;

public void validate(List<Person> people) {
   for(Validator validator : validators) {
       validator.validate(people);
   	}
}


@Component
private class AgeValidator implements Validator<Person> {
   	public void validate(List<Person> people) {
       	// 뽕짝뽕짝
   	}
}
@Component
private class NameValidator implements Validator<Person> {
    // ...
}
@Component
private class PhoneNumberValidator implements Validator<Person> {
    // ...
}
```

각 validator bean을 생성할 때 `@org.springframework.stereotype.Order`를 붙여주면 된다. value로 가장 먼저 실행하고 싶은걸 작은 숫자로 준다.

```java
@Autowired
private List<Validator> validators;

public void validate(List<Person> people) {
   for(Validator validator : validators) {
       validator.validate(people);
   	}
}
@PostConstruct
public void init() {
    // Spring 4.x에는 자동으로 정렬되지만 3.x에서는 수동으로 해줘야 한다.
    Collections.sort(validators, AnnotationAwareOrderComparator.INSTANCE);
}


@Order(2)
@Component
private class AgeValidator implements Validator<Person> {
   	public void validate(List<Person> people) {
       	// 뽕짝뽕짝
   	}
}
@Order(1)
@Component
private class NameValidator implements Validator<Person> {
    // ...
}
@Component
private class PhoneNumberValidator implements Validator<Person> {
    // ...
}
```

이렇게 `@Order`를 붙여주면 validators 리스트에는 `NameValidator`, `AgeValidator`, `PhoneNumberValidator` 순서로 들어간다. `@Order`를 붙여주지 않으면 가장 뒤로 들어간다. 그리고 Spring 4.x부터는 `@Order`만 붙여주면 되지만, 3.x버전에서는 사용하는 부분에서 손으로 정렬해줘야 한다는 문제가 T_T
