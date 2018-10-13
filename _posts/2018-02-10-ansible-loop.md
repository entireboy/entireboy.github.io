---
layout: post
title:  "[Ansible] 반복(loop)"
date:   2018-02-10 22:18:00 +0900
published: true
categories: [ ansible ]
tags: [ ansible, loop, item ]
---

[Ansible](https://www.ansible.com/)에서 배열과 같은 리스트를 주어 주고 반복할 때 `with_items`를 사용하면 편하다. `with_items`에 리스트로 추가한 항목들이 `item`이라는 변수에 하나씩 들어온다. 반복할 항목은 `with_items`에 `key: value` 형태로 배열로 추가하면되고, 각 항목의 값은 `{{ item.key }}`으로 사용하면 된다.

```yaml
- name: 권한과 함께 파일 복사
  templates:
    src: {% raw %}{{ item.src }}{% endraw %}
    dest: {% raw %}{{ item.dest }}{% endraw %}
    mode: {% raw %}{{ item.mode }}{% endraw %}
  with_items:
    - {src: "hello.sh", dest: "scripts/hello.sh", mode: "0755"}
    - {src: "names.txt", dest: "datas/names.txt", mode: "0644"}
```

항목이 하나만 있는 경우 굳이 `key: value`형태로 사용하지 않고 `{{ item }}`으로 사용해도 된다.

```yaml
- name: 시스템 사용자 추가
  user:
    name: "{% raw %}{{ item }}{% endraw %}"
    state: present
    groups: "wheel"
  with_items:
     - testuser1
     - testuser2
```


# 참고

- [Ansible docs Loops](http://docs.ansible.com/ansible/latest/playbooks_loops.html)
