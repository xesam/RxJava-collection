#Hello World!

####名词说明
1. Observable 没有翻译, 可观察量. 类似于(但不等同)常见观察者模式中的被观察者,或者发布/订阅者模式中的发布者
2. emit 同理,类似于(但不等同)发布者发布一个消息


#正文

下面的例子创建一个 *Observable* 字符串序列,然后使用一个打印"Hello ${String}!"的操作来订阅这个可观察量,每当这个可观察量 *emit* 一个字符串的时候,这个操作就会打印出这个字符串.

完整示例代码参见 :

[RxGroovy examples](https://github.com/ReactiveX/RxGroovy/tree/1.x/src/examples/groovy/rx/lang/groovy/examples)
[RxClojure examples](https://github.com/ReactiveX/RxClojure/tree/0.x/src/examples/clojure/rx/lang/clojure/examples)
[RxScala examples](https://github.com/ReactiveX/RxScala/tree/0.x/examples/src/main/scala)

###Java
    
    public static void hello(String... names) {
        Observable.from(names).subscribe(new Action1<String>() {
    
            @Override
            public void call(String s) {
                System.out.println("Hello " + s + "!");
            }
    
        });
    }

运行结果:
    
    hello("Ben", "George");
    Hello Ben!
    Hello George!
    
###Groovy

    def hello(String[] names) {
        Observable.from(names).subscribe { println "Hello ${it}!" }
    }
    
运行结果:

    hello("Ben", "George")
    Hello Ben!
    Hello George!


###Clojure

(defn hello
  [&rest]
  (-> (Observable/from &rest)
    (.subscribe #(println (str "Hello " % "!")))))

运行结果:

    (hello ["Ben" "George"])
    Hello Ben!
    Hello George!

###Scala

    import rx.lang.scala.Observable
    
    def hello(names: String*) {
      Observable.from(names) subscribe { n =>
        println(s"Hello $n!")
      }
    }

运行结果:

    hello("Ben", "George")
    Hello Ben!
    Hello George!

#怎样使用RxJava
使用RxJava的大致流程

1. 创建 *Observable*, *Observable* 会 *emit* 数据项
2. 对 *Observable* 进行相应的变换, 获得真正感兴趣的数据类型
3. 对最终获得的感兴趣数据类型序列进行操作

##创建 *Observable*



###从现有的数据结构中创建 *Observable*

###使用create()方法创建 *Observable*

####Synchronous *Observable* Example


####Asynchronous *Observable* Example

##使用 Operator 对 *Observable* 进行变换


##错误处理
















