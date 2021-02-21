# 分布式系统下单、砍价、秒杀、减库存

> 本demo里面包含项目所有需要的源码和完整的`SQL`文件

​                                                                                                             **拼刀刀砍价系统架构图**

![image-20210220220618939](https://alphahub-test-bucket.oss-cn-shanghai.aliyuncs.com/image/image-20210220220618939.png)

# 1 传统的解决方案

## 1.1 乐观锁

其核心思想`CAS`（Compare And Set）

```sql
update table set version = version+1 where id = #{id}
```

## 1.2 使用[Jedis](https://github.com/redis/jedis)的`set if not exists`



以上这些方法实现起来代码罗里吧嗦，鉴于此，写出此文方便交流学习。



# 2 本文项目实现示例

> ## `Redission`

`redis`官方推荐的分布式锁解决方案：[官方PUB发文](https://redis.io/topics/distlock), 也是本文介绍的方案`spring boot + spring coud + redisson`，从`redis`官方文档我们可以看到 `redission`是`redis`在`java`实现中分布式红锁的不二选择。

![image-20210220173026981](https://alphahub-test-bucket.oss-cn-shanghai.aliyuncs.com/image/image-20210220173026981.png)





- 准备工作

1. *安装好`MySQL`*
2. *安装好`Redis`*

- Redission核心配置

1. `application.yml`

```yaml
spring:
  application:
    name: stock-decrease
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://192.168.40.132:33306/ah_stock_decrease?serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true&autoReconnect=true&allowMultiQueries=true
    username: root
    password: 123456
  redis:
    database: 6
    host: 192.168.226.129
    port: 6379
    password: 123456
    client-name: stock_decrease_client
    # Redisson settings
    # Tips: Use Redisson through spring bean with RedissonClient interface or
    #       RedisTemplate/ReactiveRedisTemplate objects
    #path to config - redisson.yml
    redisson:
      file: classpath:redisson-single.yml
```

2. `redisson-single.yml`

```yaml
# 单节点配置
singleServerConfig:
  #连接空闲超时(毫秒)，默认10000
  idleConnectionTimeout: 10000
  #连接空闲超时(毫秒)，默认10000
  connectTimeout: 10000
  #命令等待超时(毫秒)，默认3000
  timeout: 3000
  #命令失败重试次数
  retryAttempts: 3
  #命令重试发送时间间隔(毫秒)，默认1500
  retryInterval: 1500
  #单个连接最大订阅数量，默认5
  subscriptionsPerConnection: 5
  #客户端名称
  clientName: ALPHAHUB_REDISSION_CLIENT1
  address: redis://192.168.40.132:6379
  password: 123456
  database: 6
  #发布和订阅连接的最小空闲连接数，默认1
  subscriptionConnectionMinimumIdleSize: 1
  #发布和订阅连接池大小，默认50
  subscriptionConnectionPoolSize: 50
  #最小空闲连接数，默认32
  connectionMinimumIdleSize: 24
  #连接池大小，默认64
  connectionPoolSize: 64
  #DNS监测时间间隔(毫秒)，默认5000
  dnsMonitoringInterval: 5000
threads: 16
nettyThreads: 32
codec: !<org.redisson.codec.JsonJacksonCodec> { }
transportMode: "NIO"
```

2. `redisson-cluster.yml`

```yaml
#集群配置
clusterServersConfig:
  idleConnectionTimeout: 10000
  connectTimeout: 10000
  timeout: 3000
  retryAttempts: 3
  retryInterval: 1500
  failedSlaveReconnectionInterval: 3000
  failedSlaveCheckInterval: 60000
  password: null
  subscriptionsPerConnection: 5
  clientName: null
  loadBalancer: !<org.redisson.connection.balancer.RoundRobinLoadBalancer> { }
  subscriptionConnectionMinimumIdleSize: 1
  subscriptionConnectionPoolSize: 50
  slaveConnectionMinimumIdleSize: 24
  slaveConnectionPoolSize: 64
  masterConnectionMinimumIdleSize: 24
  masterConnectionPoolSize: 64
  readMode: "SLAVE"
  subscriptionMode: "SLAVE"
  nodeAddresses:
    - "redis://127.0.0.1:7004"
    - "redis://127.0.0.1:7001"
    - "redis://127.0.0.1:7000"
  scanInterval: 1000
  pingConnectionInterval: 0
  keepAlive: false
  tcpNoDelay: false
  threads: 16
  nettyThreads: 32
  codec: !<org.redisson.codec.MarshallingCodec> { }
  transportMode: "NIO"
```

4. 最终结构

![image-20210220211653977](https://alphahub-test-bucket.oss-cn-shanghai.aliyuncs.com/image/image-20210220211653977.png)





## 2.1 项目结构

- maven结构

![image-20210220180649594](https://alphahub-test-bucket.oss-cn-shanghai.aliyuncs.com/image/image-20210220180649594.png)

- 特别提示

本项目启动起来一共是9个服务，电脑配置不够可能需要设置下`JVM`内存，参数：`-Xms512m -Xmx512m -Xmn200m`

IDEA的配置位置：

![GIF](https://alphahub-test-bucket.oss-cn-shanghai.aliyuncs.com/image/GIF.gif)



## 2.2 节点配置

- 如何在IDEA中将同一个微服务配置多个副本

![IDEA同一份springboot应用复制多分示例为集群](https://alphahub-test-bucket.oss-cn-shanghai.aliyuncs.com/image/IDEA同一份springboot应用复制多分示例为集群.gif)

用同样的方式继续给`AlphahubGatewayApplication` `和StockDecreaseDemoApplication`各自增加两个副本，这样就可以组成3节点的集群模式，eureka服务端集群和eureka客户端集群配置如下：

![eureka服务端集群和eureka客户端集群配置](https://alphahub-test-bucket.oss-cn-shanghai.aliyuncs.com/image/eureka服务端集群和eureka客户端集群配置.gif)



## 2.3 `nginx`负载均衡配置

```nginx
# cd /usr/local/nginx/sbin/ && ./nginx -s reload
# cd /usr/local/nginx/sbin/ && ./nginx -t

#user  nobody;
worker_processes  1;

#error_log  logs/error.log;
#error_log  logs/error.log  notice;
#error_log  logs/error.log  info;

pid        logs/nginx.pid;

events {
    worker_connections  1024;
}

http {
    include       mime.types;
    default_type  application/octet-stream;

    sendfile        on;
    keepalive_timeout  65;

    server_names_hash_bucket_size 64;

    client_max_body_size 4G;
    
    #网关服务集群
    upstream getewayClusterService{
        ip_hash;
   		server 127.0.0.1:10000;
   		server 127.0.0.1:10001;
   		server 127.0.0.1:10002;
	}
    
    server {
        listen       80;
        
        server_name  localhost;

        proxy_ssl_verify off;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header REMOTE-HOST $remote_addr;
        proxy_set_header Host $http_host;
        proxy_set_header cookie $http_cookie;
        proxy_set_header Proxy-Connection "";
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        location / {
            #路由到网关服务集群，将请求通过网关分发个下游集群服务
            proxy_pass http://getewayClusterService;
        }

        error_page  404              /404.html;

        error_page   500 502 503 504  /50x.html;
        
        location = /50x.html {
            root   html;
        }
    }
}
```



## 2.4 并发压力测试说明

> 使用[ab](https://www.apachelounge.com/download/)进行并发压力测试

- 下载安装[ab](https://www.apachelounge.com/download/)

![image-20210220175201755](https://alphahub-test-bucket.oss-cn-shanghai.aliyuncs.com/image/image-20210220175201755.png)



- 环境变量设置说明

这里只提供windows的版本，Linux请自行百度。

变量名：`APACHE_HTTPD_HOME`

变量值：`E:\ProgramFiles\httpd-2.4.46-win64-VS16\Apache24`

变量值替换成自己的文件解压目录

![image-20210220175741161](https://alphahub-test-bucket.oss-cn-shanghai.aliyuncs.com/image/image-20210220175741161.png)

![image-20210220175916050](https://alphahub-test-bucket.oss-cn-shanghai.aliyuncs.com/image/image-20210220175916050.png)

![image-20210220180012373](https://alphahub-test-bucket.oss-cn-shanghai.aliyuncs.com/image/image-20210220180012373.png)

- 简单使用说明

> 最基本的关心两个选项 -c -n
>
> 例： ab -n 1000 -c 100 http://www.baidu.com/
>
> -n 1000           即： 共发送1000个请求
> -c 100              即：每次并发100个
> -C                     添加cookie信息，例如："Apache=1234"(可以重复该参数选项以添加多个)
> -H                     添加任意的请求头，例如："Accept-Encoding: gzip"，请求头将会添加在现有的多个请求头之后(可以重复该参数选项以添加多个)
>
> 并发压力测试示例：
>
> ab -c 8000 -n 8000 -H "Authorization:0f8a-477d-bcaf-1234" "http://127.0.0.1/api/stock/product/decreaseStock/1/1"

## 2.5 启动集群服务使用并发压力测试

### 2.5.1 启动服务验证集群功能

![启动服务验证集群功能](https://alphahub-test-bucket.oss-cn-shanghai.aliyuncs.com/image/启动服务验证集群功能.gif)

通过网关调用地址：

http://localhost:10000/api/stock/product/info/1

http://localhost:10001/api/stock/product/info/1

http://localhost:10002/api/stock/product/info/1

返回数据一致：

```json
{
  "message": "操作成功",
  "success": true,
  "timestamp": "2021-02-21 01:36:29",
  "code": 200,
  "data": {
    "id": 1,
    "name": "苏打水",
    "categoryId": 12,
    "brandId": 245,
    "purchasePrice": 200,
    "priceUnit": "分",
    "stockQuantity": 1,
    "stockUnit": "个",
    "deleted": 0,
    "createTime": "2021-02-20 00:56:53",
    "createBy": "admin",
    "updateTime": "2021-02-21 01:36:04",
    "updateBy": null,
    "remark": null
  }
}
```



### 2.5.2 在库存量为0时测试ab工具访问`nginx`下游分发的请求量

请求链路：ab工具 -->  `nginx` 负载均衡 -->  网关服务（3个节点）  -->  库存服务（3个节点） -->  查看下游服务各自接收到的请求数量



接下来我们试10次并发请求,看负载均衡的结果

在终端输入：`cls && ab -c 10 -n 10 "http://127.0.0.1/api/stock/product/decreaseStock/1/1"`

![在库存量为0时测试ab工具访问nginx下游分发的请求量](https://alphahub-test-bucket.oss-cn-shanghai.aliyuncs.com/image/在库存量为0时测试ab工具访问nginx下游分发的请求量.gif)



### 2.5.3 手动给db里面的库存字段添加几个库存进行压测

> **压测：15000 的并发请求秒杀3000的库存量**

在终端输入：

```shell
#windows
cls && ab -c 15000 -n 15000 "http://127.0.0.1/api/stock/product/decreaseStock/1/1

#Linux
clear && ab -c 15000 -n 15000 "http://127.0.0.1/api/stock/product/decreaseStock/1/1
```

![手动给db里面的库存字段添加几个库存进行压测](https://alphahub-test-bucket.oss-cn-shanghai.aliyuncs.com/image/手动给db里面的库存字段添加几个库存进行压测.gif)

最终效果：

![image-20210221024139583](https://alphahub-test-bucket.oss-cn-shanghai.aliyuncs.com/image/image-20210221024139583.png)

# 3 `TODO`

- [ ] 引入分布式事务

