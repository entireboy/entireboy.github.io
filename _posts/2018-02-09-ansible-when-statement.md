---
layout: post
title:  "[Ansible] 특정 조건에만 실행하기 (when statement)"
date:   2018-02-09 23:18:00 +0900
published: true
categories: [ ansible ]
tags: [ ansible, condition, when, statement ]
---

[Ansible](https://www.ansible.com/)로 복잡한 로직을 만들기는 어렵지만, 특정 상황에만 실행하는 간단한 로직은 `when`을 사용하면 쉽게 만들 수 있다.


# 변수 설정(defined) 여부

변수가 설정된 경우만 실행하도록 하려면 `when: 변수명 is defined`라고 쓰면 된다. 선언되지 않은 경우는 `is undefined`나 `is not defined`로 사용하면 된다.

```yaml
- name: system_name 변수가 설정되지 않아 실행 안 됨
  debug:
    msg: "System: {% raw %}{{ system_name }}{% endraw %}"
  when: system_name is defined
- name: system_name 변수 설정
  set_fact:
    system_name: "my system"
- name: system_name을 사용할 수 있음
  debug:
    msg: "System: {% raw %}{{ system_name }}{% endraw %}"
  when: system_name is defined
```


# 변수 값 체크

또 많이 사용할 조건이 변수의 값이 특정 값인 경우에만 실행하도록 하는 것이다.

```yaml
- set_fact:
    os_family: Debian
- name: Debian 이나 CentOS인 경우만 실행
  debug:
    msg: "OKOK {% raw %}{{ os_family }}{% endraw %}"
  when: os_family == "Debian" or os_family == "CentOS"
- name: 5 이상인 경우만 출력
  command: echo {% raw %}{{ item }}{% endraw %}
  with_items: [ 0, 2, 4, 6, 8, 10 ]
  when: item > 5
```


# 참고

- [when satement - Ansible docs Conditionals](http://docs.ansible.com/ansible/latest/playbooks_conditionals.html#the-when-statement)
