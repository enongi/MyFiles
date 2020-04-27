## Java多线程面试题之如何让主线程等子线程执行完之后再执行

问题描述
现在有一个主线程X，和两个子线程A和B，A和B之间没有依赖关系且两者的执行时间不确定，现在要求如下：
1：不限制A和B执行顺序的
2：主线程X需要在子线程A和B执行完成之后再执行

### 方案1

1）思路
使用join()方法实现，join()的作用是让父线程等待子线程结束后再执行。
join源码如下：

```
public final void join() throws InterruptedException {
	//直接调用含参的同名方法
    join(0);
}
public final synchronized void join(long millis) throws InterruptedException {
    long base = System.currentTimeMillis();
    long now = 0;
    if (millis < 0) {
        throw new IllegalArgumentException("timeout value is negative");
    }
    //满足当前条件
    if (millis == 0) {
    	//调用native方法isAlive(),作用是判断【当前线程】是否处于活动状态，cpu运行状态
        while (isAlive()) {
        	//让【当前线程】让出cpu资源，进入等待唤醒状态
            wait(0);
        }
    } else {
        while (isAlive()) {
            long delay = millis - now;
            if (delay <= 0) {
                break;
            }
            wait(delay);
            now = System.currentTimeMillis() - base;
        }
    }
}
```

 
大家此处是不是有个疑问呢？
我们是要主线程等子线程执行完，现在我们调用的是子线程的wait()方法，怎么理解？
答疑：
此处的关键点是要理解当前线程，即cpu正在运行的线程，虽然是通过子线程调用的wait()方法，但是也是通过主线的去调用的，此时当前线程实际还是我们的主线程，而wait()方法的作用的使当前线程让出cpu资源，所有此处是让主线程进入等待唤醒状态。

大家此处是不是有个疑问呢？
我们是要主线程等子线程执行完，现在我们调用的是子线程的wait()方法，怎么理解？
答疑：
此处的关键点是要理解当前线程，即cpu正在运行的线程，虽然是通过子线程调用的wait()方法，但是也是通过主线的去调用的，此时当前线程实际还是我们的主线程，而wait()方法的作用的使当前线程让出cpu资源，所有此处是让主线程进入等待唤醒状态。

通过源码我们知道了join()方法底层还是通过wait()方法实现的。

2）代码实现
先定义一个线程A，代码如下：

```
public class ThreadA implements Runnable {`
    public void run() {
        System.out.println("线程A开始执行");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("线程A异常了");
        }
        System.out.println("线程A执行结束");
    }
}
```


在定义线程B，代码如下：

在定义线程B，代码如下：

    public class ThreadB implements Runnable {
        public void run() {
            System.out.println("线程B开始执行");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println("线程B出异常了");
            }
            System.out.println("线程B执行结束");
    	}
    }
最后写一个线程X，代码如下



    public class ThreadX {
       public static void main(String[] args) throws InterruptedException {
            //主线程X
            Thread threadX = new Thread(new Runnable() {
                public void run() {
                    System.out.println("主线程X开始执行");
                    //子线程A
                    Thread threadA = new Thread(new ThreadA());
                    //子线程B
                    Thread threadB = new Thread(new ThreadB());
                    threadA.start();
                    threadB.start();
                    try {
                        threadA.join();
                        threadB.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("主线程X执行结束");
                }
            });
            threadX.start();
        }
    }

运行主线程X，输出结果如下：

主线程X开始执行
线程A开始执行
线程B开始执行
线程A执行结束
线程B执行结束
主线程X执行结束

从输出结果可以看出来我们的方案是可行的。

3）去掉join()测试
我们可以将线程B的join()方法去掉再看下执行顺序，线程A我设置的是休眠1s，线程B我设置的是休眠2s，这样能更清楚的看到执行结果。

    public class ThreadX {
        public static void main(String[] args) throws InterruptedException {
            //主线程X
            Thread threadX = new Thread(new Runnable() {
                public void run() {
                    System.out.println("主线程X开始执行");
                    //子线程A
                    Thread threadA = new Thread(new ThreadA());
                    //子线程B
                    Thread threadB = new Thread(new ThreadB());
                    threadA.start();
                    threadB.start();
                    try {
                        threadA.join();
                        //我们将线程B的join()方法注释掉测试看看
                        //threadB.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("主线程X执行结束");
                }
            });
            threadX.start();
        }
    }


运行主线程X，输出结果如下：

主线程X开始执行
线程A开始执行
线程B开始执行
线程A执行结束
主线程X执行结束
线程B执行结束

可以看到主线程X没有等线程B执行完就结束了

### 方案2

1）思路
使用countDownLatch实现

2）代码实现（错误案例）

    import java.util.concurrent.CountDownLatch;
    
    public class CountDownLatchThread implements Runnable {
        private String threadName;
    
        private CountDownLatch latch;
    
        public CountDownLatchThread(String threadName, CountDownLatch latch) {
            this.threadName = threadName;
            this.latch = latch;
        }
    
        public void run() {
            System.out.println("线程" + threadName + "开始工作");
            try {
                //模拟线程工作
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            latch.countDown();
            System.out.println("线程" + threadName + "完成工作");
        }
    
        public static void main(String[] args) {
            //创建主线程
            Thread threadX = new Thread(new Runnable() {
                public void run() {
                    CountDownLatch latch = new CountDownLatch(2);
                    System.out.println("主线程需要等待子线程执行完成后再执行");
                    Thread threadA = new Thread(new CountDownLatchThread("A", latch));
                    Thread threadB = new Thread(new CountDownLatchThread("B", latch));
                    threadA.start();
                    threadB.start();
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("子线程执行完成了，主线程继续执行");
                }
            });
            //启动主线程X
            threadX.start();
        }
    }

执行结果如下：

主线程需要等待子线程执行完成后再执行
线程A开始工作
线程B开始工作
线程B完成工作
线程A完成工作
子线程执行完成了，主线程继续执行

Process finished with exit code 0
3）此方案验证失败了，执行过程中发现会出现主线程早于其中一个子线程结束之前结果，多运行几次发现会有如下结果：
主线程需要等待子线程执行完成后再执行
线程A开始工作
线程B开始工作
线程B完成工作
子线程执行完成了，主线程继续执行
线程A完成工作

4）错误案例剖析
子线程的run方法中调用latch.countDown(); 应该放到方法的最后执行，我放的前面了。具体可以参考前面的代码

5）正确代码示例

    import java.util.concurrent.CountDownLatch;
    
    public class CountDownLatchThread implements Runnable {
        private String threadName;
    
        private CountDownLatch latch;
    
        public CountDownLatchThread(String threadName, CountDownLatch latch) {
            this.threadName = threadName;
            this.latch = latch;
        }
    
        public void run() {
            System.out.println("线程" + threadName + "开始工作");
            try {
                //模拟线程工作
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("线程" + threadName + "完成工作");
            latch.countDown();
        }
    
        public static void main(String[] args) throws InterruptedException {
            //直接使用main线程作为主线程
            //        CountDownLatch latch = new CountDownLatch(2);
        //        System.out.println("主线程需要等待子线程执行完成后再执行");
        //        Thread threadA = new Thread(new CountDownLatchThread("A", latch));
        //        Thread threadB = new Thread(new CountDownLatchThread("B", latch));
        //        threadA.start();
        //        threadB.start();
        //        latch.await();
        //        System.out.println("子线程执行完成了，主线程继续执行");
         //创建主线程X
            Thread threadX = new Thread(new Runnable() {
                CountDownLatch latch = new CountDownLatch(2);
    
                public void run() {
                    System.out.println("主线程需要等待子线程执行完成后再执行");
                    Thread threadA = new Thread(new CountDownLatchThread("A", latch));
                    Thread threadB = new Thread(new CountDownLatchThread("B", latch));
                    threadA.start();
                    threadB.start();
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("子线程执行完成了，主线程继续执行");
                }
            });
            //启动主线程X
            threadX.start();
        }
    }
执行结果如下：

主线程需要等待子线程执行完成后再执行
线程A开始工作
线程B开始工作
线程B完成工作
线程A完成工作
子线程执行完成了，主线程继续执行

Process finished with exit code 0

### 方案3

1）思路
利用callable和FutureTask可以获取到子线程的执行结果或返回值。

2）代码实现

    import java.util.concurrent.Callable;
    import java.util.concurrent.ExecutionException;
    import java.util.concurrent.FutureTask;
    
    public class FutureTaskThread implements Callable {
        private String threadName;
        public FutureTaskThread(String threadName) {
            this.threadName = threadName;
        }
    
        public Object call() throws Exception {
            Thread.sleep(3000);
            return "线程" + threadName + "执行结束了";
        }
    
        public static void main(String[] args) throws ExecutionException, InterruptedException {
            System.out.println("主线程开始等待子线程执行完成");
            FutureTask taskA = new FutureTask(new FutureTaskThread("A"));
            FutureTask taskB = new FutureTask(new FutureTaskThread("B"));
            new Thread(taskA).start();
            new Thread(taskB).start();
            //判断子线程A是否执行结束
            if (!taskA.isDone()) {
                System.out.println("线程A未执行完，主线程继续等待");
            }
            //判断子线程B是否执行结束
            if (!taskB.isDone()) {
                System.out.println("线程B未执行完，主线程继续等待");
            }
            //打印一下线程A的返回值
            System.out.println(taskA.get());
            //打印一下线程B的返回值
            System.out.println(taskB.get());
            System.out.println("子线程执行完成了，主线程开始执行了");
        }
    }


执行结果如下：

主线程开始等待子线程执行完成
线程A未执行完，主线程继续等待
线程B未执行完，主线程继续等待
线程A执行结束了
线程B执行结束了
子线程执行完成了，主线程开始执行了

Process finished with exit code 0

