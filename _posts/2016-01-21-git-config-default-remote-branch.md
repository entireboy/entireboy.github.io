---
layout: post
title:  "[git] default remote/branch 설정"
date:   2016-01-21 01:31:36 +0900
categories: git remote
tags: git 깃 remote repository branch default 브랜치 리모트
---

커맨드에서 git 명령을 쓰다 보면 remote나 branch명을 쓰기가 너무 귀찮다. [zsh](http://ohmyz.sh/)이라도 쓰면 tab키로 자동완성을 사용할 수 있지만, 그마저도 귀찮을 때가 많다. =_=

```bash
$ # origin, master 까지 타이핑하기 구찮아
$ git pull origin master

$ # 이렇게만 하면 안 될까??
$ git pull
```

이럴 때 default로 remote나 branch를 설정해두면 좋다.

처음 checkout할 때 `--track` 옵션을 주거나, '.git/config' 파일을 직접 수정하는 방법이 있다. 또는, `branch --set-upstream-to` 옵션으로 설정해 줘도 된다.

```bash
$ git checkout -b createBatchJob origin/createBatchJob --track
Branch createBatchJob set up to track remote branch createBatchJob from origin.
Switched to a new branch 'createBatchJob'
```

`--track` 옵션을 주고 checkout 받으면 '.git/config' 파일에 아래처럼 항목이 추가돼 있을 것이다. 이미 checkout 받았고, 귀찮으면 애래 내용으로 '.git/config' 파일을 직접 수정해도 된다.

```ini
[branch "createBatchJob"]
    remote = origin
    merge = refs/heads/createBatchJob
```

또는.. `branch --set-upstream-to` 옵션으로 설정해줘도 된다.

```bash
$ git pull
There is no tracking information for the current branch.
Please specify which branch you want to merge with.
See git-pull(1) for details

   git pull <remote> <branch>

If you wish to set tracking information for this branch you can do so with:

   git branch --set-upstream-to=origin/<branch> createBatchJob

$ git branch --set-upstream-to=origin/createBatchJob createBatchJob
Branch createBatchJob set up to track remote branch createBatchJob from origin.

$ git pull
Already up-to-date.
```

이제 remote명과 branch명을 안 적어줘도 pull/push가 가능하다. 구찮음 안뇽~
