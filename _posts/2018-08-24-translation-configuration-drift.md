---
layout: post
title:  "[발번역] 컨피그레이션 드리프트 - Configuration Drift"
date:   2018-08-24 23:18:00 +0900
published: true
categories: [ translation ]
tags: [ clumsylation, translation, translate, configuration drift, configuration, drift, config ]
---

> 본 글은 아래 글을 **[발번역]({{ site.baseurl }}/tags/clumsylation)** 한 내용입니다. 누락되거나 잘못 옮겨진 내용이 있을(~~아니 많을;;~~) 수 있습니다. 어색하거나 잘못된 표현은 <a href="{{ site.baseurl }}/about">알려주세요</a>.
>
> 원글: Configuration Drift by Kief
> <http://kief.com/configuration-drift.html>

[서버 라이프사이클](http://kief.com/ops/automated-server-management-lifecycle.html)에 관한 이전 글에서 **컨피그레이션 드리프트(Configuration Drift)** 를 이야기했었다. 컨피그레이션 드리프트는 손으로 직접 수정한 임시 수정/업데이트와 전반적인 엔트로피(entropy) 증가로 인해 인프라의 서버들이 시간이 갈수록 점점 서로 다른 상태가 되는 현상이다.

필자가 이상적이라고 생각하는 좋은 자동화된 서버 프로비저닝 프로세스는 장비(machine)가 생성될 때 일관된 상태가 될 수 있도록 해준다. 그러나 장비의 라이프사이클 동안 초기 설정으로 부터 멀어지고(drift) 다른 장비들과도 서로 달라진다.

컨피그레이션 드리프트를 방지하는 방법에는 두 가지가 있다. 하나는 Puppet[^puppet]이나 Chef[^chef] 같은 자동화된 설정 툴(configuration tool[^configuration-management-tool])을 사용하여 모든 장비가 동일선상에 있을 수 있도록 자주 반복해서 실행시켜 주는 것이다. 또 다른 방법은 초기설정으로부터 멀어질 시간을 주지 않도록 장비 인스턴스를 자주 다시 빌드(rebuild) 해 주는 것이다.

자동화된 설정 툴의 문제는 이런 툴들은 장비 상태의 일부만을 관리한다는 점이다. manifest/recipe/script 등을 만들고 유지/보수하는 것은 시간이 많이 들기 때문에 대부분의 팀은 (초기 설정으로부터 멀어진 커다란) 차이는 그대로 둔 채 시스템의 가장 중요한 부분의 자동화에만 노력을 기울이는 경향이 있다.

그렇게 자주 바뀌지 않고 평소에는 중요하지 않은 시스템의 부분을 해결하려고 과도하게 노력을 기울일 때, 이런 차이를 줄이기 위한 노력에 비해 그 노력의 보상(returns)이 훨씬 못 미친다.

반면에, 장비를 충분히 자주 리빌드한다면, 프로비전 된 후에 실행되는 설정 변경을 걱정할 필요가 없다. 그러나, 웹 서버 설정 변경과 같이 상당히 사소한 변경의 부담이 늘어날 수 있다.

실제 서비스 상에서 대부분의 인프라는 이 방법들을 조합해서 사용하는 것이 아마도 최선일 것이다. 효과가 가장 극대화 될 수 있는 장비 설정에 꾸준히 업데이트되는 자동화된 설정을 사용하고, 장비가 자주 리빌드될 수 있도록 해라.

리빌드하는 빈도는 제공하는 서비스의 특성과 인프라 구성에 따라 다양할 것이다. 또한, 장비의 종류에 따라서도 다를 수 있다. 예를 들어, DNS처럼 네트웍 서비스를 제공하는 장비는 매주 리빌드할 수 있지만, 배치 작업을 하는 장비는 필요에 따라 리빌드할 수 있다.


# 옮긴이 각주

[^puppet]: Puppet, <https://puppet.com/>
[^chef]: Chef, <https://www.chef.io/chef/>
[^configuration-management-tool]: Configuration management tool: 옮긴이. 서버 또는 인프라 장비의 설정 등을 스크립트로 작성해서 동일한 설정의 서버를 구성하기 쉽도록 도와주는 툴들을 뜻하며, Puppet, Chef, Ansible, Salt 등이 있음. - [Ten Tools for Configuration Management](https://opensourceforu.com/2015/03/ten-tools-for-configuration-management/)
