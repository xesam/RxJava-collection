#什么是函数响应式编程(Functional Reactive Programming:FRP)

[原文](http://www.jdon.com/45581) 的翻译有点问题,所以我重新组织了下。

##简介
函数响应式编程(Functional Reactive Programming:FRP)是一种和事件流有关的编程方式。
在程序中，导致状态值发生改变的行称为事件，一系列事件组成事件流, FRP的核心就是高效地处理事件流，而无需显式管理状态。

具体来说，FRP包括两个核心观点：

1. 事件流(离散事件序列)
2. 属性properties, 代表模型连续的值

事件流是导致属性值发生变化的原因, 因此FRP非常类似于GOF的观察者模式。

##为什么需要FRP?
FRP的需求来源于对于多个值发生改变，以javascript为例子，如下：

```javascript
    var a = function (b,c) { return b + c } // a = b + c
```

其中a实际代表b与c之和,但是,上述代码只是一种表达式，并没有指定a值的变化依赖b和c。如果b或c持续不断在被改变，如何触发a值也跟着变化呢？

使用Reactive.js可以达到指定这种依赖关系，我们将b和c可以看成是被观察者，而a作为观察者，如果随着时间推移，b和c的值不断变化，并将这种变化传导到a。
代码如下：

```javascript
    //A = B + C
    var reactiveA = $R(function (b, c) { return b + c });
    var reactiveB = $R.state(2);
    var reactiveC = $R.state(1);
    reactiveA.bindTo(reactiveB, reactiveC);

    reactiveA();   //-> 3
    reactiveB(5);  //Set reactiveB to 5
    reactiveC(10); //Set reactiveC to 10
    reactiveA();   //-> 15
```

我们将导致b和c被观察者发生变化的一系列事件组成事件流，如果用集合来表示事件流，那么FRP框架所要做的就是，遍历这个事件流集合，将导致b和c的变化的事件重新播放，然后获得a的一系列结果。
当存在重复的事件流时，选取其中一个事件，当事件流如果很多时，就需要进行压缩。因此事件流也被认为是一种可观察者序列(observable sequences)

所有这些针对事件流的额外加工处理需要专门框架实现，[RxJava](https://github.com/Netflix/RxJava)和RxJS分别这样的框架
RxJava作为一个Functional reactive框架，可以提供了如下的事件流处理能力：

1. filtering
2. selecting
3. transforming
4. combining
5. composing

在对被观察的数据类型进行遍历中，观察者从被观察者那里拉取poll数据，然后，线程会堵塞等待直到这些数值真正到达获取。
反过来讲，被观察者在数值可用时，则是将数值推送push给观察者，这样的方式更加灵活，因为数值的获取可以是同步或异步。

以RxJava/groovy代码为例子：

```groovy
    /**
     * 异步调用'customObservableNonBlocking' 并 注册一个操作链
     */
    def simpleComposition() {
      // 异步获取字符串
      customObservableNonBlocking()
        // 忽略前10个
        .skip(10)
        // 选取接下来的5个
        .take(5)
        // 变换
        .map({ stringValue -> return stringValue + "_transformed"})
        // 订阅序列，并打印变换后的数据
        .subscribe({ println "onNext => " + it})
    }
```

上述代码是对事件流集合中从索引10开始5个 事件进行订阅。

    // output
    onNext => anotherValue_10_transformed
    onNext => anotherValue_11_transformed
    onNext => anotherValue_12_transformed
    onNext => anotherValue_13_transformed
    onNext => anotherValue_14_transformed

使用Clojure 实现如下：

```clojure
    (defn simpleComposition []
      "Asynchronously calls 'customObservableNonBlocking' and defines a
       chain of operators to apply to the callback sequence."
      (->
        ; fetch an asynchronous Observable<String>
        ; that emits 75 Strings of 'anotherValue_#'
        (customObservableNonBlocking)
        ; skip the first 10
        (.skip 10)
        ; take the next 5
        (.take 5)
        ; transform each String with the provided function
        (.map #(str % "_transformed"))
        ; subscribe to the sequence and print each transformed String
        (.subscribe #(println "onNext =>" %))))
```

output

    onNext => anotherValue_10_transformed
    onNext => anotherValue_11_transformed
    onNext => anotherValue_12_transformed
    onNext => anotherValue_13_transformed
    onNext => anotherValue_14_transformed

