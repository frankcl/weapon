# 工具库 weapon

## 模块

> * weapon-base: 基础工具
> * weapon-aliyun: 阿里云工具，例如OTS、OSS和ONS等
> * weapon-spring-boot-starter: spring boot工具，目前实现了阿里云相关工具spring boot增强
> * weapon-alarm: 报警发送和监控定义，自定义报警需继承实现AlarmSender
> * weapon-dynamic-secret-listener: 阿里云工具AK/SK加载实现，通过加载本地配置文件实现AK/SK加载，不具备动态更新功能


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

