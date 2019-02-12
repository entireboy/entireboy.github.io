---
layout: post
title:  "[Java] JDK9(Java SE 9) 이상에서 JAXB(javax.xml.bind) 클래스 못 찾음 문제"
date:   2019-02-12 22:18:00 +0900
published: true
categories: [ java ]
tags: [ java, jdk, jdk9, jaxb, java se, java ee, module, deprecated ]
---

JAXB API 관련 클래스가 JDK9 부터는 SE에서 EE로 옮겨졌다. Java SE 11 등에서는 `java.xml.bind` 패키지에 있는 클래스를 사용하면 아래와 같은 에러를 볼 수 있다.

```java
Caused by: java.lang.ClassNotFoundException: javax.xml.bind.JAXBException
    at java.base/jdk.internal.loader.BuiltinClassLoader.loadClass(BuiltinClassLoader.java:583)
    at java.base/jdk.internal.loader.ClassLoaders$AppClassLoader.loadClass(ClassLoaders.java:178)
    at java.base/java.lang.ClassLoader.loadClass(ClassLoader.java:521)
    ... 40 more
```


# 해결 방법

실행 시에 `--add-modules` 옵션을 추가로 주면 된다.

```java
--add-modules java.xml.bind
```

Java SE Javadoc을 보면 클래스 위에 `module`이 명시되어 있으니 이 모듈명을 적어주면 된다. <https://docs.oracle.com/javase/9/docs/api/javax/xml/bind/JAXBContext.html>

{% include image.html file='/assets/img/2019-02-12-java-cannot-find-jaxb-from-jdk9-and-above1.png' alt='java.xml.bind javadoc' border='1px' width='300px' %}


# Deprecated Java EE 모듈

JAXB와 비슷하게 SOAP 같은 web service들도 별도의 모듈로 분리가 되어서 `java.xml.ws` 패키지를 사용할 때도 `—add-modules java.xml.ws` 옵션을 주어야 한다. 다음 목록들이 EE 모듈로 구분되어 제거될 예정이다. (JAXB나 JAX-WS처럼 Java SE 9에서 이미 제거된 모듈도 있다.)

```java
java.activation (JAF)
java.corba (CORBA)
java.transaction (JTA)
java.xml.bind (JAXB)
java.xml.ws (JAX-WS)
java.xml.ws.annotation (Common Annotation)
```

모듈 Javadoc을 보면 `@Deprecated(since="9", forRemoval=true)`와 같이 `forRemoval=true`로 적혀 있다. 다음은 [java.xml.bind](https://docs.oracle.com/javase/9/docs/api/java.xml.bind-summary.html) 모듈의 Javadoc이다.

{% include image.html file='/assets/img/2019-02-12-java-cannot-find-jaxb-from-jdk9-and-above2.png' alt='java.xml.bind module javadoc' border='1px' %}


# 참고

- [How to resolve java.lang.NoClassDefFoundError: javax/xml/bind/JAXBException in Java 9](https://stackoverflow.com/questions/43574426/how-to-resolve-java-lang-noclassdeffounderror-javax-xml-bind-jaxbexception-in-j)
- [JDK 11: End of the road for Java EE modules](https://jaxenter.com/jdk-11-java-ee-modules-140674.html)
