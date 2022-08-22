---
layout: post
title:  "[Swagger] object 타입으로 테스트하기 - YearMonth 타입 샘플"
date:   2022-08-22 22:18:00 +0900
published: true
categories: [ swagger ]
tags: [ swagger, try, call, api, type, string, object, YearMonth ]
---

Swagger UI에서 `Try it now` 버튼을 통해서 API를 테스트할 수 있다. `DateTime`과 같이 일반적으로 많이 사용하는 클래스는 자동으로 인지하여 `string`과 같은 타입으로 변환해 준다.

{% include image.html file='/assets/img/2022/2022-08-22-swagger-try-api-with-non-primitive-type1.png' %}

하지만, 모든 클래스를 지원하기는 어려워서인지 `YearMonth` 클래스는 `object`로 인식해 버린다. 그리고 `Execute` 버튼을 누르면 잘못된 입력이라고 빨갛게 표시된다.

{% include image.html file='/assets/img/2022/2022-08-22-swagger-try-api-with-non-primitive-type2.png' %}

이럴 때는 아래처럼 `schema`를 사용해서 어떤 타입으로 해석하면 될지 지정해 주면 된다. 물론 어플리케이션 서버에서는 `String` 으로 들어오는 파라미터를 `YearMonth` 클래스로 변환하는 mapper 설정은 별도로 필요하다.

```kotlin
@GetMapping("/{shopNumber}")
@Operation(summary = "조회조회!!")
fun fetchMyStatus(
    @Parameter(description = "가게 번호", example = "138277") @PathVariable("shopNumber") shopNumber: Long,
    @Parameter(description = "조회할 년월", example = "2022-08", schema = Schema(type = "string", format = "YearMonth")) @RequestParam("yearMonth")yearMonth: YearMonth,
): List<MyStatusApiResponse> =
    myService.fetchMyStatus(
        yearMonth = yearMonth,
        shopNumber = shopNumber,
    ).let(MyStatusApiResponse.Companion::from)
```

`schema`가 들어가면서 솔직히 라인은 너무 길어지지만, 포메팅 잘 해두자.

{% include image.html file='/assets/img/2022/2022-08-22-swagger-try-api-with-non-primitive-type3.png' %}


# 참고

- [Representing ISO 8601 year-month dates documentation with Swagger](https://stackoverflow.com/questions/67540201/representing-iso-8601-year-month-dates-documentation-with-swagger)
