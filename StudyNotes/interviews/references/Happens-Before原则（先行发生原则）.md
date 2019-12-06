# Happens-Before原则（先行发生原则）

[![](https://upload.jianshu.io/users/upload_avatars/18653532/ede3a541-308c-4eee-81e6-1a00e54481b1.jpg?imageMogr2/auto-orient/strip|imageView2/1/w/96/h/96/format/webp)](https://www.jianshu.com/u/77c22c5a7dd1)

[dragonMen](https://www.jianshu.com/u/77c22c5a7dd1)关注

0.482019.07.10 14:32:50字数 2,275阅读 155

## Happens-Before

> 从jdk5开始，java使用新的JSR-133内存模型，基于Happens-Before的概念来阐述操作之间的内存可见性。

### Happens-Before定义

> 1. 如果一个操作Happens-Before另一个操作，那么第一个操作的执行结果将对第二个操作可见，而且第一个操作的执行顺序排在第二个操作之前。
> 2. 两个操作之间存在Happens-Before关系，并不意味着一定要按照Happens-Before原则制定的顺序来执行。如果重排序之后的执行结果与按照Happens-Before关系来执行的结果一致，那么这种重排序并不非法。

**注意**：不能将Happens-Before理解为它的字面意思，可以理解为“先行发生”，如A先行发生于B，就是说B执行之前，A产生的影响（修改共享变量、发送消息、调用方法等）可以被B观察到。（一团浆糊...继续挖）

### Happens-Before规则

Happens-Before的八个规则（摘自《深入理解Java虚拟机》12.3.6章节）：

1. 程序次序规则：一个线程内，按照代码顺序，书写在前面的操作先行发生于书写在后面的操作；
2. 管程锁定规则：一个unLock操作先行发生于后面对**同一个锁**的lock操作；（此处后面指时间的先后）
3. volatile变量规则：对一个变量的写操作先行发生于后面对这个变量的读操作；（此处后面指时间的先后）
4. 线程启动规则：Thread对象的start()方法先行发生于此线程的每个一个动作；
5. 线程终结规则：线程中所有的操作都先行发生于线程的终止检测，我们可以通过Thread.join()方法结束、Thread.isAlive()的返回值手段检测到线程已经终止执行；
6. 线程中断规则：对线程interrupt()方法的调用先行发生于被中断线程的代码检测到中断事件的发生；
7. 对象终结规则：一个对象的初始化完成先行发生于他的finalize()方法的开始；
8. 传递性：如果操作A先行发生于操作B，而操作B又先行发生于操作C，则可以得出操作A先行发生于操作C；

### Happens-Before规则详解

#### 程序次序规则

**同一个线程内，书写在前面的操作先行发生于书写在后面的操作**：在网上有看到过很多文章，但是实际编译时经过指令重排序，有些情况下书写在后面的代码会先于前面的代码。Happens-Before可以理解为前面代码的执行结果对于后面代码是可见的（...怎么说有点绕，看例子吧）。



```cpp
int a = 3;     //代码1
int b = a + 1; //代码2
```

上面的代码中，因为代码2的计算会用到代码1的运行结果，此时**程序次序规则**就会保证代码2中的a一定为3，不会是0(默认初始化的值)，所以JVM不允许操作系统对代码1、2进行重排序，即代码1一定在代码2之前执行。下面的例子就无法保证执行顺序：



```cpp
int a = 3; //代码1
int b = 2; //代码2
```

上面的代码中，代码1、2之间没有依赖关系，所以指令重排序有可能会发生，b的初始化可能比a早。

#### 管程锁定规则

一个unLock操作先行发生于后面对**同一个锁**的lock操作：同一个锁只能由一个线程持有，下面举例



```csharp
public class TestHappenBefore {
    public static int var;
    private static TestHappenBefore happenBefore = new TestHappenBefore();

    public static TestHappenBefore getInstance() {
        return happenBefore;
    }

    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> TestHappenBefore.getInstance().method2()).start();
        new Thread(() -> TestHappenBefore.getInstance().method1()).start();
        new Thread(() -> TestHappenBefore.getInstance().method3()).start();
    }

    public synchronized void method1() {
        var = 3;
        System.out.println("method1，var：" + var);
    }

    public synchronized void method2() {
        try {
            System.out.println("线程2开始睡觉了~");
            new Thread().sleep(5000);
            System.out.println("线程2睡好了~");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int b = var;
        System.out.println("method2，var：" + var + "，b：" + b);
    }

    public void method3() {
        synchronized (new TestHappenBefore()) { //换了把新锁
            var = 4;
            System.out.println("method3，var：" + var);
        }
    }
}
```



```csharp
执行结果：
线程2开始睡觉了~
method3，var：4
线程2睡好了~
method2，var：4，b：4
method1，var：3
```

通过上面的例子我们发现，当线程2在“睡觉”的时间段内，线程1并没有执行，因为此时happenBefore对象的锁被线程2持有，线程2释放锁之前，线程1无法持有该锁，这符合**管程锁定规则**，还发现线程2“睡觉”的时候，线程3并没有停下，仍然执行了自己的代码，是因为method3的锁和线程2不是同一把锁，所以不受**管程锁定规则**的限制。

#### volatile变量规则

对一个变量的写操作先行发生于后面对这个变量的读操作（此处后面指时间的先后）：这条规则保证了volatile变量的可见性，线程A写volatile变量后，线程B读volatile变量，则B读到的一定是A写的值，照旧举例（没有写出合适的案例，附上伪代码说明，如有合适的案例，请指教）：



```cpp
volatile int a;
//线程1执行内容
public void method1() {
    a = 1;
}
//线程2执行内容
public void method2() {
    int b = a;
}
```

如果线程1先执行，线程2再执行，则**volatile变量规则**可以保证线程2读取的变量a的值为1。

#### 传递性

如果操作A先行发生于操作B，而操作B又先行发生于操作C，则可以得出操作A先行发生于操作C（感觉类似数学的传递性：A>B,B>C则A>C...），照旧一例：



```csharp
volatile int var;
int b;
int c;
//线程1执行内容
public void method1() {
    b = 4; //1
    var = 3; //2
}
//线程2执行内容
public void method2() {
    c = var; //3
    c = b; //4
}
```

假设执行顺序为 1、2、3、4，由于单线程的**程序次序规则**，得出1 Happen Before 2，3 Happen Before 4，又因为**volatile变量规则**得出2 Happen Before 3，所以1 Happen Before 3，1 Happen Before 4（**传递性**），即最后变量c的值为4；若执行顺序为1、3、4、2，因为3、2没有匹配到Happen Before规则，所以无法通过**传递性**推测出传递关系，也就无法保证最后变量c的值为4，也可能为0（b初始化的值，没有读到线程1写入的值）

线程启动规则、线程终结规则、线程中断规则、对象终结规则四个规则相对比较易于理解，不再赘述。

### Happens-Before原则与时间顺序的关系

前面提到不可以将Happens-Before理解为它的字面意思，即不能站在时间顺序的角度去理解先行发生原则，通过下面的例子来验证一下：



```csharp
private int value = 0;
public void setValue(int value){
    this.value = value;
}
public void getValue(){
    return value;
}
```

假设线程A调用setValue(1)方法，线程B调用同对象的getValue()方法，线程A在时间上先执行，此时线程B调用方法的返回值是什么？
依次分析一下先行发生的八大原则：例子不在同一个线程内，故程序次序规则不适用；代码中没有同步块，所以管程锁定规则不适用；变量value没有被volatile关键字修饰，volatile变量规则同样不适用；线程启动规则、线程终结规则、线程中断规则、对象终结规则和本例没有关系。因为没有匹配到任何一条规则，所以传递性也不适用。通过执行结果（具有一定偶然性，实验时加大循环次数），我们会发现B的返回值有可能是1有可能是0，所以这个操作不是线程安全的。
解决方式有多种，例如：getter、setter方法加上synchronized同步块，就可以匹配上管程锁定规则；或者value变量用volatile关键字进行修饰，则可以匹配上volatile变量规则。
通过这个例子我们可以得出：“时间上的先发生”不代表这个操作是“先行发生”。
那“先行发生”的操作一定是“时间上的先发生”么？答案是否定的，最典型的例子就是我们常说的“指令重排序”，例子如下：



```cpp
// 同一线程内
int i=1;
int j=1;
```

上面代码运行情况符合**程序次序规则**，按规则应该是“int i = 1;”的操作先行发生于“int j = 2;”，但“int j = 2;”有可能会先被处理器执行，这并不影响先行发生原则的正确性，因为我们的线程无法感知这点。
通过上面的两个例子，我们得出：时间的先后顺序和先行发生原则（Happen-Before原则）基本没有关系，所以我们在排查线程安全问题的时候不要受到时间顺序的干扰，一切以先行发生原则（Happen-Before原则）为准（摘自《深入理解Java虚拟机》12.3.6章节）。