---
layout: post
title:  "[JUnit] Collection 안에 있는 property 테스트 - hasProperty, containsInAnyOrder"
date:   2019-09-01 21:18:00 +0900
published: true
categories: [ java ]
tags: [ java, junit, test, collection, property, field, value, hasProperty, containsInAnyOrder ]
---

JUnit으로 객체가 property(field)로 특정값들을 가지고 있는지 체크할 때는 `hasProperty`를 사용하면 된다.

```java
// "bar" 라는 property가 있는지 체크
assertThat(new Foo(10), hasProperty("bar"));

// "bar" 라는 property가 있고, 그 값이 10인지 체크
assertThat(new Foo(10), hasProperty("bar", is(10)));


@Getter
@AllArgsConstructor
public class Foo {
    private int bar;
}
```

그리고 이 테스트를 Collection에 있는 객체들을 대상으로 체크하고 싶다면 `containsInAnyOrder`와 함께 사용하면 된다.

```java
    Set<Foo> actual = ImmutableSet.of(new Foo(10), new Foo(20), new Foo(30));

    assertThat(actual, containsInAnyOrder(
        hasProperty("bar", is(10)),
        hasProperty("bar", is(20)),
        hasProperty("bar", is(30))
    ));
```


# public class 이느뇨??

하지만!! (두둥!!) 마음대로 되면 재미가 없는지.. 막상 테스트를 만들고 돌리니 아래처럼 오류 메시지만 나오고 원하는대로 제대로 테스트가 돌지 않는다.

```java
java.lang.AssertionError:
Expected: iterable over [hasProperty("bar", is <10>), hasProperty("bar", is <20>), hasProperty("bar", is <30>)] in any order
     but: Not matched: <kr.leocat.test.PropertyTest$Foo@574caa3f>

    at org.hamcrest.MatcherAssert.assertThat(MatcherAssert.java:20)
    at org.junit.Assert.assertThat(Assert.java:956)
    at org.junit.Assert.assertThat(Assert.java:923)
    at kr.leocat.test.PropertyTest.test(PropertyTest.java:18)
    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
    at java.lang.reflect.Method.invoke(Method.java:498)
    at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50)
    at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
    at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47)
    at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)
    at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325)
    at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:78)
    at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:57)
    at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)
    at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)
    at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)
    at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58)
    at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)
    at org.junit.runners.ParentRunner.run(ParentRunner.java:363)
    at org.junit.runner.JUnitCore.run(JUnitCore.java:137)
    at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68)
    at com.intellij.rt.execution.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:47)
    at com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:242)
    at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:70)
```

테스트코드는 아래와 같다. `containsInAnyOrder`와 함께 사용한 것 말고는 특별히 이상한 것은 없다.

```java
package kr.leocat.test;

import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import org.junit.Test;

import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class PropertyTest {

    @Test
    public void test() {
        Set<Foo> actual = ImmutableSet.of(new Foo(10), new Foo(20), new Foo(30));

        assertThat(actual, containsInAnyOrder(
            hasProperty("bar", is(10)),
            hasProperty("bar", is(20)),
            hasProperty("bar", is(30))
        ));
    }

    // 결론부터 말하면, 이 테스트 클래스가 public 이야아 한다.
    @Getter
    private class Foo {
        private int bar;

        public Foo(int bar) {
            this.bar = bar;
        }
    }

}
```

{% include google-ad-content %}

그런데 왜 안 될까?? `containsInAnyOrder`를 `hasItem(allOf())`로 바꿔서 테스트해 봐도 동일한 오류가 발생한다.

결국 `containsInAnyOrder`를 제거하고 테스트하니 아래처럼 오류 메시지가 보였다. 테스트만을 위해 `Foo` 클래스님을 private inner class로 만들었더니 접근이 안 된단다. (아.. 토나와. 처음부터 쫌만 더 친절히 알려주지!! T_T)

```java
java.lang.AssertionError:
Expected: hasProperty("bar", is <10>)
     but: Class org.hamcrest.beans.HasPropertyWithValue$1 can not access a member of class kr.leocat.test.PropertyTest$Foo with modifiers "public"
```


# 결론

객체가 property를 가졌는지 또 특정 값을 가졌는지 테스트는 `hasProperty`를 통하면 된다. collection에 담긴 객체는 `containsInAnyOrder`와 `hasProperty`를 함께 사용하면 된다.

- `org.hamcrest.Matchers#hasProperty(java.lang.String, org.hamcrest.Matcher<?>) `를 사용할 때는 클래스가 `public`으로 **외부에서 접근이 가능해야 한다**.
- `org.hamcrest.Matchers#hasProperty(java.lang.String)`는 **외부에서 접근이 가능하지 않아도 된다**.
