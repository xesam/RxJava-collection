#how_to_use_rxjava

####名词说明
1. Observable 没有翻译, 可观察量. 类似于(但不等同)常见观察者模式中的被观察者,或者发布/订阅者模式中的发布者
2. emit 同理, 发射。类似于(但不等同)发布者发布一个消息。emitter —— 事件发射器


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

为了创建一个 Observable， 你可以手工实现一个显式的 create() 方法，或者通过内置的专用 Observable 操作将已有的数据结构转化成Observable。

###从现有的数据结构中创建 *Observable*

可以使用 just( ) 以及 from( ) 将 objects, lists, 或者 objects数组转化为能 emit 这些对象的 Observable

    Observable<String> o = Observable.from("a", "b", "c");

    def list = [5, 6, 7, 8]
    Observable<Integer> o = Observable.from(list);

    Observable<String> o = Observable.just("one object");

这些转化后得到的 Observable 对象 emit 的事件会同步执行一个订阅者的 onNext( ) 以及 onCompleted( )方法，然后接着就是下一个订阅者的相同方法。

译者注：这段容易产生歧义，因此我补充了一个例子：

    Observable<String> observable = Observable.from(new String[]{"a", "b"});
    observable.subscribe(new Subscriber<String>() {
        @Override
        public void onNext(String s) {
            System.out.println("1 onNext: " + s);
        }

        @Override
        public void onCompleted() {
            System.out.println("1 onCompleted ");
        }

        @Override
        public void onError(Throwable e) {
        }
    });
    observable.subscribe(new Subscriber<String>() {
        @Override
        public void onNext(String s) {
            System.out.println("2 onNext: " + s);
        }

        @Override
        public void onCompleted() {
            System.out.println("2 onCompleted ");
        }

        @Override
        public void onError(Throwable e) {
        }
    });

运行结果如下：

    //第一个订阅者
    1 onNext: a
    1 onNext: b
    1 onCompleted

    //第二个订阅者
    2 onNext: a
    2 onNext: b
    2 onCompleted

上面的示例先将一个字符串数组转换成Observable，注册两个订阅者，emit

###使用create()方法创建 *Observable*

通过实现 create( ) 方法来得到自定义的 Observable， 从而可以获得异步IO， 计算操作， 甚至无限数据流等特性。

####Synchronous *Observable* Example

下面是一个自定义的Observable，没有开辟新线程，在注册订阅者的时候会阻塞后面的操作。

    /**
     * This example shows a custom Observable that blocks
     * when subscribed to (does not spawn an extra thread).
     */
    def customObservableBlocking() {
        return Observable.create { aSubscriber ->
            50.times { i ->
                if (!aSubscriber.unsubscribed) {
                    aSubscriber.onNext("value_${i}")
                }
            }
            // after sending all values we complete the sequence
            if (!aSubscriber.unsubscribed) {
                aSubscriber.onCompleted()
            }
        }
    }

    // 运行结果
    customObservableBlocking().subscribe { println(it) }


####Asynchronous *Observable* Example

下面的示例是一个 emit 75个字符串的 Observable。

Groovy版本：

这段代码使用静态类型以及匿名类Func1 来使得示例比较清晰。

    /**
     * 自定义的Observable，开辟新线程，注册订阅者的时候不会阻塞后面的操作。
     */
    def customObservableNonBlocking() {
        return Observable.create({ subscriber ->
            Thread.start {
                for (i in 0..<75) {
                    if (subscriber.unsubscribed) {
                        return
                    }
                    subscriber.onNext("value_${i}")
                }
                // after sending all values we complete the sequence
                if (!subscriber.unsubscribed) {
                    subscriber.onCompleted()
                }
            }
        } as Observable.OnSubscribe)
    }

    //运行结果
    customObservableNonBlocking().subscribe { println(it) }

Clojure版本：

    (defn customObservableNonBlocking []
      "This example shows a custom Observable that does not block
       when subscribed to as it spawns a separate thread.

      returns Observable<String>"
      (Observable/create
        (fn [subscriber]
          (let [f (future
                    (doseq [x (range 50)] (-> subscriber (.onNext (str "value_" x))))
                    ; after sending all values we complete the sequence
                    (-> subscriber .onCompleted))
            ))
          ))

    //运行结果
    (.subscribe (customObservableNonBlocking) #(println %))

从 Wikipedia 获取文章，然后对每一项执行 onNext

    (defn fetchWikipediaArticleAsynchronously [wikipediaArticleNames]
      "Fetch a list of Wikipedia articles asynchronously.

       return Observable<String> of HTML"
      (Observable/create
        (fn [subscriber]
          (let [f (future
                    (doseq [articleName wikipediaArticleNames]
                      (-> subscriber (.onNext (http/get (str "http://en.wikipedia.org/wiki/" articleName)))))
                    ; after sending response to onnext we complete the sequence
                    (-> subscriber .onCompleted))
            ))))

    //执行

    (-> (fetchWikipediaArticleAsynchronously ["Tiger" "Elephant"])
      (.subscribe #(println "--- Article ---\n" (subs (:body %) 0 125) "...")))

Groovy 版本

    def fetchWikipediaArticleAsynchronously(String... wikipediaArticleNames) {
        return Observable.create { subscriber ->
            Thread.start {
                for (articleName in wikipediaArticleNames) {
                    if (subscriber.unsubscribed) {
                        return
                    }
                    subscriber.onNext(new URL("http://en.wikipedia.org/wiki/${articleName}").text)
                }
                if (!subscriber.unsubscribed) {
                    subscriber.onCompleted()
                }
            }
            return subscriber
        }
    }

    //执行

    fetchWikipediaArticleAsynchronously("Tiger", "Elephant")
        .subscribe { println "--- Article ---\n${it.substring(0, 125)}" }

执行结果

    --- Article ---
     <!DOCTYPE html>
    <html lang="en" dir="ltr" class="client-nojs">
    <head>
    <title>Tiger - Wikipedia, the free encyclopedia</title> ...
    --- Article ---
     <!DOCTYPE html>
    <html lang="en" dir="ltr" class="client-nojs">
    <head>
    <title>Elephant - Wikipedia, the free encyclopedia</tit ...


上面的示例都忽略了错误处理，错误处理参见下文

##使用 Operator 对 *Observable* 进行变换
RxJava 允许将多个操作串成操作链来对 Observable 进行变换和组合。
RxJava allows you to chain operators together to transform and compose Observables.

下面是示例使用一个预定义 emit 75个数据项的异步Observable，在发送到订阅者之前先使用skip(10)跳过最前面的10项，
接着使用take(5)选取后面的5项，再接下来使用map(...)对这5项进行变换处理，最终由订阅者将各项的结果打印出来。

Groovy版本

    /**
     * Asynchronously calls 'customObservableNonBlocking' and defines
     * a chain of operators to apply to the callback sequence.
     */
    def simpleComposition() {
        customObservableNonBlocking().skip(10).take(5)
            .map({ stringValue -> return stringValue + "_xform"})
            .subscribe({ println "onNext => " + it})
    }

运行结果：

    onNext => value_10_xform
    onNext => value_11_xform
    onNext => value_12_xform
    onNext => value_13_xform
    onNext => value_14_xform

下图展示了这一系列操作：

![Composition.1.png](https://github.com/Netflix/RxJava/wiki/images/rx-operators/Composition.1.png)

接下来一个示例，有三个相互依赖的 Observable，先使用 zip 将这三个 Observable 各自emit的数据项打包成一个数据集，然后使用map(...)对这个数据集进行变换处理。

Clojure版本

    (defn getVideoForUser [userId videoId]
      "Get video metadata for a given userId
       - video metadata
       - video bookmark position
       - user data
      return Observable<Map>"
        (let [user-observable (-> (getUser userId)
                  (.map (fn [user] {:user-name (:name user) :language (:preferred-language user)})))
              bookmark-observable (-> (getVideoBookmark userId videoId)
                  (.map (fn [bookmark] {:viewed-position (:position bookmark)})))
              ; getVideoMetadata requires :language from user-observable so nest inside map function
              video-metadata-observable (-> user-observable
                  (.mapMany
                    ; fetch metadata after a response from user-observable is received
                    (fn [user-map]
                      (getVideoMetadata videoId (:language user-map)))))]
              ; now combine 3 observables using zip
              (-> (Observable/zip bookmark-observable video-metadata-observable user-observable
                    (fn [bookmark-map metadata-map user-map]
                      {:bookmark-map bookmark-map
                      :metadata-map metadata-map
                      :user-map user-map}))
                ; and transform into a single response object
                (.map (fn [data]
                      {:video-id videoId
                       :video-metadata (:metadata-map data)
                       :user-id userId
                       :language (:language (:user-map data))
                       :bookmark (:viewed-position (:bookmark-map data))
                      })))))

    //结果形式如下：

    {:video-id 78965,
     :video-metadata {:video-id 78965, :title House of Cards: Episode 1,
                      :director David Fincher, :duration 3365},
     :user-id 12345, :language es-us, :bookmark 0}

图示：

![Composition2](https://github.com/Netflix/RxJava/wiki/images/rx-operators/Composition.2.png)

示例：来自[Ben Christensen’s QCon presentation on the evolution of the Netflix API](https://speakerdeck.com/benjchristensen/evolution-of-the-netflix-api-qcon-sf-2013)

在 emit 之前， 先使用merge合并两个 Observable，然后使用 reduce 从结果序列里面聚合构造出一个单独的序列，接着使用map对这个序列进行变换。

Groovy版本

    public Observable getVideoSummary(APIVideo video) {
       def seed = [id:video.id, title:video.getTitle()];
       def bookmarkObservable = getBookmark(video);
       def artworkObservable = getArtworkImageUrl(video);
       return( Observable.merge(bookmarkObservable, artworkObservable)
          .reduce(seed, { aggregate, current -> aggregate << current })
          .map({ [(video.id.toString() : it] }))
    }

图示：

![Composition3](https://github.com/Netflix/RxJava/wiki/images/rx-operators/Composition.3.png)

##错误处理

下面的示例是上面 Wikipedia 示例的带有错误处理的修订版本

    /*
     * Fetch a list of Wikipedia articles asynchronously, with error handling.
     */
    def fetchWikipediaArticleAsynchronouslyWithErrorHandling(String... wikipediaArticleNames) {
        return Observable.create({ subscriber ->
            Thread.start {
                try {
                    for (articleName in wikipediaArticleNames) {
                        if (true == subscriber.isUnsubscribed()) {
                            return;
                        }
                        subscriber.onNext(new URL("http://en.wikipedia.org/wiki/"+articleName).getText());
                    }
                    if (false == subscriber.isUnsubscribed()) {
                        subscriber.onCompleted();
                    }
                } catch(Throwable t) {
                    if (false == subscriber.isUnsubscribed()) {
                        subscriber.onError(t);
                    }
                }
                return (subscriber);
            }
        });
    }

注意上面的代码在错误发生的时候是怎样进行错误处理的，下面的示例就使用subscribe() 的第二个参数来传递错误处理操作。

    fetchWikipediaArticleAsynchronouslyWithErrorHandling("Tiger", "NonExistentTitle", "Elephant")
        .subscribe(
            { println "--- Article ---\n" + it.substring(0, 125) },
            { println "--- Error ---\n" + it.getMessage() })

除了上面的处理方法，RxJava还包含像 onErrorResumeNext() 以及 onErrorReturn()这类支持错误恢复的操作。

更多的错误处理操作参见 Error-Handling-Operators 一节

下面是一个
的里例子

假设你有一个或这几个级联的 Observable（即 myObservable），你想拦截所有会传递到订阅者onError方法的错误，并替换成自定义的Throwable。

为了实现这个目标，你可以通过onErrorResumeNext()来修改 myObservable，向onErrorResumeNext()传递一个使用 onError 来处理自定义错误的 Observable 即可。

工具方法 error() 可以创建这样的 Observable


    myModifiedObservable = myObservable.onErrorResumeNext({ t ->
       Throwable myThrowable = myCustomizedThrowableCreator(t);
       return (Observable.error(myThrowable));
    });















