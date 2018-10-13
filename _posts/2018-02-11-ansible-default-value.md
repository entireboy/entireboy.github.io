---
layout: post
title:  "[Ansible] 기본값(default value)"
date:   2018-02-11 22:18:00 +0900
published: true
categories: [ ansible ]
tags: [ ansible, template, default, undefined, value, filter, omit ]
---

[Ansible](https://www.ansible.com/)에서 변수를 사용할 때, 변수가 선언되지 않은 경우 에러를 뱉는다. 이런 경우에 변수가 선언되지 않으면 default value를 사용하도록 할 수 있다. 변수 template 안에 `|defualt(value)`로 써주면 된다.

```yaml
- debug:
    msg: "my value: {% raw %}{{ my_val | default(5) }}{% endraw %}"
  # "msg": "my value: 5
- set_fact:
    my_val: 7
- debug:
    msg: "my value: {% raw %}{{ my_val | default(5) }}{% endraw %}"
  # "msg": "my value: 7
```

변수가 선언되지 않은 경우 module의 parameter 자체를 무시하는 `omit`이라는 특수값(?)도 있다. `default(omit)`으로 사용하면 된다.

```yaml
- name: touch files with an optional mode
  file: dest={% raw %}{{ item.path }} state=touch mode={{ item.mode|default(omit) }}{% endraw %}
  with_items:
    - path: /home/leocat/foo
    - path: /home/leocat/bar
    - path: /home/leocat/baz
      mode: "0444"
```

`file` module을 사용해서 파일을 생성할 때 `mode`를 설정하지 않은 경우 시스템에 설정된 default mode로 생성하고 싶을 때 위와 같이 사용할 수 있다. `with_items`에 `mode`를 설정하지 않은 `foo`와 `bar` 파일은 `mode` parameter가 없는 것처럼 `file` module이 실행된다.


# 참고

- [default undefined value - Ansible docs Filters](http://docs.ansible.com/ansible/latest/playbooks_filters.html#defaulting-undefined-variables)
- [omitting parameters - Ansible docs Filters](http://docs.ansible.com/ansible/latest/playbooks_filters.html#omitting-parameters)
