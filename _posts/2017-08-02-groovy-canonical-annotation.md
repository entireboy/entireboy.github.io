---
layout: post
title:  "[Groovy] @Canonical annotation - @EqualsAndHashCode, @ToString, @TupleConstructor"
date:   2017-08-02 21:18:00 +0900
published: true
categories: [ groovy ]
tags: [ groovy, annotation, canonical, equals, hashcode, tostring, tuple constructor ]
---

[@Canonical](http://docs.groovy-lang.org/latest/html/gapi/groovy/transform/Canonical.html) annotation은 하나로 간단하게 3개 annotation을 모두 넣은 것과 같다.

- [@EqualsAndHashCode](http://docs.groovy-lang.org/latest/html/gapi/groovy/transform/EqualsAndHashCode.html): Object 클래스의 equals()와 hashcode()를 자동으로 만들어주는 annotation

- [@ToString](http://docs.groovy-lang.org/latest/html/gapi/groovy/transform/ToString.html): 클래스 필드 내용을 출력해 주는 toString()을 구현해 주는 annotation

- [@TupleConstructor](http://docs.groovy-lang.org/latest/html/gapi/groovy/transform/TupleConstructor.html): 객체를 생성할 때 모든 필드값을 받는 생성자를 만들어 주는 annotation

간단하게 각 annotation을 붙인 샘플 코드로 확인..

```groovy
class Person {
	String lastName
	String firstName
}

@groovy.transform.EqualsAndHashCode
class EqualsAndHashCodePerson {
	String lastName
	String firstName
}

@groovy.transform.ToString
class ToStringPerson {
	String lastName
	String firstName
}

@groovy.transform.TupleConstructor
class TupleConstructorPerson {
	String lastName
	String firstName
}

@groovy.transform.Canonical
class CanonicalPerson {
	String lastName
	String firstName
}

Person p1 = new Person(lastName: "last", firstName: "first")
Person p2 = new Person(lastName: "last", firstName: "first")
assert p1 != p2

EqualsAndHashCodePerson ep1 = new EqualsAndHashCodePerson(lastName: "last", firstName: "first")
EqualsAndHashCodePerson ep2 = new EqualsAndHashCodePerson(lastName: "last", firstName: "first")
assert ep1 == ep2 // equals

ToStringPerson tp1 = new ToStringPerson(lastName: "last", firstName: "first")
assert tp1.toString() == "ToStringPerson(last, first)" // toString

TupleConstructorPerson tup = new TupleConstructorPerson("last", "first") // tuple constructor
assert tup.lastName == "last"
assert tup.firstName == "first"

CanonicalPerson cp1 = new CanonicalPerson("last", "first") // tuple constructor
CanonicalPerson cp2 = new CanonicalPerson("last", "first")
assert cp1 == cp2 // equals
assert cp1.toString() == "CanonicalPerson(last, first)" // toString
```


# 참고

- [Groovy Goodness: Canonical Annotation to Create Mutable Class](http://mrhaki.blogspot.kr/2011/05/groovy-goodness-canonical-annotation-to.html))
- [@Canonical - groovy API](http://docs.groovy-lang.org/latest/html/gapi/groovy/transform/Canonical.html)
- [@EqualsAndHashCode - groovy API](http://docs.groovy-lang.org/latest/html/gapi/groovy/transform/EqualsAndHashCode.html)
- [@ToString - groovy API](http://docs.groovy-lang.org/latest/html/gapi/groovy/transform/ToString.html)
- [@TupleConstructor - groovy API](http://docs.groovy-lang.org/latest/html/gapi/groovy/transform/TupleConstructor.html)
