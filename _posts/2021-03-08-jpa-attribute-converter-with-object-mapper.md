---
layout: post
title:  "[JPA] AttributeConverter에 injection 받아 사용하기 - (ObjectMapper 등)"
date:   2021-03-08 22:18:00 +0900
published: true
categories: [ jpa ]
tags: [ java, jpa, attribute, converter, object mapper, jackson, injection ]
---

# AttributeConverter

JPA를 사용할 때 문자열 같은 값으로 DB에 저장되어 있고, 이 값을 converter를 통해서 클래스 객체로 변환할 수 있다.

```
@Entity(name = "order")
public class Order {
    @Convert(converter = OrderItemConverter.class)
    @Column(name = "order_items", columnDefinition = "json", nullable = false)
    private OrderItem orderItem;
}

public class OrderItem {
    private String itemName;
    private Integer count;
}
```

OrderItem을 별도 entity로 만들지 않고, 아래의 converter를 통해서 DB의 특정 컬럼에 저장하고 꺼내올 수 있다.

```java
@Converter
public class OrderItemConverter implements AttributeConverter<OrderItem, String> {
    private static final String SEPARATOR = ",";

    @Override
    public String convertToDatabaseColumn(OrderItem orderItem) {
        return orderItem.getItemName + SEPARATOR + orderItem.getCount();
    }

    @Override
    public AdTargetingConditions convertToEntityAttribute(String dbData) {
        String[] splittedData = dbData.split(SEPARATOR)
        OrderItem orderItem = new OrderItem();
        orderItem.setItemName(splittedData[0]);
        orderItem.setCount(Integer.parseInt(splittedData[1]));
        return orderItem;
    }
}
```


# Spring bean 으로 생성된 ObjectMapper와 함께 converter 사용하기

`SEPARATOR`가 아닌 json 형태로 저장한다면 ObjectMapper를 함께 사용해야 한다.

ObjectMapper는 모듈이나 프로젝트 전체에서 동일한 결과를 만들 수 있도록 하나의 Spring bean으로 ObjectMapperBuilder를 설정해 두고 사용하는 경우가 많다. 이런 경우에는 Hibernate와 같은 구현체에서 Spring bean에 접근할 수있도록 설정해 줘야 한다. 특히, AutoConfiguration을 사용하지 않고 EntityManagerFactory를 설정해서 사용하는 경우는 다음과 같이 설정을 해주면 된다.

```java
@Bean(name = MY_SERVICE_ENTITY_MANAGER_FACTORY)
public LocalContainerEntityManagerFactoryBean entityManagerFactory(ConfigurableListableBeanFactory beanFactory) {
    // @Converter 에서 빈 주입을 받으려면 아래와 같이 설정이 필요하다.
    // org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaConfiguration 참고.
    final HibernateSettings settings = new HibernateSettings();
    if (ClassUtils.isPresent("org.hibernate.resource.beans.container.spi.BeanContainer", getClass().getClassLoader())) {
        final HibernatePropertiesCustomizer customizer = (properties) -> properties.put(AvailableSettings.BEAN_CONTAINER, new SpringBeanContainer(beanFactory));
        settings.hibernatePropertiesCustomizers(List.of(customizer));
    }

    final JpaProperties adcenterServiceJpaProperties = adcenterServiceJpaProperties();
    return new EntityManagerFactoryBuilder(new HibernateJpaVendorAdapter(), adcenterServiceJpaProperties.getProperties(), null)
        .dataSource(adcenterDataSource())
        .properties(adcenterServiceHibernateProperties().determineHibernateProperties(myServiceJpaProperties.getProperties(), settings))
        .persistenceUnit(MY_SERVICE_PERSIST_UNIT)
        .packages("kr.leocat.test")
        .build();
}
```

AutoConfiguration을 사용하면 최근 버전에서는 특별한 설정 없이 사용이 가능하다.


# Spring bean 으로 생성된 ObjectMapper와 함께 converter 사용하기

이 방법이 안 된다면 조금 찜찜한 방법을 써야 한다.
모듈 전체에서 같은 ObjectMapper 설정을 사용하기 위해서 Spring bean 으로 ObjectMapperBuilder를 설정해 두고 사용하는 경우가 많다. 이런 경우에는 아래처럼 injection 을 받아야 한다.

```java
@Converter
@Component
public class OrderItemConverter implements AttributeConverter<OrderItem, String> {

    private static ObjectMapper objectMapper;


    @Autowired
    @SuppressWarnings("java:S2696") // Injection 을 받기 위해 static 필드에 setter 사용
    public void setJsonParser(ObjectMapper objectMapper) {
        OrderItemConverter.objectMapper = objectMapper;
    }

    @Override
    public String convertToDatabaseColumn(OrderItem orderItem) {
        try {
            return objectMapper.writeValueAsString(orderItem);
        } catch (Exception e) {
            // exception handling
        }
    }

    @Override
    public OrderItem convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, OrderItem.class);
        } catch (Exception e) {
            // exception handling
        }
    }
}
```


# 참고

- [Autowiring into JPA converters](https://stackoverflow.com/questions/36855901/autowiring-into-jpa-converters)
