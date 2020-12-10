---
layout: post
title:  "[mockk] 호출되지 않음 테스트"
date:   2020-12-09 22:18:00 +0900
published: true
categories: [ kotest ]
tags: [ mockk, kotest, kotlin, test, mock, verify ]
---

MockK([https://mockk.io/](https://mockk.io/))을 이용해서 mocking 된 객체의 메소드가 호출된 것을 확인하기 위해 `verify` 를 사용한다.

```kotlin
// save() 메소드가 1번 호출됐는지 확인
verify(exactly = 1) { myMock.save(any()) }

// save() 메소드가 호출되지 않았는지 확인
verify(exactly = 0) { myMock.save(any()) }
```

위의 예제처럼 특정 메소드를 호출하지 않은 것을 확인하기 위해 `exactly = 0` 로 체크할 수도 있지만, 해당 객체의 어떤 메소드도 호출하지 않은걸 체크하려면 `wasNot Called` 를 사용하면 된다. 모든 메소드 호출을 체크하기 때문에 좋다.

```kotlin
import io.mockk.Called
import io.mockk.verify

internal class UserServiceTest : BehaviorSpec({

  // mocking here

  Given("빈 리스트를 받으면") {
    val users = emptyList<User>()

    When("save를 호출해도") {
      inventoryImportService.updateByIds(users)

      Then("아무 동작도 하지 않는다") {
        verify { displayInventoryAdapter wasNot Called }
        verify { inventoryElasticsearchRepository wasNot Called }
      }
    }
  }
})
```


# 참고
- [https://mockk.io/](https://mockk.io/)
