##获取二进制文件

你可以在[http://search.maven.org](http://search.maven.org)上找到关于Maven, Ivy, Gradle, SBT等等的二进制以及各种依赖.

Maven:

    <dependency>
        <groupId>io.reactivex</groupId>
        <artifactId>rxjava</artifactId>
        <version>1.0.0</version>
    </dependency>

Ivy:

    <depen dency org="io.reactivex" name="rxjava" rev="1.0.0" />
    
SBT:

    libraryDependencies += "com.netflix.rxjava" % "rxjava-scala" % "1.0.0"

Gradle:

    compile 'io.reactivex:rxjava:1.0.0'
    
如果需要直接下载jar,创建一个如下Maven pom文件:

    <?xml version="1.0"?>
    <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
        <modelVersion>4.0.0</modelVersion>
        <groupId>com.netflix.rxjava.download</groupId>
        <artifactId>rxjava-download</artifactId>
        <version>1.0-SNAPSHOT</version>
        <name>Simple POM to download rxjava and dependencies</name>
        <url>http://github.com/ReactiveX/RxJava</url>
        <dependencies>
            <dependency>
                <groupId>io.reactivex</groupId>
                <artifactId>rxjava</artifactId>
                <version>1.0.0</version>
                <scope/>
            </dependency>
        </dependencies>
    </project>
    
然后执行命令:

    $ mvn -f download-rxjava-pom.xml dependency:copy-dependencies
    
上面的命令会下载 rxjava-*.jar 以及依赖包到 ./target/dependency/. 目录下面

环境要求:java6及以上

##Building

检出源码:

    $ git clone git@github.com:ReactiveX/RxJava.git
    $ cd RxJava/
    $ ./gradlew build
    
开始一个clean build:

    $ ./gradlew clean build
    
一个成功的编译大致输出如下:

    $ ./gradlew build
    :rxjava:compileJava
    :rxjava:processResources UP-TO-DATE
    :rxjava:classes
    :rxjava:jar
    :rxjava:sourcesJar
    :rxjava:signArchives SKIPPED
    :rxjava:assemble
    :rxjava:licenseMain UP-TO-DATE
    :rxjava:licenseTest UP-TO-DATE
    :rxjava:compileTestJava
    :rxjava:processTestResources UP-TO-DATE
    :rxjava:testClasses
    :rxjava:test
    :rxjava:check
    :rxjava:build
    
    BUILD SUCCESSFUL
    
    Total time: 30.758 secs

clean build 会同时运行一个单元测试,你会看到如下内容:

    > Building > :rxjava:test > 91 tests completed

##Troubleshooting

    Could not resolve all dependencies for configuration ':language-adaptors:rxjava-scala:provided'

通过从.gradle/caches and .m2/repository/org/scala-lang/下面移除旧版本的scala-library,然后重新clean build即可

    Failed to apply plugin [id 'java'] Could not generate a proxy class for class nebula.core.NamedContainerProperOrder.

升级jdk,或者在build之前运行 export GRADLE_OPTS=-noverify
  
    
    
    
    