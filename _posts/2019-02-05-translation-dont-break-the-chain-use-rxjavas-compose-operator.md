---
layout: post
title:  "[발번역] 체인을 끊지 말고 RxJava의 compose() 연산자를 사용하라"
date:   2019-02-05 22:18:00 +0900
published: true
categories: [ translation ]
tags: [ clumsylation, translation, translate, break, chain, rxjava, compose, Transformer, flatmap, alternative, operator ]
---

> 본 글은 아래 글을 **[발번역]({{ site.baseurl }}/tags/clumsylation)** 한 내용입니다. 누락되거나 잘못 옮겨진 내용이 있을(~~아니 많을;;~~) 수 있습니다.
>
> 원글: Don't break the chain: use RxJava's compose() operator by Dan Lew
> <https://blog.danlew.net/2015/03/02/dont-break-the-chain/>

> **옮긴이의 TL;DR** 체인 형태의 코드는 체인을 끊지 말고 일관된 (체인) 형태로 읽을 수 있어야 한다.
>
> **옮긴이.** 샘플은 RxJava이지만, Java의 stream/Optional이나 Reactor 같이 체인 형태의 다양한 코드에서도 적용되는 내용이다. 샘플의 RxJava compose()는 Java stream의 map()이나 Reactor Mono/Flux의 map() 등으로 대체할 수 있다.


RxJava의 장점 중 하나는 연속된(series) operator[^operator]들(체인)을 통해 데이터가 어떻게 바뀌는지를 알 수 있다는 것이다.:

```java
Observable.from(someSource)
    .map(data -> manipulate(data))
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(data -> doSomething(data));
```

만약 여러 스트림에서 재사용하길 원하는 operator 조합이 있다면 어떨까?? 예를 들어, 나는 워커 스레드(worker thread)에서 데이터를 처리하고 난 다음 메인 스레드(main thread)에서 그 데이터를 구독(subscribe)하고 싶어서 `subscribeOn()`과 `observeOn()`을 자주 사용한다. 이 로직을 일관되고 재사용가능한 방식으로 모든 스트림에 적용할 수 있다면 정말 좋을 것이다.


# 나쁜 방식

다음은 내가 지난 여러 달 동안 사용해 왔던 나쁜 안티패턴이다([잘못했어요 T_T](https://www.youtube.com/watch?v=jG2KMkQLZmI)).

먼저, scheduler[^scheduler]를 적용(apply)하는 메소드를 만든다:

```java
<T> Observable<T> applySchedulers(Observable<T> observable) {
    return observable.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
}
```

그리고, 메소드로 `Observable`[^observable] 체인을 감싼다:

```java
applySchedulers(
    Observable.from(someSource)
        .map(data -> manipulate(data))
    )
    .subscribe(data -> doSomething(data));
```

이 코드는 잘 동작하긴 하지만, 이쁘지도 않고 혼란스럽다. `applySchedulers()`에 실제로 무엇에 적용되는 것일까?? 이제 더 이상 연속된 operator 체인이 아니라서 따라 읽기 어렵다. 이 코드는 포메팅 할 방법자체가 없기 때문에 내 솜씨가 서투른게 아니다.

자, 그렇다면 이제 스트림 하나에 이 안티패턴이 여러번 적용된다면 얼마나 나쁠지 한번 생각해 보자. ㄷㄷㄷ[^shudder]


# Transformer[^transformer] 소개

RxJava를 개발한 현명한 사람들은 이것이 문제가 될 수 있다 인식했고, [Observable.compose()](http://reactivex.io/RxJava/javadoc/rx/Observable.html#compose-rx.Observable.Transformer-)를 사용한 [Transformer](http://reactivex.io/RxJava/javadoc/rx/Observable.Transformer.html)라는 한 가지 방법을 제시했다.

사실 `Transformer`는 아주 단순한 `Func1<Observable<T>, Observable<R>>`일 뿐이다. 다르게 표현하면, `Transformer`는 한 종류의 `Observable`을 넣으면 또 다른 `Observable`을 반환하는 것이다. 이것은 연속된 operator들을 인라인(inline)으로 호출하는 것과 완전히 동일하다.

scheduler를 변경하는 `Transformer`를 생성하는 메소드를 만들자:

```java
<T> Transformer<T, T> applySchedulers() {
    return new Transformer<T, T>() {
        @Override
        public Observable<T> call(Observable<T> observable) {
            return observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        }
    };
}
```

람다를 사용하면 훨씬 이쁘게 표현할 수 있다:

```java
<T> Transformer<T, T> applySchedulers() {
    return observable -> observable.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
}
```

여하튼, 원래 코드가 어떻게 바뀌었는지 보자:

```java
Observable.from(someSource)
    .map(data -> manipulate(data))
    .compose(applySchedulers())
    .subscribe(data -> doSomething(data));
```

훨씬 낫다!! 재사용 가능한 코드를 얻었고, 체인도 유지된다.

(2015. 11. 03.에 추가) JDK 7 이하 버전을 사용한다면, `compose()`가 제네릭과 함께 동작할 수 있도록 추가 작업을 해줘야 한다. 특히, 이렇게 리턴 타입을 컴파일러에게 알려줘야 한다:

```java
Observable.from(someSource)
    .map(data -> manipulate(data))
    .compose(this.<YourType>applySchedulers())
    .subscribe(data -> doSomething(data));
```


# Transformer 재사용하기

이전 샘플에서는 매 호출 마다 새로운 `Transformer` 객체를 생성하는 메소드를 사용했다. 미리 객체를 생성해 두고 불필요한 객체 생성을 대신할 수도 있다. `Transformer`의 핵심은 결국엔 코드 재사용이다.

구체 타입(concrete type)에서 다른 타입으로 변환한다면 Transformer 객체를 생성하는 것은 상당히 간단하다.

```java
Transformer<String, String> myTransformer = new Transformer<String, String>() {
    // ... 필요한 작업 ...
};
```

이 scheduler `Transformer`는 어떨까?? 이 Transformer는 타입을 전혀 고려하지 않아서 제네릭 객체를 정의할 수 없다:

```java
// 컴파일 불가능 - T는 어디서 왔나??
Transformer<T, T> myTransformer;
```

`Transformer<Object, Object>` 타입으로 만들 수도 있지만, (체인에서 이 Transformer를 통과한) `Observable`은 타입 정보를 잃어버려서 유용하지 않을 수 있다.

이 문제를 풀기 위해, [Collections.emptyList()](http://developer.android.com/reference/java/util/Collections.html#emptyList()) 같은 타입 세이프(type-safe)하고 불변의 빈 컬렉션을 생성하는 메소드들을 가지고 있는 [Collections](http://developer.android.com/reference/java/util/Collections.html)로부터 힌트를 얻었다. 내부적으로는 제네릭이 아닌 객체를 사용하고, 제네릭을 추가한 메소드로 객체를 래핑한다.

scheduler `Transformer` 객체를 정의한 방법은 아래와 같다:

```java
final Transformer schedulersTransformer =
    observable -> observable.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());

@SuppressWarnings("unchecked")
<T> Transformer<T, T> applySchedulers() {
    return (Transformer<T, T>) schedulersTransformer;
}
```

이제 `Transformer` 객체를 딱 하나만 만들게 됐다. 이예이!!

주의: unchecked cast를 사용하면 언제든 문제가 발생할 수 있다. `Transformer`가 타입에 관대하게 만들어야 한다. 그렇지 않으면, 코드가 컴파일 될지라도 실행 시에 `ClassCastException`이 발생할 가능성이 있다. 이 scheduler Transformer 같은 경우에는 Optional의 아이템을 건드리지 않기 때문에 안전하다는 것을 알 수 있다.


# 그렇다면 flatMap()은 어떨까??

이쯤에서 `compose()`와 `flatMap()`을 사용하는 차이가 무엇인지 궁금해질 것이다. 둘 다 `Observable<R>`을 배출(emit[^emit])하여, 연속된 operator들을 재사용할 수 있다는 것을 의미한다. 그죠??

그 차이점은 `compose()`는 더 높은 레벨의 추상화라는 것이다: 즉, 각각의 배출된 아이템이 아니라 모든 스트림에 대해 동작한다. 좀 더 구체적으로 설명하면:

1. `compose()`는 스트림에서 원래의 `Observable<T>`을 가져올 수 있는 유일한 방법이다. 따라서 (`subscribeOn()` 이나 `observeOn()` 같이) 전체 스트림에 적용되는 operator들은 `compose()`를 사용할 필요가 있다.
반대로, `flatMap()`에 `subscribeOn()`/`observeOn()`을 넣는다면, `flatMap()`안에서 생성한 `Observable`에만 적용되고 스트림 전체에 적용되지는 않는다.
1. `compose()`는 마치 operator들을 인라인으로 쓴 것처럼 `Observable` 스트림을 생성하는 즉시 실행된다. `flatMap()`은 `onNext()`가 호출될 때 마다 실행된다. 다른 말로 하면, `flatMap()`은 각 아이템을 변환하지만, `compose()`는 전체 스트림을 변환한다.
1. `flatMap()`은 `onNext()`가 호출될 때 마다 새로운 `Observable`을 생성해야 하므로 어쩔 수 없이 덜 효율적이다. `compose()`는 그 자체로 스트림 상에서 작동한다.

operator를 재사용 가능한 코드로 교체하고 싶다면 `compose()`를 써라. `flatMap()`은 다양한 방식으로 활용이 가능하긴 하지만, 이 문제를 해결하는 방법은 아닌 것 같다.


# 옮긴이의 각주

[^operator]: Operator. RxJava에서 체이닝하는 메소드 동작들을 부르는 용어로 map(), filter(), just(), zip(), delay() 등이 있다. <http://reactivex.io/documentation/operators.html>
[^scheduler]: Scheduler. RxJava에서는 스케줄러를 통해 코드가 동작할 별도의 스레드를 지정할 수 있다. IO 대기처럼 연산은 거의 없고 시간이 오래 걸리는 작업은 별도의 스케줄러가 담당하도록 하고 메인 스레드는 다른 일을 하도록 할 수 있다. Schedulers.computation(), Schedulers.io() 등으로 미리 정의된 스케줄러를 사용할 수도 있고, 스레드풀처럼 별도로 스케줄러를 만들어서 사용할 수도 있다. <http://reactivex.io/documentation/scheduler.html>
[^observable]: Observable. Java의 Stream과 비슷한 개념으로, 아이템을 만들어 낸다. <http://reactivex.io/documentation/observable.html>
[^shudder]: ㄷㄷㄷ. 원문에도 "shudder"라 되어 있음. <http://dic.daum.net/word/view.do?wordid=ekw000151575&q=shudder>
[^transformer]: Transformer로는 RxJava의 Observable.compose()나 Java stream의 map(), Reactor Mono/Flux의 map() 등이 있다.
[^emit]: emit. 아이템 등을 '배출한다', '출력한다' 등의 의미를 나타내는 RxJava의 표현.
