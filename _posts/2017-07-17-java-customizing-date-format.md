---
layout: post
title:  "[Java] Customizing date format - 날짜 표현하기 SimpleDateFormat"
date:   2017-07-17 21:18:00 +0900
published: true
categories: [ java ]
tags: [ java, date, date format, SimpleDateFormat ]
---

> 이전 블로그에서 옮겨온 포스트

날짜 표현하는데는 [DateFormat](http://docs.oracle.com/javase/8/docs/api/java/text/DateFormat.html)이 있다. 하지만 내가 원하는 패턴으로 보여주지 않는다. 이 클래스를 사용하면 "2009년 5월 29일 금요일" 또는 "2009. 5. 29", "09. 5. 29"와 같이 보여준다. 내가 원하는 표시형태는 "20090529"인데..

```java
Date now = new Date();

DateFormat format1 = DateFormat.getDateInstance(DateFormat.FULL);
System.out.println(format1.format(now));
DateFormat format2 = DateFormat.getDateInstance(DateFormat.LONG);
System.out.println(format2.format(now));
DateFormat format3 = DateFormat.getDateInstance(DateFormat.MEDIUM);
System.out.println(format3.format(now));
DateFormat format4 = DateFormat.getDateInstance(DateFormat.SHORT);
System.out.println(format4.format(now));
```

결과는 아래처럼..

```
2009년 5월 29일 금요일
2009년 5월 29일 (금)
2009. 5. 29
09. 5. 29
```

자.. 그럼 내가 원하는 날짜표현을 쓸 수 있는 방법을 찾아보자. 다으밍(Google은 Googling이니깐 Daum은 Dauming인겨?? =ㅅ=a)을 했다. 워낙 많이 찾는 내용이라 잘 나온다. ㅋㅋ

패턴을 사용하는 방법 중 간단한 방법은 을 사용하는 방법이다. [SimpleDateFormat](http://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html)은 DateFormat을 상속받아 사용하기 편하게 이쁘게 되어 있다. 이 클래스를 사용하면 우리가 자주 사용하는 "yyyyMMdd"와 같은 표현을 사용할 수 있다.

```java
Date now = new Date();

SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
System.out.println(format.format(now)); // 20090529
format = new SimpleDateFormat("E MMM dd HH:mm:ss", Locale.UK);
System.out.println(format.format(now)); // Fri May 29 11:06:29
```

{% include google-ad-content %}

패턴 사용법은 누구나 아니까 패스~~하고, 패턴 구문만 살짝.. [튜토리얼](http://java.sun.com/docs/books/tutorial/i18n/format/simpleDateFormat.html)에서도 사용법을 알 수 있다.

![Date format pattern syntax]({{ site.baseurl }}/assets/img/2017-07-17-java-simple-date-format1.png)

그리고 아래의 간단한 샘플은 API문서에서..

![Customized date format sample]({{ site.baseurl }}/assets/img/2017-07-17-java-simple-date-format2.png)


# 참고

- [SimpleDateFormat](http://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html)
- [Date and Times - Customizing Formats](http://docs.oracle.com/javase/tutorial/i18n/format/simpleDateFormat.html)
