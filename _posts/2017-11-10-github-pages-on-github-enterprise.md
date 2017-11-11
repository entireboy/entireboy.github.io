---
layout: post
title:  "[GitHub] GitHub Enterprise에서 GitHub Pages 사용"
date:   2017-11-10 21:18:00 +0900
published: true
categories: [ github ]
tags: [ github, pages, enterprise, publish, static, site, page, blog ]
---

# GutHub Pages

[GitHub Pages](https://pages.github.com/)를 사용하면 커밋하고 github에 푸시하는 것만으로 블로그와 같은 페이지를 만들 수 있다. 프로젝트 메인에 있는 README.md를 보여주는 것과 비슷한 방식이라고 생각하면 쉽다. [Jekyll](https://jekyllrb.com/)도 함께 사용하면 간단한(?) 블로그도 만들 수 있다. 이 페이지([Using Jekyll as a static site generator with GitHub Pages](https://help.github.com/articles/using-jekyll-as-a-static-site-generator-with-github-pages/))를 따라하면 Jekyll을 이용해서 GitHub Pages에 블로그 만드는 방법이 잘 나와 있다. (이 블로그도 그렇게 만든 블로그)


# GitHub Pages on GitHub Enterprise

[GitHub](https://github.com/)에서는 `[username].github.io`로 repository를 만들면, 자동으로 `https://[username].github.io` 주소로 매핑이 돼서 보여진다. 그런데 GitHub Enterprise 버전에서는??

요기조기 문서를 보고 이런저런 테스트를 해봤더니, [username].[orgname]을 주면 된다. [orgname]은 보통 GitHub 주소이다. GitHub Enterprise 주소가 `github.leocat.kr`이고 내 계정이 `deng`라면, `deng.github.leocat.kr`로 repository를 생성하면 된다. 그러면, `https://github.leocat.kr/pages/deng`으로 페이지가 생성된다

> - Repository: [username].[orgname]
> - URL: https://[orgname]/pages/[username]

> **주의!!**
> index.html 같은 파일을 꼭 올려줘야 페이지가 보인다.


# Multi GitHub Pages

repository를 이미 다른 이름으로 생성했다고 울지 말자. 방법은 다 있다. 그리고 이미 하나의 Pages로 만들었는데 또 만들고 싶은 경우도 있다. (URL은 달라지겠지만..)

{% include image.html file='/assets/img/2017-11-10-github-pages-on-github-enterprise1.png' alt='Project Settings' %}

repository 설정(settings)에 가보면, 아래쪽에 `GitHub Pages` 설정 영역이 있다.

{% include image.html file='/assets/img/2017-11-10-github-pages-on-github-enterprise2.png' alt='GitHub Page setting' %}

`GitHub Pages` > `Source` 에서 노출할 브랜치나 폴더를 선택하면 된다. `master branch`를 선택하면 전체 repository가 GitHub Pages로 사용되고, `master branch /docs folder`를 선택하면 `/docs` 폴더가 GitHub Pages로 사용된다. 이미 프로젝트를 만들었고, 그 프로젝트의 웰컴 페이지나 블로그를 만들고 싶다면 `/docs`를 활용해도 괜찮을 것 같다. 둘 다 `master` 브랜치에 푸시해야 한다.

`Source`를 선택하고 오른쪽에 있는 `Save` 버튼을 클릭하면, 위쪽에 `Your site is ready to be published at`이라는 메시지와 함께 접근할 수 있는 URL이 표시된다. 마지막에 [repository] 이름이 더 붙는 차이점이 있다.

> https://[orgname]/pages/[username]/[repository]


# 결론

결론?? :)

개인 블로그라면 repository를 `[username].[orgname]`로 만들고, 프로젝트의 블로그라면 각 프로젝트의 `/docs` 경로를 활용하면 좋을 것 같다.


# 참고

- [Configuring a publishing source for GitHub Pages - GitHub Help](https://help.github.com/enterprise/2.10/user/articles/configuring-a-publishing-source-for-github-pages/)
- [Using Jekyll as a static site generator with GitHub Pages - GitHub Help](https://help.github.com/articles/using-jekyll-as-a-static-site-generator-with-github-pages/)
- [GitHub Pages](https://pages.github.com/)
