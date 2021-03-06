# s-rpc

本来是想整一个RPC框架的玩具的，期间也查了很多资料，不过后来搞了一半，大概的架子已经好了，补上Netty的通信部分基本就差不多了，然后光荣地烂尾了。

现在想想还是补上一个说明吧。

### Client部分

调用模型：

* Oneway：调用后不关心结果，不等待结果直接返回，也不知道是否成功；

* Sync：同步阻塞调用，等待结果返回，有超时时间，其实内部也是使用Future#get，只不过没对外暴露而已；

* Future：调用后返回一个Future对象，不阻塞，使用Future#get时才根据结果是否已经返回来决定是否阻塞，即调用时不阻塞，获取结果时可能阻塞；

* Callback：调用后返回，不阻塞，同时会注册一个回调，结果返回时提交给异步线程池执行回调。


连接：

* 每个RPC请求都创建一个TCP连接，完成后关闭，这种方式显然是不行的；

* 维护一个连接池，以类似懒加载的方式，缓存TCP连接，每次RPC请求时都从连接池获取；

* Client和Server只建立唯一一个TCP连接，所有RPC请求都复用此连接。


负载均衡：

Client和多个Server建立连接，RPC请求时选择一个连接调用，一个Client和一个Server的情况下负载均衡也没啥意义。

### Server部分

缓存：

对于被调用的服务，第一次使用反射生成实例，之后使用缓存的对象调用。

拦截器链：

类似职责链的调用，方便后续扩展和增加功能。

隔离：

不同的业务可以用不同的线程池进行隔离，互不影响。

限流：

* 线程池和队列限流，处理不过来就拒绝或者排队；

* 信号量限流，限制最大的请求处理数，超出则根据策略拒绝或者阻塞等待（当然不能太久）；

* 计数器：假设每秒处理100个请求，那么可以设置滑动窗口为1秒，假设该窗口有10个格子，那么每个格子代表100毫秒，记录服务请求数，如果当前和第一个格子相差超过100，说明需要限流。

    * 每个格子包含了计数的起始时间戳，时间段内的请求总数，异常请求总数；
    
    * 请求过来时根据当前的时间戳定位到所属的格子，并在格子上记录请求数量和是否有异常；

* 漏桶：一个漏桶，容量固定，按固定速率流出水滴，同时可以以任意速率添加水（请求）到漏桶，如果流入的水超出了桶的容量，则溢出（请求被丢弃）。

* 令牌桶：一个桶，容量固定，按固定速率往桶里添加令牌，当桶满时，新添加的令牌被丢弃，处理请求需要消耗令牌。

    * 其实也不需要另外的线程去隔固定的时间放令牌，每次取Token之前，根据当前时间戳和上次放置Token的时间戳，计算需要的Token并放入即可，ThreadLocal和Guava的Cache也是类似。

熔断：

熔断的时机是失败的请求比例超出了阈值，直接拒绝请求，并且后续在一定时间内都会拒绝请求。

* CLOSED：熔断器闭合状态，说明一切正常，可以直接调用；

* OPEN：熔断器打开状态，不能调用，拒绝请求；

* HALF OPEN：熔断器半开状态，允许一定量的请求，如果这些请求调用成功，可以把熔断器恢复闭合状态。


### 网络部分

使用Reactor模型，一个线程用于处理ServerSocketChannel，接受请求，对于生成的SocketChannel产生的读写事件，交给IO线程池。

Netty的一个优点就是，一个SocketChannel只分配一个固定的EventLoop，多个SocketChannel也可以分配给同一个EventLoop。

这样，对于某个SocketChannel上产生的事件，都是由同一个线程串行处理，避开了多线程的切换问题，也不用加锁，多好。

另外对于Server，BossGroup的EventLoopGroup配置有多个EventLoop也没有意义，因为ServerSocketChannel最终只会注册到一个EventLoop上。

关于线程池的线程数量配置问题，线程数也不是越多越好，处理任务消耗的时间都是固定的，多线程不一定比单线程快，而且多线程还有线程切换的开销。

使用多线程的意义是尽可能的压榨CPU，比如一个任务线程因为IO阻塞，白占着CPU，这时使用别的线程就可以利用空闲的CPU，执行别的任务。

如果本来CPU就100%占用了，再增加线程也没意义。


### 其它

可以添加的东西：

* 生命周期，加上startUp/shutDown接口，使用前初始化，使用后释放资源之类的；

* 应用层心跳之类的；

* SPI扩展之类的；

* 注册中心之类的；

* 反正都不会加的啦。
