---
layout: post
title:  "[Kotlin] Bean Validation 안 되는 문제"
date:   2020-12-10 22:18:00 +0900
published: true
categories: [ kotlin ]
tags: [ kotlin, validation, bean, bean validation, spring, annotation ]
---

# 문제

Bean Validation을 위해 아래와 같은 annotation들을 추가했는데, 이상하게 Kotlin 코드에서는 동작하지 않는다. Java 코드에서 쓰던 것 그대로 가져온 것이고 spring 환경도 크게 다르지 않고 validation을 위한 dependency도 모두 잘 설정되어 있는데, Java 환경에서는 잘 동작하는 코드가 Kotlin 환경에서는 원하는 대로 동작하지 않는다.

```kotlin
data class UserDto(
    @NotBlank(message = "이름은 필수입니다.")
    @Min(value = 2, message = "이름은 2자 이상입니다.")
    val name: String?,

    @NotNull
    @Min(value = 15, message = "15세 미만은 사용할 수 없습니다.")
    val age: Int?,
)
```

곰곰히 생각하다가 IntelliJ 에는 `Kotlin Bytecode` 의 힘을 빌려 보기로 했다.

{% include image.html file='/assets/img/2020-12-10-kotest-bean-validation-in-kotlin1.png' alt='Kotlin Bytecode in IntelliJ' %}

IntelliJ 에서 Shift를 2번 누르고 `Kotlin Bytecode` 를 검색하면 위와 같은 창이 뜬다. 현재 포커싱된 파일을 바이트코드로 변환을 해주는데, 바이트코드는 알아보기 힘드니 위에 있는 `Decompile` 을 클릭해서 다시 자바 파일로 바꿔 보면 아래와 같이 변환된 것을 볼 수 있다.

{% include image.html file='/assets/img/2020-12-10-kotest-bean-validation-in-kotlin2.png' alt='Decompile the bytecode' %}

잘 살펴보면 내가 달아둔 `@Blank` 나 `@Min` 과 같은 annotation들이 생성자에 붙어 있는 것을 볼 수 있다. 아!! data class에 달아뒀으니 생성자에 붙는거구나!! 하지만 내가 validation을 해야 하는 대상은 생성자가 아니라 각 필드의 값이기 때문에 `@field:xxx` 로 annotation을 필드에 붙여 줘야 한다. ([Annotation Use-site Targets](https://kotlinlang.org/docs/reference/annotations.html#annotation-use-site-targets) - Kotlin doc) data class가 아니더라도 생성자의 필드로 선언을 했다면 같은 문제가 발생한다.


# 해결방법

간단하게 코드는 이렇게 `@field:xxx` 를 붙이면 된다.

```kotlin
data class UserDto(
    @field:NotBlank(message = "이름은 필수입니다.")
    @field:Min(value = 2, message = "이름은 2자 이상입니다.")
    val name: String?,

    @field:NotNull
    @field:Min(value = 15, message = "15세 미만은 사용할 수 없습니다.")
    val age: Int?,
) {
    fun toUser() = User(name = name!!, age = age!!)
}
```

바이트코드를 확인해 보면 아래처럼 원하는 필드에 annotation 이 붙은 것을 확인할 수 있다.

{% include image.html file='/assets/img/2020-12-10-kotest-bean-validation-in-kotlin3.png' alt='Correct annotations' %}

이 내용은 [https://beanvalidation.org/2.0-jsr380/](JSR 380), [https://beanvalidation.org/1.1/](JSR 349), [https://beanvalidation.org/1.0/](JSR 303) 스펙이 따른 bean validation 이고, `Hibernate Validator`와 같은 구현체가 있다. 그리고, 필수 체크가 아니기 때문에 설정이 잘 되어 있는지 integration test 등을 통한 확인이 꼭 필요하다. Dependency 버전업을 했는데, 적용이 안 될 수도 있기 때문에..


# 참고

- [https://kotlinlang.org/docs/reference/annotations.html#annotation-use-site-targets](Annotation Use-site Targets - Kotlin docs)
- [https://velog.io/@lsb156/SpringBoot-Kotlin에서-Valid가-동작하지-않는-원인JSR-303-JSR-380](SpringBoot - Kotlin에서 @Valid가 동작하지 않는 원인 JSR-303, JSR-380)
