---
layout: post
title:  "[Hibernate] \"A collection with cascade=\"all-delete-orphan\" was no longer referenced by the owning entity instance\""
date:   2016-04-26 23:53:00 +0900
categories: [ hibernate ]
tags: [ hibernate, jpa, orphan, orphanremoval, onetomany ]
---

JPA entity안에 있는 list를 바꿔서 저장했더니 갑자기 이런 오류가 뙇!!

```
"A collection with cascade="all-delete-orphan" was no longer referenced by the owning entity instance"
```

수정하는 코드는 요렇게..

```java
List<String> names = new ArrayList<String>();
names.add("ABCD");
names.add("EFGH");
names.add("IJHL");
holder.setNames(names);
```

원인이 뭘까??

names 라는 list는 새로 만든 녀석이다. 새로 만들어진 친구는 hibernate가 관리하지 않아서 문제가 된다고 한다. [http://stackoverflow.com/questions/5587482/hibernate-a-collection-with-cascade-all-delete-orphan-was-no-longer-referenc](http://stackoverflow.com/questions/5587482/hibernate-a-collection-with-cascade-all-delete-orphan-was-no-longer-referenc)

list를 바꾸고 싶으면 새 list를 만들어서 set하지 말고, 내용(content)을 지우고 새로 넣자.

```java
holder.getNames().clear();
holder.getNames().addAll(names);
```

name을 바로 건드리지 말고, 이런 편의 메서드를 추가해도 좋을듯 (상황에 따라..)

```java
public void setNames(List<String> names) {
    this.names.clear();
    this.names.addAll(names);
}
public void addName(String name) {
    names.add(name);
}
public void removeName(String name) {
    names.remove(name);
}
```
