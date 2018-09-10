---
layout: post
title:  "[Ansible] sudo/become과 함께 local_action 실행 시 문제"
date:   2018-09-10 22:18:00 +0900
published: true
categories: [ ansible ]
tags: [ ansible, local_action, sudo, become, su, switch user, user, script ]
---

# 현상

[Ansible](https://www.ansible.com/) 스크립트를 사용할 때 스크립트를 실행하고 있는 로컬(`127.0.0.1`)에서 명령을 실행하고 싶을 때 [local_action](https://docs.ansible.com/ansible/2.6/user_guide/playbooks_delegation.html#delegation)을 사용할 수 있다.

그런데, `local_action`과 함께 `sudo`나 `become`을 함께 사용하면 아래와 같이 `sudo: a password is required`라는 오류와 함께 실행되지 않는다.

```bash
$ ansible-playbook -i inventories/dev test-local-action.yaml

PLAY [webserver] ***********************************************************************************

TASK [Gathering Facts] *****************************************************************************
ok: [test.leocat.kr]

TASK [check file] **********************************************************************************
fatal: [test.leocat.kr -> localhost]: FAILED! => {"changed": false, "failed": true, "module_stderr": "sudo: a password is required\n", "module_stdout": "", "msg": "MODULE FAILURE", "rc": 1}
to retry, use: --limit @/Users/leocat/sample/ansible/test-local-action.retry

PLAY RECAP *****************************************************************************************
test.leocat.kr : ok=1 changed=0 unreachable=0 failed=1
```

샘플 코드는 아래와 같다. (전체 샘플은 [여기](https://github.com/entireboy/blog-sample/tree/master/ansible)에 별도로 저장해 두었고, `local_action with sudo` 내용 참고)

```yaml
- hosts: webserver
  remote_user: deploy
  become: yes
  # sudo: yes
  tasks:
  - name: check file
    local_action: stat path={{ MY_FILE_PATH }}
    register: myfile_for_copy
  - name: copy files if exist
    copy:
      src: "{{ MY_FILE_PATH }}"
      dest: "{{ SOMEWHERE_TO_COPY }}"
    when: myfile_for_copy.stat.exists == true
```

에러가 발생하는 playbook이 거대해서 `sudo` 때문인지 찾는데 한참 걸렸는데, 이런저런 테스트를 하다 보니 `sudo`와 함께 쓰는 경우에 발생하는 문제였다.


# 원인

(한참의 삽질 끝에) `sudo`나 `become`과 함께 `local_action`을 사용하면 ansible 스크립트를 실행하는 로컬에 이런 파일이 생기는 것을 발견했다. `sudo` 없이 실행하면 이런 스크립트가 생성되지 않고, 에러도 없이 정상 동작한다.

```bash
$ ls -al ~/.ansible/tmp
total 0
drwx------  13 leocat  staff  416  9 10 13:32 .
drwx------   4 leocat  staff  128  8  4  2017 ..

$ ansible-playbook -i inventories/dev test-local-action.yaml
# .. 실패 메시지 어쩌구 저쩌구 ..

$ ls -al ~/.ansible/tmp
total 0
drwx------  13 leocat  staff  416  9 10 13:32 .
drwx------   4 leocat  staff  128  8  4  2017 ..
drwx------   3 leocat  staff   96  9 10 13:32 ansible-tmp-1536553955.88-2316783747169
$ ll .ansible/tmp/ansible-tmp-1536553955.88-2316783747169
total 120
drwx------   3 leocat  staff     96  9 10 13:32 .
drwx------  13 leocat  staff    416  9 10 13:32 ..
-rwxr--r--   1 leocat  staff  60608  9 10 13:32 stat.py
```

`sudo`나 `become`으로 다른 사용자가 되어서 실행하는 경우 이런 스크립트를 생성하고, 실행하는 것이다. 때문에 `sudo: a password is required` 같이 다른 사용자로 전환하지 못 해서 오류 메시지가 나오는 것이다.


# 해결 방법 #1

다른 사용자가 되어 이 스크립트를 실행할 수 있게 `ansible` 명령을 실행하는 터미널에서 임시로 `sudo` 명령을 실행해 주면 된다. 이 방법은 ansible 스크립트를 실행할 터미널에만 임시로 5분 정도 `sudo` 권한을 부여 받는 방법이라, 시간이 지나거나 다른 터미널에서는 실행되지 않을 수 있다.

`ansible --version` 같이 스크립트가 없어서 실행 가능한 명령을 `sudo`와 함께 실행해서, 잠시 `sudo`권한을 받고 `ansible` 명령을 실행한다.

```bash
$ sudo ansible --version
Password:
ansible 2.3.1.0
config file =
configured module search path = Default w/o overrides
python version = 2.7.13 (default, Jul 18 2017, 09:17:00) [GCC 4.2.1 Compatible Apple LLVM 8.1.0 (clang-802.0.42)]
$ ansible-playbook -i inventories/dev test-local-action.yaml
PLAY [webserver] ***********************************************************************************

TASK [Gathering Facts] *****************************************************************************
ok: [test.leocat.kr]

TASK [check file] **********************************************************************************
ok: [test.leocat.kr -> localhost]

PLAY RECAP *****************************************************************************************
test.leocat.kr : ok=2 changed=0 unreachable=0 failed=0
```


# 해결 방법 #2

(가능하다면) `playbook`이나 `role`을 `sudo`/`become` 없이 실행한다. (`sudo: no`, `become: no`)

```yaml
- hosts: webserver
  remote_user: deploy
  become: no
  # sudo: no
  tasks:
  - name: check file
    local_action: stat path={{ MY_FILE_PATH }}
    register: myfile_for_copy
```


# 해결 방법 #3

`playbook`이나 `role`을 `sudo`/`become` 없이 실행하는 것이 어렵다면, `local_action` task 만이라도 `sudo`/`become` 없이 실행한다. (`sudo: no`, `become: no`)

```yaml
- hosts: webserver
  remote_user: deploy
  become: yes
  # sudo: yes
  tasks:
  - name: check file
    local_action: stat path={{ MY_FILE_PATH }}
    register: myfile_for_copy
    sudo: no
```


# 참고

- [Delegation, Rolling Updates, and Local Actions - Ansible doc](https://docs.ansible.com/ansible/2.6/user_guide/playbooks_delegation.html#delegation)
- [Ansible all modules](https://docs.ansible.com/ansible/latest/modules/list_of_all_modules.html)
