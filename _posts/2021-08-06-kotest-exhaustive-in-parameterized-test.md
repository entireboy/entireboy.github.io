---
layout: post
title:  "[Kotest] Parameterized test에서 모든 enum을 돌면서 테스트하기 (exhaustive)"
date:   2021-08-06 22:18:00 +0900
published: true
categories: [ kotest ]
tags: [ kotest, kotlin, test, parameterized test, property test, enum, exhaustive ]
---

Kotest의 Parameterized test([Property test](https://kotest.io/docs/proptest/property-test-functions.html))의 [Generator](https://kotest.io/docs/proptest/property-test-generators.html)에는 모든 enum과 같이 합타입(
[enum/sealed class 그리고 대수적 타입(algebraic data type)]({{ site.baseurl }}{% post_url 2020-02-08-kotlin-enum-and-algebraic-data-type %}) 참고)의 모든 값을 테스트할 수 있는 [Exhaustive](https://kotest.io/docs/proptest/property-test-generators.html#exhaustive)가 있다.

아래와 같이 `checkAll`과 함께 사용하면, enum의 모든 값을 테스트한다. (`MyStatus`는 enum이다.)

```
context("accept") {
    checkAll(
        Exhaustive.enum<MyStatus>()
    ) { myStatus ->
        test("모든 status에 대해 유효성 검사를 한다. - $myStatus") {
            // given
            val machine = MachineFixture.aMachine(status = myStatus)

            // expect
            validtionRule.accept(machine) shouldBe true
        }
    }
}
```

이 enum 중 일부만 테스트를 하고 싶은 경우 `filter`, `filterNot`을 사용하면 된다.

```
context("accept") {
    checkAll(
        Exhaustive.enum<MyStatus>().filter { it == ONGOING }
    ) { adGroupStatus ->
        test("status 가 ONGOING 일 때만 유효성 검사를 한다. - $myStatus") {
            // given
            val machine = MachineFixture.aMachine(status = myStatus)

            // expect
            validtionRule.accept(machine) shouldBe true
        }
    }
    checkAll(
        Exhaustive.enum<MyStatus>().filterNot { it == ONGOING }
    ) { adGroupStatus ->
        test("adGroup status 가 ONGOING 이 아닐 때는 유효성 검사를 하지 않는다. - $adGroupStatus") {
            // given
            val machine = MachineFixture.aMachine(status = myStatus)

            // expect
            validtionRule.accept(machine) shouldBe false
        }
    }
}
```

[Generator](https://kotest.io/docs/proptest/property-test-generators.html)
[Property test](https://kotest.io/docs/proptest/property-test-functions.html)
