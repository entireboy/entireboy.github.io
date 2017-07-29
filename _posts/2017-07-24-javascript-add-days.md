---
layout: post
title:  "[Javascript] 날짜 더하고 빼기"
date:   2017-07-24 21:18:00 +0900
published: true
categories: [ javascript ]
tags: [ javascript, date, add, sub ]
---

> 이전 블로그에서 옮겨온 포스트

오늘 날짜에서 3일 전/후 등의 날짜 계산을 하고 싶다. 하지만 Javascript는 Java의 [java.util.Calendar.add(int, int)](http://docs.oracle.com/javase/7/docs/api/java/util/Calendar.html#add(int,%20int)) 같은 날짜 더하고 빼는 기능이 없다. T_T (Java8 이상이라면 [java.time.LocalDateTime]({{ site.baseurl }}{% post_url 2017-07-25-java-add-days %})을 사용하면 Calendar 보다 편하다.)

Java였다면..
```java
Calendar rightNow = Calendar.getInstance(); // 현재 시간
rightNow.add(Calendar.DATE, 3); // 3일 뒤
rightNow.add(Calendar.MONTH, -3); // 거기에 3달 전
```

이 기능의 가장 편한 점은 년, 월을 자동으로 계산해준다는 점이다. 만일 오늘이 3월 1일인데 3일 전 날짜를 알아야 한다면, 2월의 날짜를 계산해주는건 물론이고 윤달까지 판단해주기 때문에 대똥 좋다!! +_+d

자.. 그럼 Javascript라면 어떻게?!?!
[Date.prototype.setDate()](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Date/setDate)를 사용하면 된다.

```javascript
var theBigDay = new Date(1962, 6, 7); // 1962-07-07
theBigDay.setDate(24); // 1962-07-24
theBigDay.setDate(32); // 1962-08-01 -> 7월은 31일까지만 있으므로, 8월로 자동 계산..

var threeDaysAgo = new Date(2014, 2, 1); // 2014-03-01 - 월은 0에서부터 시작된다.
threeDaysAgo.setDate(threeDaysAgo.getDate() - 3); // 2014-02-26 => 3일전으로~
```

원래 setDate()는 해당 날짜를 설정하는 함수이지만, **해당 달의 날짜를 벗어나는 경우는 이전/이후 달로 자동으로 계산**해준다. 위의 샘플에서 theBigDay에 setDate(24)를 호출한 결과는, 7월에는 24일이 포함되어 있기 때문에 7월 24일로 설정이 된다. 하지만 setDate(32)의 결과는, 7월에는 32일이 없기 때문에 7월의 마지막 날짜인 31일을 뺀 1일이 날짜로 설정되고 다음 달로 넘어가서 8월 1일이 된다.

{% include google-ad-content %}

이런 성질을 잘 이용해서 Date 객체의 날짜를 기준으로 x일 전/후 날짜를 구할 수 있다. 3월 1일로 설정한 threeDaysAgo의 getDate()로 현재 날짜를 가져와서 +- 날짜를 설정해 주자. :D


그리고 뽀나스~ 위의 방법으로 해당 월의 마지막날짜 구하기
```javascript
var d1 = new Date(2014, 2, 1); // 2014-03-01
d1.setDate(0); // 2014-02-28 => 2월 마지막날
var d2 = new Date(2014, 2, 1); // 2014-03-01
d2.setDate(-1); // 2014-02-27 => 2월 마지막날에서 하루 전
```

setDate(0)을 호출하면 해당 월에는 0일이 없으므로, 이전달 마지막 날로 넘어가게 된다.

# 참고

- [Date.prototype.setDate()](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Date/setDate)
- [How to add number of days to today's date?](https://stackoverflow.com/questions/3818193/how-to-add-number-of-days-to-todays-date)
- [java.time.LocalDateTime]({{ site.baseurl }}{% post_url 2017-07-25-java-add-days %})
