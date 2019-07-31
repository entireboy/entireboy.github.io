---
layout: post
title:  "[GitHub] 여러 GitHub 계정 사용하기"
date:   2019-05-25 22:18:00 +0900
published: true
categories: [ github ]
tags: [ github, multi, account, rsa, ssh, key, version control ]
---

# 여러개의 GitHub 계정

[GitHub](https://github.com/)을 사용하다 보면 프로젝트 마다 다른 계정을 사용해야 하는 경우가 있다. 이럴 때는 계정 마다 SSH key를 만들어서 등록해서 사용하면 된다.

등록이 제대로 되지 않았다면 아래와 같은 `Permission denied (publickey)` 오류를 볼 수 있다.

```bash
$ ssh -T git@github.com
git@github.com: Permission denied (publickey).

$ git push origin master
git@github.com: Permission denied (publickey).
fatal: Could not read from remote repository.

Please make sure you have the correct access rights
and the repository exists.
```

여러 계정을 사용하려면 아래에 나오는 단계를 계정 마다 반복해 주면 된다.


# SSH key 생성

[GitHub Help](https://help.github.com/en/articles/generating-a-new-ssh-key-and-adding-it-to-the-ssh-agent) 페이지에 있는 것처럼 SSH key를 생성한다. 계정 마다 SSH key를 따로 생성한다.

```bash
$ ssh-keygen -t rsa -b 4096 -C "your_email@example.com"
```

`your_email@example.com`는 계정 마다의 메일 주소를 적고, 아래 프롬프트가 나오면 각 계정 마다 알아볼 수 있는 위치로 설정한다. 계정 마다 다른 위치를 지정해 주면 된다.

```bash
Enter a file in which to save the key (/Users/you/.ssh/id_rsa): [Press enter]
```

샘플로 `/Users/you/.ssh/github_me`로 설정하면, 다음과 같이 `github_me`와 `github_me.pub` 파일이 생성된다. `github_me`는 외부에 공개되면 안 되는 private key 이고, `github_me.pub`는 public key로 공개돼도 된다.

```bash
$ ls /Users/you/.ssh/
github_me    github_me.pub    id_rsa    id_rsa.pub
```

`ssh-add` 명령으로 SSH key를 등록해 준다.

```bash
$ ssh-add /Users/you/.ssh/github_me
$ ssh-add -l
어쩌구저쩌구 your_email@example.com (RSA)
```


# GitHub에 SSH key 등록

생성한 SSH key를 GitHub에 등록해야 한다. 해당 계정으로 로그인 하고, [SSH and GPG keys](https://github.com/settings/keys) 설정 화면에서 `New SSH key` 버튼을 클릭해서 추가한다.

{% include image.html file='/assets/img/2019-05-25-github-using-multi-account1.png' alt='New SSH key' %}

조금 전에 생성한 파일 중에 `.pub`으로 끝나는 public key의 내용을 추가하면 된다. `ssh-rsa`나 `ssh-dsa` 등으로 시작하는 부분부터 끝까지 복사하면 된다. 회사, 집 등 여러 머신에서 등록할 수 있으니 구분할 수 있는 이름을 적어주는게 좋다. 나중에 필요 없어지면 제거하기 편하게..

```bash
$ cat /Users/you/.ssh/github_me.pub
ssh-rsa 어쩌구 저쩌구 your_email@example.com
```

{% include image.html file='/assets/img/2019-05-25-github-using-multi-account2.png' alt='Add SSH key' %}

추가가 끝나면 아래처럼 key를 확인할 수 있다.

{% include image.html file='/assets/img/2019-05-25-github-using-multi-account3.png' alt='Added SSH key' %}


# SSH config 추가

여러 SSH key를 사용하기 위해서는 `~/.ssh/config` 파일에 key를 등록해 줘야 한다. `~/.ssh/config` 파일이 없다면 생성하면 된다. `Host`를 `github.com`으로 적어두면 `github.com`에 접속할 때는 `IdentityFile`에 정의된 key를 사용하게 된다.

```bash
$ cat /Users/you/.ssh/config
# Default account
Host github.com
  HostName github.com
  User git
  IdentityFile ~/.ssh/github_primary

# Secondary account
Host github.com github_me설명
  HostName github.com
  User git
  IdentityFile ~/.ssh/github_me
```


# 접속 테스트

새로 추가된 계정의 SSH key가 잘 동작하는지 그 계정만 접근 가능한 프로젝트로 이동하고 다음 명령을 실행한다.

```bash
$ cd /Users/you/projects/my-test
$ ssh -T git@github.com
Hi THERE! You've successfully authenticated, but GitHub does not provide shell access.
```


# 참고

- [여러 개의 github 계정 사용하기](https://aweekj.github.io/using-multiple-accounts-in-git/)
- [Generating a new SSH key and adding it to the ssh-agent - GitHub Help](https://help.github.com/en/articles/generating-a-new-ssh-key-and-adding-it-to-the-ssh-agent)
