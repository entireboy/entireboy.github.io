---
layout: post
title:  "[Spock] @Unroll - where 절에 있는 테스트 데이터 풀어서 보여주기"
date:   2019-05-01 22:18:00 +0900
published: true
categories: [ java ]
tags: [ spock, test, unroll, where, parameterized test, data driven test, groovy, java ]
---

Spock으로 테스트할 때 `where`절에 테스트 데이터가 많은 경우, 실패하는 데이터를 찾기 어려운 경우가 있다. 데이터가 한 15개쯤 있는데 중간에 하나가 깨지면.. 어떤게 문제인지 찾느라 눈 빠진다. 흐앜 @_@

```groovy
package kr.leocat.test

import spock.lang.Specification
import spock.lang.Unroll

class UnrollTest extends Specification {

    @Unroll("#name의 나이는 #age살이어야 한다")
    def "나이 테스트"() {
        given:
        def person = new Person(name: name)

        expect:
        person.age == age

        where:
        name     | age
        "thDeng" | 7
        "gamza"  | 17
        "me"     | 20
    }

}

// exception 만들어 내기 위한 억지 샘플 클래스
class Person {
    def name

    def getAge() {
        if (name == "thDeng") {
            return 7
        } else if (name == "gamza") {
            return 17
        } else {
            throw new RuntimeException("모르는 사람: $name")
        }
    }
}
```

{% include image.html file='/assets/img/2019-05-01-spock-unroll-the-parameterized-tests1.png' alt='spock test result' %}

이럴 때 `@Unroll`을 달아주면 어떤 데이터가 실패하는지 확인하기 좋게 아래처럼 바뀐다.

{% include image.html file='/assets/img/2019-05-01-spock-unroll-the-parameterized-tests2.png' alt='spock test result' %}

`@Unroll("#name의 나이는 #age살이어야 한다")`처럼 각 테스트 데이터를 읽기 좋게 메시지를 만들 수도 있다. `#name`이나 `#name.size()`처럼 `#코드`를 사용해서 원하는 메시지로 바꿀 수도 있다.

이런 메시지를 항상 만들 수 있는 것이 아니기 때문에 `@Unroll`만 써도 된다. 이럴 때는 실패한 데이터의 배열 번호로 확인할 수 있다.

{% include image.html file='/assets/img/2019-05-01-spock-unroll-the-parameterized-tests3.png' alt='spock test result' %}


# 참고
- [Data Driven Testing#Method Unrolling - Spock doc](http://spockframework.org/spock/docs/1.3/data_driven_testing.html#_method_unrolling)
