# 工具库 weapon

## 模块

### 1. weapon-base 基础工具
  - [x] 通用数据对象封装：键值数据KVRecord及上下文对象等
  - [x] 字符串匹配：单模匹配、多模匹配
  - [x] 资讯类文章HTML正文抽取
  - [x] 基于OKHttp3的HTTP抓取工具
  - [x] Kafka生产及消费封装
  - [x] 基于redisson的Redis工具封装
  - [x] 基于JSON的切面日志工具
  - [x] Host及Domain工具
  - [x] 基于FastJson的JSON工具
  - [x] 反射工具
  - [x] 字节数据工具
  - [x] 文本相似性工具
  - [x] 图片工具
### 2. weapon-aliyun 阿里云工具
  - [x] 阿里云SDK动态AK/SK支持
  - [x] ONS支持：生产及消费封装
  - [x] OSS客户端封装
  - [x] OTS支持：客户端封装及数据通道封装
### 3. weapon-spring-boot-starter: spring boot starter支持
  - [x] 阿里云工具spring boot starter支持：ONS生产及消费支持、OSS支持、OTS客户端及数据通道支持
  - [x] Redis客户端spring boot starter支持
  - [x] spring web层增强：web切面日志支持、web层异常处理、web层响应对象归一化
### 4. weapon-dynamic-secret-listener 阿里云工具AK/SK加载实现
  - [x] 通过加载本地配置文件实现AK/SK加载，不具备动态更新功能
### 5. weapon-alarm 报警发送和监控定义


## 使用

如需使用基础工具，请引入以下artifact

```xml
<dependency>
    <groupId>xin.manong</groupId>
    <artifactId>weapon-base</artifactId>
    <version>按需参考release信息</version>
</dependency>
```

如需使用阿里云工具，请引入以下artifact

```xml
<dependency>
    <groupId>xin.manong</groupId>
    <artifactId>weapon-aliyun</artifactId>
    <version>按需参考release信息</version>
</dependency>
```

如需使用spring boot增强工具，请引入以下artifact

```xml
<dependency>
    <groupId>xin.manong</groupId>
    <artifactId>weapon-spring-boot-starter</artifactId>
    <version>按需参考release信息</version>
</dependency>
```

如需使用本地资源文件加载阿里云AK/SK，请引入以下artifact

```xml
<dependency>
    <groupId>xin.manong</groupId>
    <artifactId>weapon-dynamic-secret-listener</artifactId>
    <version>按需参考release信息</version>
</dependency>
```

如需实现自定义报警，请引入以下artifact，并自定义AlarmSender

```xml
<dependency>
    <groupId>xin.manong</groupId>
    <artifactId>weapon-alarm</artifactId>
    <version>按需参考release信息</version>
</dependency>
```

## 依赖三方库信息

| 组ID                        | 工件ID                 | 版本             | optional |
|:---------------------------|:---------------------|:---------------|:--------:|
| commons-codec              | commons-codec        | 1.15           |  false   |
 | com.alibaba                | fastjson             | 2.0.12         |  false   |
 | com.aliyun.openservices    | ons-client           | 1.8.0.Final    |   true   |
 | com.aliyun.openservices    | tablestore           | 5.13.10        |   true   |
| com.aliyun.oss             | aliyun-sdk-oss       | 3.15.0         |   true   |
| com.esotericsoftware       | kryo                 | 5.5.0          |   true   |
| com.fasterxml.jackson.core | jackson-annotations  | 2.13.3         |   true   |
 | com.hankcs                 | hanlp                | portable-1.7.8 |   true   |
| com.squareup.okhttp3       | okhttp3              | 4.9.3          |   true   |
 | jakarta.ws.rs              | jakarta.ws.rs-api    | 2.1.6          |   true   |
 | org.apache.commons         | commons-collections4 | 4.4            |  false   |
| org.apache.commons         | commons-lang3        | 3.8            |  false   |
| org.apache.kafka           | kafka-clients        | 3.3.1          |   true   |
| org.jsoup                  | jsoup                | 1.15.3         |   true   |
| org.redisson               | redisson             | 3.19.0         |   true   | 
 | org.sejda.imageio          | webp-imageio         | 0.1.6          |  false   |
 | org.slf4j                  | slf4j-log4j12        | 1.7.25         |   true   |
 | org.springframework        | spring-aspects       | 5.3.22         |   true   |
 | org.springframework        | spring-web           | 5.3.22         |   true   |
 | org.springframework.boot   | spring-boot-starter  | 2.7.3          |   true   |
