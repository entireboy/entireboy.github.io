---
layout: post
title:  "[Spring Batch] Partition 사용할 때 scope 설정"
date:   2020-06-10 21:18:00 +0900
published: true
categories: [ spring ]
tags: [ spring, spring batch, scope, config, setting, partition ]
---

Spring Batch 파티션(partition)을 사용하면 파티션이 공용으로 사용하게 되는 bean들이 생성된다. 이 때 scope 설정을 잘못 하면 아래와 같은 오류를 볼 수 있다.

```bash
ERROR 17:35:29.574 [main] o.s.batch.core.step.AbstractStep - Encountered an error executing step beginPartitionStep in job beginServiceJob
java.util.concurrent.ExecutionException: org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'scopedTarget.beginStep': Scope 'job' is not active for the current thread; consider defining a scoped proxy for this bean if you intend to refer to it from a singleton; nested exception is java.lang.IllegalStateException: No context holder available for job scope
    at java.util.concurrent.FutureTask.report(FutureTask.java:122)
    at java.util.concurrent.FutureTask.get(FutureTask.java:192)
    at org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler.doHandle(TaskExecutorPartitionHandler.java:121)
    ...
Caused by: org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'scopedTarget.beginPartitionStep': Scope 'job' is not active for the current thread; consider defining a scoped proxy for this bean if you intend to refer to it from a singleton; nested exception is java.lang.IllegalStateException: No context holder available for job scope
    at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:365)
    ...
Caused by: java.lang.IllegalStateException: No context holder available for job scope
    at org.springframework.batch.core.scope.JobScope.getContext(JobScope.java:159)
    at org.springframework.batch.core.scope.JobScope.get(JobScope.java:92)
    at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:353)
    ... 10 common frames omitted
```

결론적으로 이야기 하면, partition을 생성하는 step 은 `@JobScope` 으로 생성해야 하고, 각 파티션이 작업을 실행하는 slave step은 scope 없이 생성하고, 각 slave step 에서 사용할 reader, processor, writer 등은 `@StepScope`으로 생성해야 한다.


```java
@Bean(name = JOB_NAME)
public Job beginService(JobBuilderFactory jobBuilderFactory) {
    log.info("Job {} starting...", JOB_NAME);
    return jobBuilderFactory.get(JOB_NAME)
        .incrementer(new ParamCleanRunIdIncrementer())
        .start(beginPartitionStep(null, null))
        .preventRestart()
        .build();
}

@Bean
@JobScope
public Step beginPartitionStep(StepBuilderFactory stepBuilderFactory,
                                    ThreadPoolTaskExecutor myTaskExecutor) {

    return stepBuilderFactory.get("beginPartitionStep")
        .partitioner("beginStep", beginPartitioner())
        .gridSize(PARTITION_SIZE)
        .step(beginStep(null, null))
        .taskExecutor(myTaskExecutor)
        .build();
}

@Bean
public Step beginStep(StepBuilderFactory stepBuilderFactory,
                           PlatformTransactionManager myTransactionManager) {

    return stepBuilderFactory.get("beginStep")
        .<AdCampaign, AdCampaign>chunk(CHUNK_SIZE)
        .reader(beginReader())
        .processor(beginProcessor())
        .writer(beginWriter())
        .transactionManager(myTransactionManager)
        .build();
}

@Bean
@StepScope
public beginReader() {
    ...
}
```
