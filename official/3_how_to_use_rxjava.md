##Hello World!

下面的例子创建一个可观察的(Observable)字符串序列,然后使用一个打印"Hello ${String}!"的操作来订阅这个可观察对象,每当这个可观察对象 emit 一个字符串的时候,这个操作就会打印出这个字符串.

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

##怎样使用RxJava




















