#简介

RxJava是 ReactiveX (Reactive Extensions)在JVM上的实现:一个使用 *observable sequences* 组合异步事件流的库.

更多关于ReactiveX的信息可以移步[ReactiveX](http://reactivex.io/intro.html).

##RxJava 是轻量级的
RxJava尽量保持轻量级,他的实现是有一个jar包,关注点集中在Observable抽象以及高阶函数上面.你可以在此之上实现无偏差的组合Future,
Akka Futures 就是一个实现Actor模式以及其他任务的库

##RxJava多语言实现

RxJava支持java6及以上,还支持一些基于JVM的语言,包括Groovy, Clojure, JRuby, Kotlin 以及 Scala.

RxJava的目标不仅仅只是Java/Scala,而是被设计为能更融合各种jvm方言的管用习惯.
