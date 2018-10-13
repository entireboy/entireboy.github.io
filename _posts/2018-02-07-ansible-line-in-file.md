---
layout: post
title:  "[Ansible] 파일에 한 줄 넣기"
date:   2018-02-07 22:18:00 +0900
published: true
categories: [ ansible ]
tags: [ ansible, line, text, file, insert ]
---

# 파일에 한 줄 넣기

[Ansible](https://www.ansible.com/)을 사용해서 서버 설정(provisioning)을 할 때 일반적으로 파일을 복사(`copy`, `file` 등 module)하거나 템플릿(`template` module)처럼 이용하는 경우가 많다. 하지만 간혹 파일에 한 줄을 넣고 싶을 때가 있다. 예를 들어, 터미널로 로그인 할 때 마다 특정 스크립트를 실행시키고 싶을 때, `.bash_profile`에 스크립트를 한 줄 넣어주면 된다. (여기서는 `/home/leocat/scripts/helloThere.sh`)

```bash
$ cat /home/leocat/.bash_profile
  .. 이거저거 ..
export PATH=어쩌구저쩌구

/home/leocat/scripts/helloThere.sh
$
```

`.bash_profile` 같은 경우는 기존에 존재하는 파일이기 때문에, redirect(`>`, `>>`) 등으로 파일에 추가하는 형태로 ansible 스크립트를 만들어야 한다. 하지만 이렇게 파일에 한 줄 추가하는 ansible 스크립트를 만들게 되면, ansible 스크립트를 여러번 실행시킬 때 마다 파일에 계속 라인이 추가된다.


# lineinfile Module

이미 존재하는 파일에 내가 추가할 라인이 있는지 체크하고, 없는 경우에만 추가하고 싶은 경우에 `lineinfile` module을 사용하면 딱 좋다. - [lineinfile module](http://docs.ansible.com/ansible/latest/lineinfile_module.html)

```yaml
# .bash_profile 파일에 helloThere.sh 스크립트 라인이 없는 경우 파일 끝(EOF)에 추가한다.
- lineinfile:
    path: '/home/leocat/.bash_profile'
    line: '/home/leocat/scripts/helloThere.sh'
    insertAfter: EOF

# .bash_profile 파일에 helloThere.sh 스크립트 라인이 있으면 삭제한다.
- lineinfile:
    path: '/home/leocat/.bash_profile'
    line: '/home/leocat/scripts/helloThere.sh'
    state: absent

- lineinfile:
    path: /etc/httpd/conf/httpd.conf
    regexp: '^Listen '
    insertafter: '^#Listen '
    line: 'Listen 8080'
```


# 참고

- [Ansible lineinfile module](http://docs.ansible.com/ansible/latest/lineinfile_module.html)
