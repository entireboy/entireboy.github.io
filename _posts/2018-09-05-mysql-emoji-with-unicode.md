---
layout: post
title:  "[MySQL] MySQL에서 Unicode 저장 시 이모지(emoji) 문제"
date:   2018-09-05 23:18:00 +0900
published: true
categories: [ java ]
tags: [ mysql, encoding, encode, unicode, emoji, charset, character set, character, char, utf, utf8, utf8mb3, utf8mb4 ]
---

MySQL에서 유니코드로 이모지를 저장하려면 `utf8`([alias for `utf8mb3`](https://dev.mysql.com/doc/refman/5.5/en/charset-unicode-utf8.html))이 아닌 `utf8mb4` 캐릭터셋을 사용해야 한다.

- The `utf8mb4` Character Set (4-Byte UTF-8 Unicode Encoding)
  - BMP character 지원
  - supplementary characters **지원**
  - 문자당 최대 4 byte 까지 저장공간이 필요할 수 있음
- The `utf8mb3` Character Set (3-Byte UTF-8 Unicode Encoding)
  - BMP character 지원
  - supplementary characters **미지원**
  - 문자당 최대 3 byte 까지 저장공간이 필요할 수 있음
  - `utf8mb3` 대신 `utf8`로 사용할 수 있음 (alias for)

`BMP(Basic Multilingual Plane)`는 일반적으로 많이 사용되는 언어들의 기본 문자판(basic plate)을 뜻한다. [한글 자모](<https://en.wikipedia.org/wiki/Hangul_Syllables>)나 CJK 같은 한자, [Latin-1 supplement](https://en.wikipedia.org/wiki/Latin-1_Supplement_(Unicode_block))(e.g. à, á, â, ã 등) 같은 문자들을 포함한다.

이 기본 문자 외에 자주 사용하지는 않지만 추가로 필요한 문자는 `supplementary characters`로 따로 정리되어 있으며, [이모지](https://en.wikipedia.org/wiki/Emoji)는 여기에 포함되어 있다.

그래서.. 이모지를 저장하려면 `utf8mb4` charset을 사용하면 된다.


# 참고

- [The utf8mb4 Character Set (4-Byte UTF-8 Unicode Encoding) - MySQL Document](https://dev.mysql.com/doc/refman/5.5/en/charset-unicode-utf8mb4.html)
- [The utf8mb3 Character Set (3-Byte UTF-8 Unicode Encoding) - MySQL Document](https://dev.mysql.com/doc/refman/5.5/en/charset-unicode-utf8mb3.html)
- [The utf8 Character Set (Alias for utf8mb3)](https://dev.mysql.com/doc/refman/5.5/en/charset-unicode-utf8.html)
- [Why are we using utf8mb4_general_ci and not utf8mb4_unicode_ci?](https://drupal.stackexchange.com/questions/166405/why-are-we-using-utf8mb4-general-ci-and-not-utf8mb4-unicode-ci)
- [What's the difference between utf8_general_ci and utf8_unicode_ci](https://stackoverflow.com/questions/766809/whats-the-difference-between-utf8-general-ci-and-utf8-unicode-ci)
- [Plate (Unicode) - Wikipedia](https://en.wikipedia.org/wiki/Plane_(Unicode))
- [Emoji - Wikipedia](https://en.wikipedia.org/wiki/Emoji)
- [Emoticons (Unicode block) - Wikipedia](https://en.wikipedia.org/wiki/Emoticons_(Unicode_block))
