---
layout: post
title:  "[Gradle] javax 클래스를 찾지 못 하는 문제"
date:   2021-03-19 22:18:00 +0900
published: true
categories: [ gradle ]
tags: [ gradle, java ee, jakarta ee, jakarta, java, javax ]
---

Java EE 모듈은 Jakarta EE로 옮겨졌다. 그래서.. 아래와 같은 클래스를 찾지 못 하는 에러를 만날 수 있다. 해결 방법은 아주 간단하게 dependency만 추가하면 된다.

이 오류는 gradle 빌드 시 `--stacktrace` 옵션을 함께 주어야 보인다.

```bash
$ ./gradlew clean check --stacktrace
```

`--stacktrace` 옵션이 없으면 아래처럼 단순히 빌드가 실패했다고만 나온다.

```
FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':leocat-service:kaptKotlin'.
> A failure occurred while executing org.jetbrains.kotlin.gradle.internal.KaptExecution
   > java.lang.reflect.InvocationTargetException (no error message)
```


# javax/persistence/Entity

```
Caused by: com.sun.tools.javac.processing.AnnotationProcessingError: java.lang.NoClassDefFoundError: javax/persistence/Entity
        at jdk.compiler/com.sun.tools.javac.processing.JavacProcessingEnvironment.callProcessor(JavacProcessingEnvironment.java:992)
        at jdk.compiler/com.sun.tools.javac.processing.JavacProcessingEnvironment.discoverAndRunProcs(JavacProcessingEnvironment.java:896)
        at jdk.compiler/com.sun.tools.javac.processing.JavacProcessingEnvironment$Round.run(JavacProcessingEnvironment.java:1222)
        at jdk.compiler/com.sun.tools.javac.processing.JavacProcessingEnvironment.doProcessing(JavacProcessingEnvironment.java:1334)
        at jdk.compiler/com.sun.tools.javac.main.JavaCompiler.processAnnotations(JavaCompiler.java:1258)
        ... 34 more
Caused by: java.lang.NoClassDefFoundError: javax/persistence/Entity
        at com.querydsl.apt.jpa.JPAAnnotationProcessor.createConfiguration(JPAAnnotationProcessor.java:37)
        at com.querydsl.apt.AbstractQuerydslProcessor.process(AbstractQuerydslProcessor.java:83)
        at org.jetbrains.kotlin.kapt3.base.incremental.IncrementalProcessor.process(incrementalProcessors.kt:89)
        at org.jetbrains.kotlin.kapt3.base.ProcessorWrapper.process(annotationProcessing.kt:166)
        at jdk.compiler/com.sun.tools.javac.processing.JavacProcessingEnvironment.callProcessor(JavacProcessingEnvironment.java:980)
        ... 38 more
Caused by: java.lang.ClassNotFoundException: javax.persistence.Entity
        ... 43 more
```

`build.gradle.kts`에 다음 kapt를 추가한다.

```
dependencies {
    kapt("jakarta.persistence:jakarta.persistence-api")
}
```


## javax/annotation/Generated

```
Caused by: java.lang.NoClassDefFoundError: javax/annotation/Generated
        at com.querydsl.codegen.EntitySerializer.introImports(EntitySerializer.java:423)
        at com.querydsl.codegen.EntitySerializer.intro(EntitySerializer.java:269)
        at com.querydsl.codegen.EntitySerializer.serialize(EntitySerializer.java:594)
        at com.querydsl.apt.AbstractQuerydslProcessor.serialize(AbstractQuerydslProcessor.java:606)
        at com.querydsl.apt.AbstractQuerydslProcessor.serializeMetaTypes(AbstractQuerydslProcessor.java:526)
        at com.querydsl.apt.AbstractQuerydslProcessor.process(AbstractQuerydslProcessor.java:98)
        at org.jetbrains.kotlin.kapt3.base.incremental.IncrementalProcessor.process(incrementalProcessors.kt:89)
        at org.jetbrains.kotlin.kapt3.base.ProcessorWrapper.process(annotationProcessing.kt:166)
        at jdk.compiler/com.sun.tools.javac.processing.JavacProcessingEnvironment.callProcessor(JavacProcessingEnvironment.java:980)
        ... 38 more
Caused by: java.lang.ClassNotFoundException: javax.annotation.Generated
        ... 47 more
```

`build.gradle.kts`에 다음 kapt를 추가한다.

```
dependencies {
    kapt("jakarta.annotation:jakarta.annotation-api")
}
```


# 참고

- [How can be solved java.lang.NoClassDefFoundError: javax/annotation/Generated?](https://stackoverflow.com/questions/48238014/how-can-be-solved-java-lang-noclassdeffounderror-javax-annotation-generated)
- [Transition from Java EE to Jakarta EE](https://blogs.oracle.com/javamagazine/transition-from-java-ee-to-jakarta-ee)
