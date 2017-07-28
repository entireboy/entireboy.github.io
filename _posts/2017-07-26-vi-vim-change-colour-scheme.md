---
layout: post
title:  "[vi/vim] Color Scheme(색깔) 바꾸기"
date:   2017-07-26 21:18:00 +0900
published: true
categories: [ vi ]
tags: [ vi, vim, scheme, color, colour ]
---

> 이전 블로그에서 옮겨온 포스트 (일부 동작 안 하는 샘플 수정)

vi에서 사용하는 syntax의 색깔을 바꿔보자. 배경색도..
방법은 아주 간단하게 샘플을 보고 vim 파일을 받아서 넣고 vi설정파일에 설정해주면 된다. 참 쉽죠잉~?!?!


구글링해보면 샘플을 금방 찾을 수 있다. 좋은 샘플 사이트 몇 개..

- [Vim Color Scheme Test](https://code.google.com/archive/p/vimcolorschemetest/)
- [10 Vim Color Schemes You Need to Own](http://www.vimninjas.com/2012/08/26/10-vim-color-schemes-you-need-to-own/)

여기서는 [Vim Color Scheme Test](https://code.google.com/archive/p/vimcolorschemetest/)에서 color scheme을 찾아 보겠다. 왜냐?? 샘플이 많아서 ㅋ [Vim Color Scheme Test](https://code.google.com/archive/p/vimcolorschemetest/)에 접속하면 아래 `Browse By File Type` 항목에 각 언어별로 샘플이 있다. 언어는 단지 샘플을 보여주는 것 뿐이니 익숙한걸로 아무거나 선택한다. 여기서 중요한건 color scheme이다.

샘플 중 `molokai`를 선택했다. 샘플 위에 scheme 이름을 클릭하면 vim 파일을 받을 수 있다. 파일을 받아서 vim 설치 경로의 `colors` 폴더에 넣어준다. 일반적인 경우라면 `/usr/share/vim/vim[버전]`에 폴더가 있다. 내 경우는 vim 7.3버전이라 `/usr/share/vim/vim73`에 vim이 설치되어 있다. 이미 colors 폴더에는 여러 scheme이 들어 있다.

```bash
$ #vim 설치 경로가 대부분 root라 root 권한이 필요할 것이다.
$ cd /usr/share/vim/vim73/colors/
$ ls
blue.vim      delek.vim    evening.vim  murphy.vim     README.txt  slate.vim
darkblue.vim  desert.vim   koehler.vim  pablo.vim      ron.vim     torte.vim
default.vim   elflord.vim  morning.vim  peachpuff.vim  shine.vim   zellner.vim

$ curl https://raw.github.com/tomasr/molokai/master/colors/molokai.vim > molokai.vim
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100  7917  100  7917    0     0   5860      0  0:00:01  0:00:01 --:--:--  8281

$ #또는 sudo 사용
$ sudo wget -O /usr/share/vim/vim73/colors/molokai.vim https://raw.github.com/tomasr/molokai/master/colors/molokai.vim
```

{% include google-ad-content %}

Color Scheme을 다운받았으니 어떤 scheme을 사용할지 설정하면 된다. `/etc/vimrc` 또는 `~/.vimrc` 등의 파일을 열어서 아래의 내용을 추가해주자. `/etc/vimrc` 파일을 수정하면 모든 사용자의 설정이 변경되고, `~/.vimrc` 파일을 수정하면 현재 로그인한 사용자의 설정만 변경된다. 파일이 존재하지 않는다면 생성해도 상관 없다. `molokai`는 여기서 다운받은 scheme의 이름이고, 설정하고 싶은 scheme이름을 써주면 된다.

```bash
:colorscheme molokai
```

짜잔~!! 끄트!!!

- Before
  ![Before colour scheme](/assets/img/2017-07-26-vi-vim-change-colour-scheme-before.png)


- After (molokai)
  ![After colour scheme](/assets/img/2017-07-26-vi-vim-change-colour-scheme-after.png)


# 참고

- [Vim Color Scheme Test](https://code.google.com/archive/p/vimcolorschemetest/)
- [10 Vim Color Schemes You Need to Own](http://www.vimninjas.com/2012/08/26/10-vim-color-schemes-you-need-to-own/)
