---
layout: post
title:  "[Kotest] 프로젝트 전역 설정"
date:   2020-11-25 22:18:00 +0900
published: true
categories: [ kotest ]
tags: [ kotest, kotlin, test, fix, time, current, instant ]
---

시간 관련된 코드를 작성하면 테스트를 위해 테스트를 실행하는 시각을 고정시켜야 할 때가 있다.

예를 들어, 심야 시간에 푸시를 보내지 않도록 하기 위해 현재 시각을 체크해서 오후 11시 부터 아침 8시 까지는 `true`를 반환하는 `doNotDisturb` 메소드를 만들었다고 하자. 이 메소드를 테스트하기 위해 밤낮으로 테스트를 돌릴 수 없으니 현재 시각을 고정해서 테스트를 진행하게 된다.

# Kotest

`withConstantNow` 를 사용하면 현재 시각을 쉽게 고정할 수 있다.

```kotlin
class DisturbTimeTest : FunSpec({
    context("doNotDisturb") {
        test("오후 23시 부터 오전 8시 까지는 true 를 반환한다.") {
            forAll(
                row("2028-01-01T00:00:00", true),
                row("2028-01-01T01:23:45", true),
                row("2020-12-25T07:02:24", true),
                row("2020-12-31T07:59:59", true),
                row("2028-01-01T08:00:00", false),
                row("2022-04-01T12:00:00", false),
                row("2028-01-24T23:45:12", false),
                row("2026-01-01T23:00:01", true),
            ) { givenTime, expected ->
                val now = LocalDateTime.parse(givenTime)

                // 현재 시각 고정
                withConstantNow(now) {
                    doNotDisturb() shouldBe expected
                }
            }
        }
    }
}) {
    companion object {
        // 샘플을 위해 임의의 위치에 만든 테스트 대상
        fun doNotDisturb(): Boolean {
            // 현재 시각을 구해봅세!!
            val now = LocalDateTime.now()
            return now.toLocalTime().isBefore(LocalTime.of(8, 0, 0)) ||
                now.toLocalTime().isAfter(LocalTime.of(23, 0, 0))
        }
    }
}
```

`doNotDisturb` 메소드에서 시스템의 현재시각을 구해온다. 이 때 `withConstantNow` 를 사용하면 해당 블럭 안에서는 해당 시각으로 고정된다.

혹은 아래와 같은 방법으로 전역으로 테스트 시각을 고정시킬 수도 있다.

```kotlin
class Test : FunSpec() {
    override fun listeners() = listOf(
        ConstantNowTestListener(LocalDateTime.of(2020, 12, 25, 12, 34, 56))
    )

    init {
        // .. 테스트 코드 ..
    }
}
```

# JUnit

JUnit 같은 경우, 시각을 고정하고 extension 등을 통해서 원래 시각으로 돌리게 된다.

```kotlin
@ExtendWith(value = [LocalDateTimeExtension::class])
class MyLocalDateTimeTest {
    fun `fixCurrentDateTime(LocalDateTime)`() {
        // given
        val now = LocalDateTime.of(2018, 12, 1, 12, 25, 38)

        // when
        LocalDateTimeTestHelper.fixCurrentDateTime(now)

        // then
        MyLocalDateTime.now() `should be equal to` now
    }
}

class LocalDateTimeExtension : AfterTestExecutionCallback {
    override fun afterTestExecution(context: ExtensionContext) {
        // 테스트가 끝날 때 마다 현재시각으로 돌린다.
        LocalDateTimeTestHelper.unfixCurrentDateTime()
    }
}
```

위 샘플에서 `LocalDateTimeTestHelper#fixCurrentDateTime` 을 통해서 현재 시각을 고정시키고, `MyLocalDateTime` 에서 고정된 instant를 가져오는 형태로 사용했다. 팀에서 JUnit을 사용할 때는 이 방법을 사용했었지만, Kotest 를 사용하니 `MyLocalDateTime` 클래스를 만드는 귀찮음도 없고 잘못해서 `LocalDateTime` 을 사용하면 테스트도 깨지고 번거로웠는데.. 너무 좋다 +_+ 이렇게 세상 편할수가!!


# 참고

- [Current instant listeners - Kotest Extensions](https://github.com/kotest/kotest/blob/master/doc/extensions.md#current-instant-listeners)
