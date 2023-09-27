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
  - [x] DataHub客户端封装
  - [x] SLS日志客户端封装
### 3. weapon-spring-boot-starter: spring boot starter支持
  - [x] 阿里云工具spring boot starter支持：ONS生产及消费支持、OSS支持、OTS客户端及数据通道支持、DataHub客户端支持、SLS日志客户端支持
  - [x] Redis客户端spring boot starter支持
  - [x] spring web层增强：web切面日志支持、web层异常处理、web层响应对象归一化
### 4. weapon-dynamic-secret-listener 阿里云工具AK/SK加载实现
  - [x] 通过加载本地配置文件实现AK/SK加载，不具备动态更新功能
### 5. weapon-alarm 报警和监控规范及接口定义


## 使用

如需使用，请引入对应工具包

| 工件                             | 分组         | 最新版本  | 是否必须 | 说明                 |
|:-------------------------------|:-----------|:------|:-----|:-------------------|
| weapon-base                    | xin.manong | 0.1.7 | 否    | 基础开发工具             |
| weapon-aliyun                  | xin.manong | 0.1.7 | 否    | 阿里云工具              |
| weapon-spring-boot-starter     | xin.manong | 0.1.7 | 否    | spring boot增强工具    |
| weapon-dynamic-secret-listener | xin.manong | 0.1.7 | 否    | 通过本地资源文件加载阿里云AK/SK |
| weapon-alarm                   | xin.manong | 0.1.7 | 否    | 报警接口定义，引入使用实现自定义报警 |

## 依赖三方库信息

| 工件                   | 分组                         |       版本       | optional |
|:---------------------|:---------------------------|:--------------:|:--------:|
| aliyun-log           | com.aliyun.openservices    |     0.6.82     |   true   |
| aliyun-log-producer  | com.aliyun.openservices    |     0.3.12     |   true   |
| aliyun-sdk-datahub   | com.aliyun.datahub         |  2.3.0-public  |   true   |
| aliyun-sdk-oss       | com.aliyun.oss             |     3.15.0     |   true   |
| commons-codec        | commons-codec              |      1.15      |  false   |
| commons-collections4 | org.apache.commons         |      4.4       |  false   |
| commons-lang3        | org.apache.commons         |      3.8       |  false   |
| fastjson             | com.alibaba                |     2.0.35     |  false   |
| hanlp                | com.hankcs                 | portable-1.7.8 |   true   |
| jackson-annotations  | com.fasterxml.jackson.core |     2.13.3     |   true   |
| jakarta.ws.rs-api    | jakarta.ws.rs              |     3.0.0      |   true   |
| jsoup                | org.jsoup                  |     1.15.3     |   true   |
| kafka-clients        | org.apache.kafka           |     3.3.1      |   true   |
| kryo                 | com.esotericsoftware       |     5.5.0      |   true   |
| okhttp3              | com.squareup.okhttp3       |     4.9.3      |   true   |
| ons-client           | com.aliyun.openservices    |  1.8.0.Final   |   true   |
| redisson             | org.redisson               |     3.19.0     |   true   | 
| slf4j-log4j12        | org.slf4j                  |     1.7.25     |   true   |
| spring-aspects       | org.springframework        |     5.3.22     |   true   |
| spring-boot-starter  | org.springframework.boot   |     2.7.3      |   true   |
| spring-web           | org.springframework        |     5.3.22     |   true   |
| tablestore           | com.aliyun.openservices    |    5.13.10     |   true   |
| webp-imageio         | org.sejda.imageio          |     0.1.6      |  false   |


 
 
 



 


 



 
 
 
 
 
