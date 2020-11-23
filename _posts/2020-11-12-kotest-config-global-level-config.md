---
layout: post
title:  "[Kotest] 프로젝트 전역 설정"
date:   2020-11-12 22:18:00 +0900
published: true
categories: [ kotest ]
tags: [ kotest, kotlin, test, config, global, project ]
---

여러 프로젝트나 모듈 전체에 Kotest 설정을 하고 싶은 경우가 있다. integration test에 있는 모든 테스트가 spring context와 함께 테스트를 해야 한다. 이 때, 모든 integration test 마다 spring context를 띄우고 내리는 설정을 한다면, 누락하기도 쉽고 복붙도 귀찮고..

```kotlin
class MyTest: FunSpec(
    override fun listeners() = listOf(SpringListener) // 요거 빼먹으면 안 된다

    init {
        // 여기 테스트 코드
    }
})

class AnotherMyTest: FunSpec(
    override fun listeners() = listOf(SpringListener) // 요거 빼먹으면 안 된다
)

class DefinatelyAnotherMyTest: FunSpec(
    override fun listeners() = listOf(SpringListener) // 요거 빼먹으면 안 된다
)
```

테스트 마다 일일이 복사해서 넣기 힘드니, `AbstractProjectConfig`를 상속해서 전역 설정을 만들어 두면 모든 테스트에서 실행되게 할 수 있다.

```kotlin
object ProjectConfig : AbstractProjectConfig() {
    // 1 보다 큰 값이면 병렬로 테스트를 실행하고, 이 숫자는 동시에 처리할 spec의 개수
    override val parallelism = 8

    // 테스트가 실행되고 끝날 때 spring test context 를 실행/종료 시켜줌
    override fun listeners(): List<SpringTestListener> = listOf(SpringListener)

    // 이런저런 필요한 override ..
}
```


# 주의

테스트 실행 시에 `AbstractProjectConfig` 클래스를 상속 받은 object 나 class 를 찾아서 모든 config를 합쳐서 사용한다. 여러 config 파일로 분리해서 설정해 두는 것이 가능한데, 이 경우 동일한 설정이 여러 config 파일에 존재하면 임의의 값이 선택된다.

> In the case of clashes, one value will be arbitrarily picked, so it is not recommended adding competing settings to different configs.
>
> from <https://kotest.io/project_config/>

parallel 설정을 unit test에서는 8로 하고, integration test에서 는 1로 하려고 했는데.. 어떻게 해야 깔끔하게 정리가 될까 T_T


# 참고

- [Project Level Config - Kotest Docs](https://kotest.io/project_config/)
- [ProjectConfig sample](https://github.com/bcneng/salary-tracker-api/blob/2f301cff91f0f485b414e6c5f9f60d9dba0c20c2/src/test/kotlin/net/bcneng/salarytrackerbe/ProjectConfig.kt)
