---
layout: post
title:  "[GitHub] (공용 서버 등에서) 로그인 없이 GitHub 사용하기"
date:   2017-11-27 23:18:00 +0900
published: true
categories: [ github ]
tags: [ github, enterprise, ssh, deploy, key ]
---
```
본 내용은 GitHub Enterprise 에서도 가능한 방법이다.
```

간혹 배포서버 등에서 GitHub에 접속해서 commit을 한다거나 pull 받아와야 하는 경우가 있다. 하지만 배포서버 같은 공용 서버는 말 그대로 공용이기 때문에 특정 사용자로 로그인을 하기 난감한 경우가 있다.

이런 경우 repository에 서버 자체를 인증해 두고, ssh를 통해 로그인을 하지 않고 접근할 수 있는 방법이 있다.


# RSA key 생성

공용 서버에서 `ssh-keygen` 명령을 통해 GitHub에 등록할 rsa 키를 생성한다. 이미 키파일(`${HOME}/.ssh/id_rsa`, `${HOME}/.ssh/id_rsa.pub`)이 존재하는 경우 이 단계를 패스하면 된다. (지원되는 암호화 방식은 [여기](https://help.github.com/articles/checking-for-existing-ssh-keys/)서 체크하자.)

```bash
$ ssh-keygen -t rsa
Generating public/private rsa key pair.
Enter file in which to save the key (/my/home/.ssh/id_rsa):
Enter passphrase (empty for no passphrase):
Enter same passphrase again:
Your identification has been saved in /my/home/.ssh/id_rsa.
Your public key has been saved in /my/home/.ssh/id_rsa.pub.
The key fingerprint is:
.. 핑거 프린트 어쩌구 저쩌구 ..
The key's randomart image is:
+---[RSA 2048]----+
.. 어쩌구 저쩌구 ..
+----[SHA256]-----+
$ cat /my/home/.ssh/id_rsa.pub
ssh-rsa ABCD어쩌구저쩌구 me@mememe
```

처음에 생성할 파일 경로명을 물어보는데, 그냥 엔터를 입력하면 default 경로(`${HOME}/.ssh/id_rsa`)에 생성된다. 파일 경로를 따로 입력해서 rsa 키 파일을 여러벌 만들어서 사이트 마다 다르게 등록해서 사용할 수도 있다.

키를 사용할 때 마다 입력할 비밀번호를 물어보는데, 그냥 엔터를 입력하면 암호 없이 사용하는 파일이 생성된다.

여기서는 defualt 경로에 암호 없이 생성하는 예제로 진행하겠다.


# GitHub에 Deploy Key 등록

GitHub repository에서 `Settings` > `Deploy Keys`를 접속하면 현재 등록되어 있는 key들을 볼 수 있다. 오른쪽에 있는 `Add deploy key` 버튼을 누르면 아래와 같이 key를 등록할 수 있는 화면이 나온다.

[[ 사진 ]]

{% include image.html file='/assets/img/2017-11-27-github-deploy-key.png' alt='Register GitHub Deploy Keys' %}

`Title`은 구분할 수 있는 이름을 주면 되고, `Key`에는 위에서 생성한 RSA 키의 public key를 적어주면 된다. 다음 명령으로 확인할 수 있다.

```bash
$ cat ~/.ssh/id_rsa.pub
ssh-rsa ABCD어쩌구저쩌구 me@mememe
```


# 접속 확인

key를 등록한 뒤 아래 명령으로 정상적으로 접속 되는지 확인하자. GitHub Enterprise에서는 `github.com` 대신 호스트 이름을 적어주면 된다.

```bash
$ ssh -T git@github.com
$ ssh -T git@`ENTERPRISE_HOST`
```

한번도 접속한 적이 없으면 아래처럼 접속해도 될지 물어보는 내용이 나오고, `yes`라고 타이핑을 하면 접속을 시도한다.

```bash
The authenticity of host 'github.com (10.10.10.10)' can't be established.
RSA key fingerprint is 46:어쩌구:저쩌구:저쩔씨구:28.
Are you sure you want to continue connecting (yes/no)?
```

그러면 아래와 같이 사용자 이름 또는 repository 이름이 응답으로 오면 성공이다.

```bash
Hi `username 또는 repository`! You've successfully authenticated, but GitHub does not
provide shell access.
```

만일 실패했다면 [help 페이지](https://help.github.com/articles/error-permission-denied-publickey/)에서 찾아보자.


# 프로젝트 clone 받기

**여기서 실수하기 좋은 내용은, ssh 인증을 사용하는 방법이기 때문에 clone을 받을 때 `http 인증`이 아닌 `ssh 인증`으로 받아야 한다는 점이다.** (한참 삽질했어 T_T)

```
잘돼(O): git@github.com:me/my-project.git
안돼(X): https://github.com/me/my-project.git
```


# git 인증 시간 늘리기 꼼수

이건.. 정말 꼼수로.. `ssh 인증을 못 쓰는 상황`에서 쓰는 방법이 있다. GitHub 이 아닌 [GitLab](https://gitlab.com/)을 설치해서 사용할 때 ssh 인증 기능을 켜두지 않는다거나 할 때 쓸 수 있다.

git 인증 세션 시간을 길게 늘려주고 아무나 로그인을 해둔다. 한번 로그인을 해두면 인증이 만료되기 전까지는 비밀번호 없이 그 인증을 계속 쓸 수 있다. 아래 명령으로 로그인 인증 만료 시간을 늘려준다. timeout 단위는 초이다. ([git-credential-cache](https://git-scm.com/docs/git-credential-cache))

```bash
git config --global credential.helper 'cache --timeout=3600'
```

인증 만료 전에 꾸준히 git 명령을 날려주면 세션이 계속 연장(?)되기 때문에, jenkins 등으로 timer를 걸고 사용해 두면 좋다.

하지만, 어디까지나 꼼수는 꼼수일뿐..


# 참고

- [Connecting to GitHub with SSH - GitHub Help](https://help.github.com/articles/connecting-to-github-with-ssh/)
- [Connecting to GitHub with SSH - GitHub Enterprise Help](https://help.github.com/enterprise/2.11/user/articles/connecting-to-github-with-ssh/)
- [git-credential-cache - git Documentation](https://git-scm.com/docs/git-credential-cache)
- [GitLab](https://gitlab.com/)
