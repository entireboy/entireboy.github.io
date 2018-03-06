---
layout: post
title:  "[Ansible] 실행결과(error) 무시하기"
date:   2018-03-03 22:18:00 +0900
published: true
categories: [ ansible ]
tags: [ ansible, ignore, error, result, playbook ]
---

[Ansible](https://www.ansible.com/) 스크립트에서 실행한 결과가 성공인지 실패인지 알고 싶지 않고 실행만 시키고 싶은 경우에 `ignore_errors: yes`를 쓰면 된다. (`yes` 대신 `true`/`false`도 가능)

예를 들어, 중지하고 싶은 서비스가 있는데 일일이 실행 중인지 체크하지 않고 싶거나, 이미 중지되어 있는 상태에서 중지 명령을 또 보내도 큰 문제 없는 경우 사용할 수 있다.

```yaml
- name: Stop Tomcat
  shell: /home/leocat/scripts/tomcat.sh stop
  ignore_errors: yes
```


# 참고

- [Error Handling In Playbooks](http://docs.ansible.com/ansible/latest/playbooks_error_handling.html)
