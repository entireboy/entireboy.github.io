---
layout: post
title:  "[Spock] 특정 조건에만 실행할 테스트(@Ignore/@IgnoreIf/@Requires)"
date:   2019-12-12 22:18:00 +0900
published: true
categories: [ java ]
tags: [ spock, test, ignore, condition, run ]
---

# @Ignore

spock을 쓰면서 특정 테스트를 실행하고 싶지 않을 때는 테스트 클래스나 메소드에 `@Ignore`를 달아주면 된다. 로컬에서 연동 테스트를 위해 코드만 남겨두고 평소에는 호출하지 않고 싶을 때 많이 썼다.

```groovy
@Ignore("실제 API가 호출돼서 무시")
def "Create API 호출"() { ... }
```


# @IgnoreRest

`@Ignore`와는 반대로 이 테스트 이외의 다른 테스트들을 실행하지 않을 때 쓴다.

```groovy
def "실행 안 되는 테스트"() { ... }

@IgnoreRest
def "실행 되는 테스트"() { ... }

def "역시 실행 안 되는 테스트"() { ... }
```


# @IgnoreIf

`@Ignore`와 유사한데, 무조건 테스트가 실행되지 않는 `@Ignore`와는 달리 특정 조건이 만족하는 경우만 실행하고 싶지 않은 경우에 쓴다.

```groovy
@IgnoreIf({ Boolean.valueOf(env['IGNORE_TEST'])})
def "때때로 무시하고 싶은 테스트"() { ... }
```

테스트를 실행할 때 환경변수로 `IGNORE_TEST` 값이 `true`라면 이 테스트는 무시된다. 코드는 그대로 두고 환경변수만으로 테스트 여부를 결정할 때 좋다.


# @Requires

`@IgnoreIf`와는 반대로 조건에 해당하는 경우만 실행하고 싶을 때는 `@Requires`를 쓰면 된다.

```groovy
@Requires({ Boolean.valueOf(env['WANT_TO_RUN_TEST'])})
def "때때로 실행하고 싶은 테스트"() { ... }
```


# 참고

- [Spock Extensions](http://spockframework.org/spock/docs/1.0/extensions.html)
