---
layout: post
title:  "[Grails] Mocking persistence method in GORM unit test"
date:   2017-07-13 21:18:00 +0900
published: true
categories: [ grails ]
tags: [ grails, groovy, unit test, mock, gorm ]
---

GORM의 도메인 객체를 꺼내오는 테스트가 필요할 때 `grails.test.MockUtils.mockDomain()`을 사용하면 된다. 실제로 DB에 넣고 가져오는 것처럼 동작한다.

```groovy
class FoodService {
  def foo(String name) {
    Food.findByName(name)
  }
}
```

간단히 이름으로 찾아와서 가공 후 반환하는 메소드를 테스트할 때, `mockDomain()`을 이용해서 테스트할 도메인 클래스와 객체들을 넣어주면 된다. `setup`이나 `given` 어디든 상관은 없다.

```groovy
def "is my food risky"() {
  setup:
  def f1 = new Food(name: "Coffee", isRisky: true)
  def f2 = new Food(name: "Bread", isRisky: false)
  MockUtils.mockDomain(Food, [f1, f2])

  when:
  def coffee = service.foo("Coffee")

  then:
  coffee.isRisky == true

  when:
  def bread = service.foo("Bread")

  then:
  bread.isRisky == false
}
```

{% include google-ad-content %}


# 참고

- [Unit Testing Domains - Grails doc](https://docs.grails.org/latest/guide/testing.html#unitTestingDomains)
- [Mastering Grails, Mock testing with Grails](https://www.ibm.com/developerworks/library/j-grails10209/index.html)
- [[Grails] Unit test on Grails]({{ site.baseurl }}{% post_url 2017-07-12-grails-unit-test-on-grails %})
