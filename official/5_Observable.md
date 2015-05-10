#Observable

在RxJava中，实现Observer接口的对象（订阅者）向继承自 Observable 的对象注册订阅关系，订阅者能够对 Observable 对象 emit 出来的对象作出相应反应。
这种模式方便实现并行操作，在等待 Observable emit 对象的时候，订阅者使用哨兵机制，随时准备着在 Observable emit 对象的时候作出反应，所以整个过程并不会阻塞线程。