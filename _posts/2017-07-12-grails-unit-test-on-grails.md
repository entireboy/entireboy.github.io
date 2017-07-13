---
layout: post
title:  "[Grails] Unit test on Grails"
date:   2017-07-12 23:38:00 +0900
published: true
categories: [ grails ]
tags: [ grails, groovy, unit test, mock ]
---

Grails에서 unit test를 하기 위해서는 `@TestFor`를 사용해서 테스트할 클래스를 지정하면 된다. 나머지는 spock test와 형식은 같다.

Grails에서 unit test의 테스트 파일 경로는 `test/unit`아래이다.

```groovy
import grails.test.mixin.Mock
import grails.test.mixin.TestFor

@Mock([Food])
@TestFor(FoodService)
class FoodServiceSpec extends Specification {
  def "this is my test"() {
    when:
    def actual = service.foo()

    then:
    actual == bar
  }
}
```

`@TestFor(FoodService)` 어노테이션은 `FoodService`를 테스트하는 `FoodServiceSpec`테스트라는 의미이다. `@TestFor`로 서비스를 지정하면 자동으로 `service`라는 멤버로 해당 클래스를 테스트할 수 있고, 컨트롤러를 지정하면 `controller`라는 멤버를 사용할 수 있다. (이런 자동으로 생성되는 것들이 grails의 장정인 것 같은데, 모르면 못 쓰니 단점이 되기도 한다.)

`@Mock`은 `FoodService` 안에서 GORM 등으로 해당 도메인 객체를 컨트롤하는 로직이 있는 경우 사용한다. 예를 들어, `FoodService#foo()` 메소드 안에 아래와 같이 `.findByxx()` 같은 로직이 있는 경우 사용하면 된다. `@Mock`을 사용하면 이런 메소드들을 자동으로 mocking해준다. 도메인에 메소드를 만들어주는 수준이고, 값을 리턴하는 형태까지 만들어주지는 않는다. (도메인 객체까지 mocking이 필요한 경우는 [여기]({{ site.baseurl }}{% post_url 2017-07-13-grails-mocking-persistence-method-in-gorm-unit-test %}) 참조)


# 참고

- [Unit Testing - Grails doc](https://docs.grails.org/latest/guide/testing.html#unitTesting)
- [[Grails] Mocking persistence method in GORM unit test]({{ site.baseurl }}{% post_url 2017-07-13-grails-mocking-persistence-method-in-gorm-unit-test %})
