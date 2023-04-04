---
layout: post
title:  "[Elasticsearch] should 절에 대한 오해(와 진실까지는 모르겠고..) - should는 엄밀히 말하면 OR가 아니다"
date:   2023-03-28 21:18:00 +0900
published: true
categories: [ elasticsearch ]
tags: [ elasticsearch, should, clause, bool, boolean, query, misunderstanding, or, minimum_should_match, option ]
---

# 오해

Elasticsearch를 사용하면서 Boolean query 등에서 필터링을 위한 조건으로 `must`와 `should`가 많이 사용된다. (`must_not`도 있고 많지만 여기서는 `should`에 관한 이야기만을 다룬다.)

`must`와 `should`는 단순히 이렇게만 알고 사용했다. 아마도 빠르게 Elasticsearch를 도입해서 사용하는 많은 팀에서 이렇게 인식하고 사용하고 있을지 모른다.

- `must`: `AND`처럼 모든 조건을 만족해야 함
- `should`: `OR`처럼 하나의 조건만 만족해도 됨

많은 개발언어들이 `AND`와 `OR` 같은 논리 연산자를 가지고 있고, Elasticsearch도 의례 그러려니 하는 생각과 당장의 비즈니스 문제를 해결하기 위해 필요로 하는 것만을 보는 시각에 갇혀 있었다.

다시 [문서](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-bool-query.html#score-bool-filter)를 살펴 봤다.


# “해도 좋고”의 취향반영

should는 “해도 좋고..”의 **취향반영** 정도의 느낌이다. 여기서 느낌이라고 표현한 것은 영어공부할 때 어려운 조동사의 그 느낌처럼 “해도 좋고..” 느낌으로 각 상황에 맞게 사용/해석해야 하는 점 때문이다. 그래서인지 `should` 의 옵션으로 `minimum_should_match` 설정이 있는데, 여러 `should` 절 옵션 중에 몇 개가 매칭되어야 하는지를 결정할 수 있다. `should` 절에 나열된 것들 중 어떤 것이든 만족해도 좋은데, 최소한 몇 개 이상이 되어야 한다는 의미로 해석하면 좋을 것 같다.

`minimum_should_match` 설정의 기본값은 `bool` 쿼리에 `must` 절이나 `filter` 절이 있으면 `1`, 그 이외에는 `0`이다. 따라서 아래와 같이 `must`와 함께 사용된 `bool` 쿼리에서는 `should` 절의 3개의 조건 중 하나만 만족하면 된다. 하나만 만족하면 되기 때문에 `OR`로 오해받는 포인트가 된다.

```
POST _search
{
  "query": {
    "bool" : {
      "must" : {
        "term" : { "user.id" : "kimchy" }
      },
      "should" : [
        { "term" : { "tags" : "env1" } },
        { "term" : { "tags" : "deployed" } },
        { "term" : { "tags" : "production" }
      ]
    }
  }
}
```

위의 쿼리에서 `minimum_should_match` 설정을 아래처럼 2로 바꾸면, `should` 절의 조건 3개 중 2개는 매칭되어야 검색이 되기 때문에 `OR` 라고 생각하면 안 된다. `tags`에 `env1`, `deployed`, `production` 3개 중 어떤 것이든 2개가 매칭되면 되고, `OR`와는 의미가 달라진다.

```
POST _search
{
  "query": {
    "bool" : {
      "must" : {
        "term" : { "user.id" : "kimchy" }
      },
      "should" : [
        { "term" : { "tags" : "env1" } },
        { "term" : { "tags" : "deployed" } },
        { "term" : { "tags" : "production" }
      ],
      "minimum_should_match" : 2
    }
  }
}
```


# minimum_should_match

`minimum_should_match` 설정의 기본값은 1 또는 0으로 정수이지만 퍼센티지를 설정할 수도 있고, 심지어 음수까지도 가능하다. ([minimum_should_match parameter - Elasticsearch Guide](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-minimum-should-match.html))

- 양수 정수 (e.g. 3): `should`의  옵션 중 몇 개가 매칭되어야 함.
- 음수 정수 (e.g. -2): `should`의 옵션 중 전체 개수에서 이 수치 만큼은 뺀 개수가 매칭되어야 함. -1인 경우, 전체 옵션 개수가 3개이면 2, 4개이면 3과 갉음.
- 비율 (e.g. 75%): `should`의 전체 옵션 개수 중 이 비율 이상 매칭되어야 함. 옵션 개수에 이 비율을 곱해서 반내림한 개수 이상 매칭 되어야 함.
- 음수 비율 (e.g. -25%): 음수 정수처럼 `should`의 전체 옵션 개수 중 제외할 만큼의 비율. 역시 이 비율의 반내림한 개수에 음수를 붙인 값이다.
- 여러 조합도 가능 (e.g. 3<90%, 2<-25% 9<-3, ..)

주의할 점은 비율을 사용할 때 반내림으로 인한 개수 차이이다. 75%와 -25%는 옵션이 4개일 때는 모두 3개 이상 만족하면 되지만, 옵션이 5개가 되면 75%는 3이고 -25%는 -1이 되어 4개를 만족해야 한다.


# 참고

- [Boolean query - Elasticsearch Guide](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-bool-query.html#score-bool-filter)
- [minimum_should_match parameter - Elasticsearch Guide](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-minimum-should-match.html)
