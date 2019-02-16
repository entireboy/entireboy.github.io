---
layout: post
title:  "[발번역] 불변 인프라란 무엇인가?? - What is immutable infrastructure??"
date:   2018-09-01 23:18:00 +0900
published: true
categories: [ translation ]
tags: [ clumsylation, translation, translate, immutable infrastructure, mutable infrastructure, immutable, mutable, infrastructure, infra, centralized logging, logging, log ]
---

> 본 글은 아래 글을 **[발번역]({{ site.baseurl }}/tags/clumsylation)** 한 내용입니다. 누락되거나 잘못 옮겨진 내용이 있을(~~아니 많을;;~~) 수 있습니다.
>
> 원글: What Is Immutable Infrastructure? by Hazel Virdó
> <https://www.digitalocean.com/community/tutorials/what-is-immutable-infrastructure>
>
> 본 글은 다음 라이선스에 따라 사용할 수 있습니다: ![license](https://i.creativecommons.org/l/by-nc-sa/4.0/80x15.png) [Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License](https://creativecommons.org/licenses/by-nc-sa/4.0/)


# 개요

전형적인 가변 인프라(mutable infrastructure)에서 서버는 끊임없이 업데이트 되고 수정된다. 가변 인프라 환경에서 엔지니어와 관리자는 서버에 ssh로 접속하고 수동으로 패키지를 업/다운그레이드 하고, 서버 하나하나의 설정 파일을 수정하고, 새 코드를 직접 기존 서버에 배포한다. 즉, 이러한 서버는 가변적이며, 생성된 뒤에 변경될 수 있다. 가변 서버로 구성된 인프라는 가변적, 전통적, 또는 (폄하하여) 구식이라고 불린다.

*불변 인프라(immutable infrastructure)* 는 서버가 배포된 이후 절대 변경되지 않는 형태의 인프라 패러다임이다. 만약 어떤 사항을 업데이트하거나 수정, 변경해야 할 경우, 공용 이미지에 적절한 수정을 한 새 서버가 프로비저닝(provision[^provisioning])되어 기존 서버를 대체한다. 새 서버는 검증이 완료되면 서비스에 투입되고 기존 서버는 더 이상 사용하지 않게 된다.

불변 인프라의 이점은 인프라의 일관성과 신뢰성, 그리고 더 간단하고 예측 가능한 배포 프로세스에 있다. 컨피그레이션 드리프트(configuration drift[^configuration-drift])나 스노우플레이크 서버(snowflake servers. 눈송이 서버[^snowflake-server]) 같은 가변 인프라에서 자주 나타나는 문제의 발생을 줄이거나 완전히 방지한다. 그러나 효율적으로 가변 인프라를 사용하기 위해서는 종합적인 배포 자동화와 클라우드 환경에 빠른 서버 프로비저닝, 스테이트풀을 다루(handle stateful)거나 로그와 같이 일시적인 데이터를 다룰 솔루션이 필요하다.

이 글에서는 앞으로 다음과 같은 내용을 다루고자 한다:

- 가변 인프라와 불변 인프라의 차이를 개념적/실제적으로 설명
- 불변 인프라 사용의 이점 설명 및 복잡성 개념화
- 구현 디테일과 불변 인프라의 필수 구성요소를 고차원적으로 살펴보기


# 가변 인프라와 불변 인프라의 차이

가변 인프라와 불변 인프라의 가장 근본적인 차이는 핵심이 되는 정책(central policy)이다: 전자의 구성요소는 배포된 이후 변경되도록 설계되었다. 후자의 구성요소는 변경되지 않고 궁극적으로는 아예 교체되도록 만들어졌다. 이 튜토리얼은 구성요소로 서버에 초점을 두지만, 불변 인프라를 구현하는 방법에는 컨테이너를 사용하는 방식과 같이 동일한 고차원적인 컨셉이 적용가능한 다른 방식도 있다.

좀더 심도있게 살펴보면, 서버 기반의 가변 인프라와 불변 인프라 간에는 실제적인 차이와 개념적인 차이가 있다.

먼저 개념적으로 살펴보면, 두 인프라는 서버를 다루는 방식(예: 생성, 관리, 업데이트, 제거)에 관한 접근방법이 매우 다르다. 일반적으로 “애완동물 vs 소떼(pets vs cattle)” 개념으로 묘사된다.

실제 적용의 관점에서 말하면, 가변 인프라는 불변 인프라를 사용 가능하게 만든 가상화나 클라우드 컴퓨팅 같은 핵심 기술 보다 앞선 훨씬 오래된 인프라 패러다임 중 하나이다. 이러한 역사를 알면 둘의 개념적인 차이와 오늘날 인프라에서 가변 인프라와 불변 인프라 중 하나를 사용하는 것의 의미를 쉽게 이해할 수 있다.

다음 두 섹션에서는 가변 인프라와 불변 인프라의 차이점을 좀 더 자세히 살펴 본다.


## 실제 구현방식의 차이: 클라우드 수용

가상화나 클라우드 컴퓨팅이 가능해 지고 널리 사용되기 이전에 서버 인프라는 물리 서버를 중심으로 구성되었다. 이 물리 서버는 비용이 많이 들고 생성하는 시간도 오래 걸렸다. (새 하드웨어를 주문하고 조립하고, [colo](https://en.wikipedia.org/wiki/Colocation_centre)[^colo] 같은 곳에 넣는 등 초기 설치 작업은 며칠 또는 몇 주가 걸릴 수 있다.)

가변 인프라는 여기서 착안되었다. 서버 교체 비용이 너무 높기 때문에, 서버를 다운타임 없이 가능한 오래 돌리도록 유지하는 것이 실용적이었다. 정기 배포와 업데이트를 위한 수많은 변경뿐만 아니라, 뭔가 잘못 될 때 마다 임시 수정(ad-hoc fix)과 변경(tweak), 패치가 생겨남을 뜻한다. 수동적인 변경이 자주 이루어진 결과, 서버는 복제하기 어려워지며 서버 하나 하나는 인프라 전체에서 동일하지 않고 각자 고유한(unique) 개체가 되면서 동시에 취약한 구성요소가 되어버린다.

[가상화와 온디멘드/클라우드 컴퓨팅](https://www.digitalocean.com/community/tutorials/an-introduction-to-cloud-hosting)의 등장은 서버 아키텍처에 터닝 포인트가 되었다. 가상 서버는 비용이 저렴하고 확장 가능하기까지 하고, 생성하고 없애는데 며칠/몇 주가 아니라 몇 분 밖에 걸리지 않는다. 가상 서버의 등장으로 [설정관리(configuration management)](https://www.digitalocean.com/community/tutorials/an-introduction-to-configuration-management)라든지 [클라우드 API](https://www.digitalocean.com/community/tutorials/how-to-use-the-digitalocean-api-v2)를 사용해 빠르고 프로그래밍 친화적(programmatic)이며 자동으로 새 서버에 프로비전하는 등의 새로운 배포 흐름이 처음으로 가능해졌다. 빠른 속도와 낮은 비용으로 새로운 가상 서버를 생성한다는 점이 불변 인프라의 실용도가 높아지도록 해주었다.

전통적인 가변 인프라는 원래 관리를 위한 명령어를 입력 받는 물리서버를 위해 개발되었고, 오랜 시간 동안 기술이 발전됨에 따라 계속해서 개선되어 왔다. 배포 후 서버를 수정하는 패러다임은 오늘날의 인프라에서도 여전히 일반적이다. 반면, 불변 인프라는 처음부터 클라우드 컴퓨팅의 가상 서버처럼 아키텍처 구성요소를 빠르게 프로비저닝 하기 위한 가상화 기반 기술을 바탕으로 설계되었다.


## 개념적 차이: 애완동물 vs 소떼(Pets vs Cattle), 스노우플레이크 vs 피닉스(Snowflakes vs Phoenixes)

클라우드 컴퓨팅이 발전하게 된 근본적인 개념 변화는 서버를 일회용으로 간주할 수 있다는 것이다. 물리 서버를 폐기하고 교체한다는 것은 엄청나게 비현실적이지만, 가상 서버로는 서버의 폐기와 교체가 가능할 뿐 아니라 쉽고 효율적이기까지 하다.

전통적인 가변 인프라에서의 서버는 항상 작동하고 있어야 하는 대체할 수 없는 유일한 시스템이다. 이러한 점을 고려해보면 가변 인프라는 독특하고 아무나 따라할 수 없으며, 손으로 돌봐줘야 하는애완동물(pets)과 같다. 이러한 애완동물 하나를 잃어버리는 것은 끔찍한 일이다. 반면, 불변 인프라의 서버는 폐기할 수 있고 자동화 된 툴로 쉽게 교체하거나 확장할 수 있다. 이러한 특징을 생각해 보면 불변 인프라는 각각의 서버가 동일하지 않고 유니크하거나 대체 불가능하지 않은 소떼(cattle)에 비유될 수 있다.

“애완동물 vs 소떼”라는 비유를 클라우드 컴퓨팅에 처음 적용한 [Randy Bias](http://cloudscaling.com/blog/cloud-computing/the-history-of-pets-vs-cattle/)의 말을 인용하면:

> 기존의 오래된 방식에서는 서버를 애완동물처럼 다룬다. 메일 서버 ‘밥(Bob)’의 예를 생각해 보자. 만약 ‘밥’이  다운되면 전 직원이 달려들어 고치려고 안간힘을 쓰지만, CEO는 이메일을 받을 수 없고 그것으로 끝장이다. 새로운 방식에서는 서버를 다수의 소떼처럼 간주하고 www001 부터 www100 까지 각각의 서버에 번호를 붙인다. 서버 하나가 다운되면 꺼내서 제거하고 즉시 교체한다.

서버를 다루는 방식에 대한 차이를 묘사하는 또 다른 비유로 스노우플레이크 서버(snowflake servers, 눈송이 서버)와 피닉스 서버(phoenix servers, 불사조 서버)가 있다.

[스노우플레이크 서버](https://martinfowler.com/bliki/SnowflakeServer.html)는 애완동물과 비슷하다. 손으로 일일이 관리되고 잦은 업데이트와 수정 등을 통해 스노우플레이크 서버는 각각의 환경이 점점 유니크해 진다(leading to unique environment). [피닉스 서버](https://martinfowler.com/bliki/PhoenixServer.html)는 소떼와 비슷하다. 항상 처음부터 빌드되고 자동화된 절차를 통해 재생성(또는 “불사조처럼 부활”)하기 쉽다.

가변 인프라가 일부(또는 많은) 애완동물이나 스노우플레이크 서버로 구성되는데 비해, 불변 인프라는 거의 대부분 소떼나 피닉스 서버로 구성된다. 다음 섹션에서는 이 둘의 결과를 살펴보도록 하자.


# 불변 인프라의 이점

불변 인프라의 이점을 이해하기 위해서는 가변 인프라의 단점을 살펴보는 것이 필요하다.

가변 인프라의 서버는 문서화 되지 않고 즉흥적인 변경으로 서버 설정이 다른 서버나 리뷰/승인 받은 내용, 원래 배포된 설정과 점점 달라지면서 컨피그레이션 드리프트로 인해 힘들어질 수 있다. 점차적으로 증가하는 스노우플레이크 서버는 다시 생성하고 교체하기 어려워서 확장이나 문제 복구가 힘들다. 심지어 실서비스 환경과 동일한 스테이지 환경을 만들기도 어렵기 때문에 디버깅하기 위해 문제를 재연하는 것도 까다로워진다.

수동으로 많은 수정을 하게 되면 각 서버 마다 설정된 다른 설정들의 왜 중요한지 또는 왜 필요한지가 불분명해 지고, 업데이트나 변경으로 인해 의도치 않은 부작용이 나타날 수 있다. 아무리 가장 좋은 상황하에서라도 이미 존재하는 시스템에 변경을 가하는 것은 원활한 작동을 보장하지는 않는다. 즉, 그렇게 하게 되면 실패할 위험도 있고 서버를 미지의 상태로 만들 수도 있다.

이 점을 염두에 두고 살펴보면, 불변 인프라 사용의 가장 주된 이점은 흔히 나타나는 여러 어려운 점이나 실패 포인트를 궁극적으로 최소화하거나 막아주는 배포의 간단함과 신뢰성, 일관성이다.


### 정상(known-good[^known-good]) 서버 상태와 낮은 배포 실패 확률

불변 인프라의 모든 배포는 검증되고 버저닝된(version-controlled) 이미지를 바탕으로 새 서버를 프로비저닝하여 실행된다. 그 결과, 이러한 배포는 서버의 이전 상태에 의존하지 않으며, 또 그렇기 때문에 실패가 없고 부분적으로만 완성되는 일도 없다.

새 서버가 프로비저닝 되면 서비스에 투입되기 전에 테스트할 수 있고, 로드 밸런서 변경처럼 단 한 번의 변경만으로 새 서버로 서비스할 수 있게 배포 프로세스를 줄인다. 다시 말해, 배포는 성공해서 완료되거나 아무것도 변경되지 않는 [원자성(atomic)](https://en.wikipedia.org/wiki/Linearizability)이 된다.

이러한 특성으로 불변 인프라는 배포를 훨씬 더 신뢰할 수 있게 만들고, 인프라의 모든 서버 상태를 항상 알 수 있는 상태로 보장한다. 또한, 이 프로세스는 다운타임 없는 [블루-그린 배포(blue-green deployment)](https://www.digitalocean.com/community/tutorials/how-to-use-blue-green-deployments-to-release-software-safely)나 [롤링 릴리즈(rolling releases)](https://en.wikipedia.org/wiki/Rolling_release)를 쉽게 구현하도록 만든다.


### 컨피그레이션 드리프트나 스노우플레이크 서버 발생 없음 (제로, 미발생)

불변 인프라의 모든 설정 변경은 문서화와 함께 버전 컨트롤에 변경된 이미지를 저장하고, 그 이미지를 교체 서버에 배포하는 자동화 되고 통일된 배포 프로세스를 사용함으로써 구현된다. 쉘로 서버에 접근하는 것은 때에 따라 완전히 제한되기도 한다.

이렇게 스노우플레이크 서버와 컨피그레이션 드리프트의 위험을 제거함으로써 불변 인프라는 복잡하거나 재연이 어려운 설정을 방지한다. 또한, 누군가가 프로덕션 서버를 확실히 이해하지 못한 상태에서 에러가 발생하기 쉽거나 다운타임/의도치 않은 행동을 초래하는 수정을 하는 상황이 발생하는 것도 막는다.


### 일관된 스테이지 환경[^consistent-stage-environment]과 쉬운 수평적 확장

모든 서버가 동일한 생성 프로세스를 이용하기 때문에, 특이 배포 케이스(deployment edge cases)가 없다. 이러한 특징은 서비스 환경을 복제하는 것을 대수롭지 않게 만들어서 엉망이거나 일관성 없는 스테이지 환경이 되는 것을 방지하고, 인프라에 동일한 서버를 매끄럽게(seamlessly) 추가할 수 있도록 해줌으로써 [수평적 확장(horizontal scaling)](https://blog.digitalocean.com/horizontally-scaling-php-applications/)을 간단하게 해준다.


### 간단한 롤백과 복구 프로세스

이미지 이력을 보관하는 버전 컨트롤을 사용하는 것은 실서비스의 문제를 해결하는데에도 도움이 된다. 새 이미지를 배포하기 위해 사용한 프로세스는 이전 버전으로 롤백하는데에도 또한 동일하게 사용될 수 있으므로, 다운된 서비스를 처리(handling downtime)할 때 복원력이 증가하고 복구 시간도 감소시켜준다.


# 불변 인프라 구현 디테일

불변 인프라는 특히 전통적인 가변 인프라와 비교해서, 그 구현 디테일에 있어 일부 요구사항과 미묘한 차이가 있다.

간단히 불변의 핵심원칙을 지킴으로써 자동화나 툴, 소프트웨어 디자인 원칙과 관계없이 독립적인 불변 인프라를 구현하는 것은 기술적으로 가능하다. 그러나, 아래 항목들은 (대략적인 우선순위 순으로) 규모를 감안할 때 현실적으로 강력히 권고된다..

- **클라우드 컴퓨팅 환경의 서버** 또는 (아래 요구사항 일부를 바꿀지라도 [컨테이너 같은](https://www.youtube.com/watch?v=S3gYxEVz_b8)) 또 다른 가상화된 환경. 여기서의 요점은 API 또는 유사한 것을 통해 생성과 파괴를 자동화한 관리(automated management)뿐만 아니라 커스텀 이미지로 부터 빠르게 프로비저닝 되는 독립적인 인스턴스들을 만들어 내는 것이다.
- 가능하면 이미지 생성 후 검증도 포함된 **전체 배포 파이프라인의 완전 자동화**. 이러한 자동화를 세팅하는 것은 이 인프라를 구현하는 비용에 바로 추가 되지만, 그것은 빠르게 청산할 수 있는 일시적인 비용이다.
- 네트웍을 통해 통신하는 논리적으로 독립된 단위인 모듈로 인프라를 구분하는 **[서비스 지향 아키텍처(SOA)](https://en.wikipedia.org/wiki/Service-oriented_architecture)**. 이는 IaaS, PaaS와 [유사하게 서비스 지향적인](https://en.wikipedia.org/wiki/Cloud_computing#Service_models) 클라우드 컴퓨팅의 이점을 온전히 누릴 수 있도록 해준다.
- 불변 서버를 포함한 **[상태 없고(stateless)](https://en.wikipedia.org/wiki/Service_statelessness_principle) 휘발성(volatile)의 어플리케이션 레이어**. 이 레이어에서는 어떤 것이든 아무런 데이터 손실도 없이(무상태) 언제라도 파괴되고 빠르게 다시 만들어질 수 있다(휘발성).
- 다음을 포함한 **영속 데이터 레이어(persistent data layer)**:
  - 버전이나 git 커밋 SHA 등을 통한 이미지 식별 같은 서버 배포에 관한 상세사항도 포함된 **중앙화된 로깅(centralized logging[^centralized-logging])**. 이 인프라에서 서버는 일회용이(고 계속해서 처분되)기 때문에, 로그와 수집 데이터를 외부에 저장하는 것은 쉘 접속이 제한되거나 서버가 파괴된 이후일지라도 디버깅이 가능하게 해준다.
  - DBaaS/클라우드 데이터베이스와 (클라우드에서 제공되거나 직접 관리하는) 객체/블럭 스토리지 같은 스테이트풀(stateful)이거나 일회성 데이터와 데이터베이스를 위한 **외부 데이터 저장**. 서버가 휘발성이면 로컬 스토리지에 의존할 수 없기 때문에 다른 곳에 데이터를 저장할 필요가 있다.
- 공동의 목표를 위해 협력하고 전념하는 **엔지니어링팀과 운영팀**. 불변 인프라에는 최종 결과물의 간단함을 극대화하기 위한 많은 요소가 있고 아무도 그 전부를 알지 못 한다. 게다가, 이 인프라내에서 진행되는 일부분은 쉘 접근 없는 일회성 작업을 하거나 디버깅 같이 익숙한 일이 아니거나 새로운 일일 수 있다.

이러한 구성요소를 구현하는 데에는 다양한 방법이 있다. 그중 하나를 선택하는 것은 주로 개인적인 선호/친숙함과 그 인프라를 직접 구현할 것인지 유료 서비스를 사용할 것인지에 달려 있다.

[CI/CD툴](https://www.digitalocean.com/community/tutorials/ci-cd-tools-comparison-jenkins-gitlab-ci-buildbot-drone-and-concourse)은 배포 파이프라인 자동화를 시작하기에 좋다. [Compose](https://www.compose.com/)는 DBaaS 솔루션의 한 가지 선택이 될 수 있고, [rsyslog](https://www.digitalocean.com/community/tutorials/how-to-centralize-logs-with-rsyslog-logstash-and-elasticsearch-on-ubuntu-14-04)와 [ELK](https://www.digitalocean.com/community/tutorial_series/centralized-logging-with-elk-stack-elasticsearch-logstash-and-kibana-on-ubuntu-14-04)는 로깅 중앙화(centralized logging)에 널리 사용되는 방법이다. 서비스 환경의 서버를 랜덤하게 죽이는 [Netflix의 Chaos Monkey](https://github.com/Netflix/chaosmonkey)는 최종 셋업 단계에서 마치 실제상황처럼 일부러 어려운 상황을 만든다.


# 결론

이 글은 불변 인프라가 무엇인지와 오래된 가변 인프라와의 개념적, 실제적 측면에서의 차이, 불변 인프라를 사용할 때의 이점, 구현 디테일을 다루었다.

불변 인프라로 옮기는 것을 고려해야 하는지, 또한 언제 옮겨야할지 적절한 시점을 아는 것은 어렵고, 그 누구도 분할점(cutoff point)이나 변곡점(inflection point)을 명확하게 알지 못 한다. 현재 주로 가변 환경에서 작업 중일지라도, 이 글에서 추천한 설정 관리(configuration management) 같은 디자인 사례를 일부 구현해 보는 것이 불변 인프라를 시작하는 방법이 될 수 있다. 이렇게 해보면 향후 불변 인프라로의 전환이 쉬워질 것이다.

위 컴퍼넌트의 대부분을 가진 인프라를 가지고 있고 확장 문제에 이르거나 배포 프로세스의 뒤떨어짐에 좌절하는 당신을 발견한다면, 불변성이 당신의 인프라를 얼마나 개선할지 평가하기 시작할 좋은 때일 수 있다.

불변 인프라를 구현한 ([Codeship](https://blog.codeship.com/immutable-infrastructure/), [Chef](https://blog.chef.io/2014/06/23/immutable-infrastructure-practical-or-not/), [Koddi](https://www.koddi.com/developing-with-an-immutable-infrastructure/), [Fugue](https://fugue.co/assets/docs/Immutable_Infrastructure_Fugue.pdf) 같은) 여러 회사의 사례로 부터 더 많은걸 배울 수 있다.


# 옮긴이 각주

[^provisioning]: 프로비저닝(Provisioning) [wikipedia](https://en.wikipedia.org/wiki/Provisioning_(telecommunications))
[^configuration-drift]: 컨피그레이션 드리프트(Configuration drift): 옮긴이. 시간이 흘러 처음과는 다르게 설정(configuration)이 점점 변형되어 가는 현상을 뜻한다. [[발번역] 컨피그레이션 드리프트]({{ site.baseurl }}{% post_url 2018-08-24-translation-configuration-drift %})
[^colo]: colo: 옮긴이. Collocation centre. IDC의 일종.
[^snowflake-server]: 스노우플레이크 서버(눈송이 서버): 옮긴이. 결정모양이 모두 다른 눈송이처럼 같은 서비스의 서버들이지만 컨피그레이션 드리프트로 인해 모두 조금씩 달라진 서버 설정/상태를 의미한다.
[^known-good]: known-good: 옮긴이. fully test되고 신뢰할만한 등의 의미로 사용되며, known-good software, known-good configuration 등으로 활용된다. cf. [Definition of: known-good software](https://www.pcmag.com/encyclopedia/term/45850/known-good-software), [Last Known Good Configuration](https://www.webopedia.com/TERM/L/Last_Known_Good_configuration.html)
[^consistent-stage-environment]: 옮긴이. 실서비스와 스테이지 환경의 설정이 동일하다는 의미.
[^centralized-logging]: centralized logging: 옮긴이. 여러 서비스와 인스턴스의 로그를 한 곳에서 모아 볼 수 있게 별도 로깅 시스템에 오아두는 것. cf. <https://hackernoon.com/part-1-building-a-centralized-logging-application-5a537033da0a>
