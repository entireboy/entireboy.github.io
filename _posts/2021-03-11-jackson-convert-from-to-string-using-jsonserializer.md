---
layout: post
title:  "[Jackson] JsonNode를 문자열로 전환하기 (serialization / deserialization)"
date:   2021-03-11 22:18:00 +0900
published: true
categories: [ jackson ]
tags: [ jackson, json, serialization, deserialization, serializer, deserializer, string, jsonnode, jsonobject ]
---

간혹 JsonNode를 객체로 serialization/deserialization을 하지 않고, 단순하게 문자열(string)로 전환할 필요가 있을 때가 있다. 응답으로 받은 API response의 일부를 단순히 하는 경우가 있을 수 있다.

```json
{
  "orderId": 1,
  "orderItems": [
    {
      "orderItemId": 11,
      "count": 2
    },
    {
      "orderItemId": 21,
      "count": 1
    }
  ],
  "address": {
    "city": "서울",
    "road": "관악로 1",
    "detail": "903동"
  }
}
```

위와 같은 형태의 JsonNode를 아래 DTO로 받는 예를 들 수 있다. 주소 데이터(address)는 당장은 사용하지 않고 다음을 위해 json 형태로 저장하고, 주문 데이터(orderItems)만 사용하는 경우이다.

```java
public class OrderDto {
    private Long orderId;
    private List<OrderItemDto> orderItems;
    private String address;
}
```

`address`에는 `{"city":"서울", "road":"관악로 1", "detail":"903동"}`로 저장되기를 원한다.


# Serializer / Deserializer 생성

Json을 object가 아닌 문자열로 변경할 때 사용할 Serializer와 Deserializer를 만들어 준다.

```java
package kr.leocat.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;


/**
 * JSON -> JsonObject
 */
public class StringJSONTypeSerializer extends JsonSerializer<String> {

    @Override
    public void serialize(String value,
                          JsonGenerator gen,
                          SerializerProvider serializers) throws IOException {

        if (value == null) {
            return;
        }

        gen.writeRawValue(value);
    }
}
```

```java
package kr.leocat.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.Optional;

/**
 * JsonObject -> JSON
 */
public class StringJSONTypeDeserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser p,
                              DeserializationContext ctxt) throws IOException {
        return Optional.ofNullable(p.getCodec().<JsonNode>readTree(p))
            .map(JsonNode::toString)
            .orElse(null);
    }
}
```

아래는 kotlin 버전이다. JsonNode의 value가 null이거나 key가 없는 경우는 이 Serializer/Deserializer를 호출하지 않아서 nullable로 만들 필요는 없었다.

```kotlin
package kr.leocat.json

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.io.IOException
import kotlin.jvm.Throws

/**
 * JSON -> JsonObject
 */
class StringJSONTypeSerializer : JsonSerializer<String>() {

    @Throws(IOException::class)
    override fun serialize(
        value: String,
        gen: JsonGenerator,
        serializers: SerializerProvider
    ) {
        gen.writeRawValue(value)
    }
}
```

```kotlin
package kr.leocat.json

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import java.io.IOException
import kotlin.jvm.Throws

/**
 * JsonObject -> JSON
 */
class StringJSONTypeDeserializer : JsonDeserializer<String>() {

    @Throws(IOException::class)
    override fun deserialize(
        p: JsonParser,
        ctxt: DeserializationContext
    ): String {
        return p.codec.readTree<JsonNode>(p).toString()
    }
}
```


# Serializer/Deserializer 설정

`address` 필드에서만 적용할 serializer와 deserializer를 설정해 주면 끗!!

```java
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class OrderDto {
    private Long orderId;
    private List<OrderItemDto> orderItems;

    @JsonSerialize(using = StringJSONTypeSerializer.class)
    @JsonDeserialize(using = StringJSONTypeDeserializer.class)
    private String address;
}
```

Kotlin data class에서 사용하는 경우는 [[Kotlin] Bean Validation 안 되는 문제](2020-12-10-kotest-bean-validation-in-kotlin) 내용처럼 annotation에 `field:`를 꼭 붙여준다. 안 붙여주면 클래스의 필드가 아닌 생성자의 파라미터에 annotation이 붙게 된다.

```kotlin
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize

data class OrderDto(
    val orderId: Long?,
    val orderItems: List<OrderItemDto>?,

    @field:JsonSerialize(using = StringJSONTypeSerializer::class)
    @field:JsonDeserialize(using = StringJSONTypeDeserializer::class)
    val address: String?,
)
```


# 참고

- [[Kotlin] Bean Validation 안 되는 문제](2020-12-10-kotest-bean-validation-in-kotlin)
- [Jackson – Custom Serializer](https://www.baeldung.com/jackson-custom-serialization)
