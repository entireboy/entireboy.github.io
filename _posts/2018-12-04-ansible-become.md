---
layout: post
title:  "[Ansible] sudo 대신 become 사용하기"
date:   2018-12-04 22:18:00 +0900
published: true
categories: [ ansible ]
tags: [ ansible, become, sudo, privilege, switch, user, change, su ]
---

(정확한 버전이 잘 기억나지 않지만) Ansible 2.4 버전 부터인가 `sudo` 대신 `become`을 사용하라고 아래와 같은 워닝이 나온다.

```bash
[DEPRECATION WARNING]: Instead of sudo/sudo_user, use become/become_user and make sure become_method is 'sudo' (default). This feature will be removed in version 2.9. Deprecation warnings can be disabled by setting
deprecation_warnings=False in ansible.cfg.
```

`sudo`는 2.9 버전 부터 없어진다고 강력하게 나오니, 메시지에 나온 것 처럼 `become`을 사용하자.

```yaml
# wow 설치 playbook
- name: Install wow
  hosts: api_servers
  become: no
  become_user: nobody
  roles:
  - wow_0_1_27
  - other_tasks
```

`become`은 특정 사용자로 전환(become)할지 여부이고, true/false/yes/no로 설정한다. 그리고 어떤 사용자로 전환될지는 `become_user`로 설정하면 된다. 위와 같이 wow를 설치할 때 필요하면 `nobody`로 전환하도록 `become_user`를 설정해 두고, 아래 처럼 필요한 task에서만 `become`으로 설정을 on/off 할 수 있다. `become_user` 역시 `become`과 마찬가지로 세부 task에서만 설정할 수도 있다.

```yaml
# wow 설치 role
- name: Create wow directory
  file:
    path: /path_to_wow
    state: directory
- name: Unarchive files
  unarchive:
    src: "/where_wow_archive/wow-{{ wow_version }}.tar.gz"
    dest: /path_to_wow
    copy: no
  become: yes
```

특정 사용자가 되는 명령은 몇 가지가 있기 때문에, `become_method`로 그 방법을 선택할 수 있다. `become_method`는 `sudo | su | pbrun | pfexec | doas | dzdo | ksu` 중 하나로 선택할 수 있다. 그 명령을 실행할 때 함께 줄 옵션을 `become_flags`로 설정할 수 있다.

`su`명령으로 사용자를 변경해서 command를 실행하는 샘플이다.

```yaml
- name: Run a command as nobody
  command: somecommand
  become: yes
  become_method: su
  become_user: nobody
  become_flags: '-s /bin/sh'
```


# 참고

- [Understanding Privilege Escalation - Ansible doc](https://docs.ansible.com/ansible/2.6/user_guide/become.html)
- [Become (Privilege Escalation) - Ansible doc](https://docs.ansible.com/ansible/2.4/become.html)
