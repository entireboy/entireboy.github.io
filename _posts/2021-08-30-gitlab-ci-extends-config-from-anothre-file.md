---
layout: post
title:  "[GitLab CI] 다른 파일에 선언된 설정 사용하기 (공유 설정)"
date:   2021-08-30 22:18:00 +0900
published: true
categories: [ gitlab ]
tags: [ gitlab, ci, extends, config, yaml, anchor, hidden, include ]
---

# Anchor

GitLab CI에서 [YAML anchor](https://docs.gitlab.com/ee/ci/yaml/#anchors) 기능을 사용하면 미리 선언해둔 설정을 여러 곳에서 사용할 수 있다.

(GitLab CI 문서에 있는 설명을 복사해 오면..)

```yaml
.job_template: &job_configuration  # Hidden yaml configuration that defines an anchor named 'job_configuration'
  image: ruby:2.6
  services:
    - postgres
    - redis

test1:
  <<: *job_configuration           # Merge the contents of the 'job_configuration' alias
  script:
    - test1 project

test2:
  <<: *job_configuration           # Merge the contents of the 'job_configuration' alias
  script:
    - test2 project
```

위와 같은 설정은 아래처럼 변환된다. `&`로 anchor의 이름과 설정을 선언하고 `<<`로 그 설정을 가져와서 넣어준다.

```yaml
.job_template:
  image: ruby:2.6
  services:
    - postgres
    - redis

test1:
  image: ruby:2.6
  services:
    - postgres
    - redis
  script:
    - test1 project

test2:
  image: ruby:2.6
  services:
    - postgres
    - redis
  script:
    - test2 project
```


# 문제 - 여러 파일에 나눠진 설정

그런데 GitLab CI 설정이 너무 길어지다보니 복잡해 져서 여러 파일로 나누게 되는 경우가 있다. [include](https://docs.gitlab.com/ee/ci/yaml/#include)를 사용해서 아래와 같은 설정으로 나뉘어 졌다.

```yaml
# .gitlab-ci.yml

stages:
  - init
  - build
  - verification
  - ..

include:
  - .gitlab/ci/init.gitlab-ci.yml
  - .gitlab/ci/build.gitlab-ci.yml
  - .gitlab/ci/verification.gitlab-ci.yml
  - ..
```

이 때 여러 stage/job에서 공용으로 사용하기 위한 설정을 `.gitlab-ci.yml`파일에 선언이 필요할 수 있는데, anchor를 사용하면 참조가 안 돼서 IntelliJ가 화를 낸다.

```yaml
# .gitlab-ci.yml

.use-cache: &use-cache
  cache:
    paths:
      - .gradle/caches
      - .gradle/wrapper

---
# .gitlab/ci/init.gitlab-ci.yml

init:
  stage: init
  <<: *use-cache
  script: ./gradlew clean --stacktrace

---
# .gitlab/ci/build.gitlab-ci.yml

build:
  stage: build
  <<: *use-cache
  needs:
    - init
  script: ./gradlew classes testClasses --stacktrace
  artifacts:
    when: always
    paths:
      - ./**/build/**/*
```

위와 같이 설정을 하면 모르는 참조라고 IntelliJ가 아래처럼 화를 낸다.

{% include image.html file='/assets/img/2021/2021-08-30-gitlab-ci-extends-config-from-another-file1.png' alt='Cannot resolve alias' %}

물론 GitLab CI도 화를 낸다.

{% include image.html file='/assets/img/2021/2021-08-30-gitlab-ci-extends-config-from-another-file2.png' alt='YAML syntax error' %}


# extends

이런 경우, hidden job을 생성해 두고 [extends](https://docs.gitlab.com/ee/ci/yaml/#extends)를 사용하면 IntelliJ의 불만 없이 설정을 복사해 올 수 있다.

```yaml
# .gitlab-ci.yml
.use-cache:
  cache:
    paths:
      - .gradle/caches
      - .gradle/wrapper

---
# .gitlab/ci/init.gitlab-ci.yml

init:
  stage: init
  extends: .use-cache
  script: ./gradlew clean --stacktrace

---
# .gitlab/ci/build.gitlab-ci.yml

build:
  stage: build
  extends: .use-cache
  needs:
    - init
  script: ./gradlew classes testClasses --stacktrace
  artifacts:
    when: always
    paths:
      - ./**/build/**/*
```

(역시 GitLab CI 문서에 있는 샘플을 보면..)

```yaml
.tests:
  script: rake test
  stage: test
  only:
    refs:
      - branches

rspec:
  extends: .tests
  script: rake rspec
  only:
    variables:
      - $RSPEC
```

위의 설정은 아래처럼 바뀐다.
- key를 기준으로 합쳐진다.
  - `stage` key 추가
  - `only:refs` key 추가
- 이미 존재하는 key의 value를 바꾸지 않는다.
  - `script` key는 이미 존재하기 때문에 안 바뀜

```yaml
rspec:
  script: rake rspec
  stage: test
  only:
    refs:
      - branches
    variables:
      - $RSPEC
```


# 참고

- [YAML anchor](https://docs.gitlab.com/ee/ci/yaml/#anchors)
- [include](https://docs.gitlab.com/ee/ci/yaml/#include)
- [extends](https://docs.gitlab.com/ee/ci/yaml/#extends)
