# 仿哔哩哔哩后端系统

一套模仿B站的弹幕视频网站后端项目，主要包括用户中心、用户订阅、权限控制、视频投稿、视频搜索、弹幕系统 等功能模块。

## 项目架构

![](D:\Mycode\github-project\bilibili-project\img\image-20230914212248181.png)

## 技术选型

|     技术      |  版本  |
| :-----------: | :----: |
|  Spring Boot  | 2.7.5  |
|   Websocket   |   /    |
|    MyBatis    | 2.3.1  |
| MyBatis-Plus  | 3.5.3  |
|      JWT      | 3.18.2 |
|     MySQL     |  5.7   |
|    FastDFS    | 1.27.2 |
|     Redis     |  6.2   |
|   RocketMQ    | 4.9.1  |
| Elasticsearch |  7.0   |



## 核心功能------弹幕系统架构

![](D:\Mycode\github-project\bilibili-project\img\image-20230914215952081.png)

- 场景分析：用户通过浏览器进入仿b站项目，在视频详情页可以发送弹幕。
- 前端生成弹幕信息：用户点击发送弹幕后，前端生成一个弹幕相关的json数据，需要将该数据发送给后端进行处理。
- 后端处理：后端需要对所有正在观看视频的用户进行推送弹幕操作，包括发送弹幕的人自己也能看到生成的弹幕。后端还需将弹幕信息存储到数据库和缓存中以方便查询。
- 设计实现方式：有两种实现方式可选，即短连接和长连接。
  - 短连接通信：因为HTTP协议的通信只能由客户端向服务端发起，需不断轮询后端接口，查询是否有新的弹幕，效率较低且浪费资源。
  - 长连接通信：使用websocket协议建立持久的双向通信连接，解决了短连接的问题，使服务器和客户端能主动发起通信。
- WebSocket协议：WebSocket是基于TCP的全双工通信协议，实现了浏览器与服务器之间的双向通信。它具有报文体积小、支持长连接等优点，但对于扩展性较HTTP协议稍逊。

长连接具体实现：

- 核心使用 @ServerEndpoint 注解标记为一个 WebSocket 服务器端点，并且指定访问的路径。
- 然后，当客户端成功连接到服务器时，会调用 @OnOpen 注解里面的方法，去把当前会话实例 (session)和唯一标识符 (sessionId) 进行一个保存，存到ConcurrentHashMap里面，并且还会进行在线的人数统计。
- 而当客户端发送消息时，服务器会调用 @OnMessage 注解里面的方法，进行消息的群发。（这一步中进行了MQ 削峰）
- 最后，还有一个@OnClose 注解，断开连接的时候，把当前会话的实例移除。



其他功能文档，后续完善。

## 开发Bug记录

1.启动RocketMQ时报错，发现是C盘空间不够，内存不足；

2.Websocket的多例问题

Spring默认实例化的Bean是单例模式，这就意味着在Spring容器加载时，就注入了RedisTemplate的实例，不管再调用多少次接口，加载的都是这个Bean同一个实例。

而WebSocket是多例模式，在项目启动时第一次初始化实例时，RedisTemplate的实例的确可以加载成功，但可惜这时WebSocket是无用户连接的。当有第一个用户连接时，WebSocket类会创建第二个实例，但由于Spring的Dao层是单例模式，所以这时RedisTemplate对应的实例为空。后续每连接一个新的用户，都会再创建新的WebSocket实例，当然RedisTemplate的实例都为空。
