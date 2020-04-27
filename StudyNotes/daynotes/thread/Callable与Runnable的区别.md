## Java线程——Callable与Runnable的区别

### Runnable

其中Runnable应该是我们最熟悉的接口，它只有一个run()函数，用于将耗时操作写在其中，该函数没有返回值。然后使用某个线程去执行该runnable即可实现多线程，Thread类在调用start()函数后就是执行的是Runnable的run()函数。Runnable的声明如下 : 

```
public interface Runnable {
    /*
     * @see     java.lang.Thread#run()
     */
    public abstract void run();
}
```

### Callable

Callable与Runnable的功能大致相似，Callable中有一个call()函数，但是call()函数有返回值，而Runnable的run()函数不能将结果返回给客户程序。Callable的声明如下 :

public interface Callable<V> {
    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    V call() throws Exception;
}
可以看到，这是一个泛型接口，call()函数返回的类型就是客户程序传递进来的V类型。

### Future

Executor就是Runnable和Callable的调度容器，Future就是对于具体的Runnable或者Callable任务的执行结果进行

取消、查询是否完成、获取结果、设置结果操作。get方法会阻塞，直到任务返回结果(Future简介)。Future声明如下 :

    public interface Future<V> {
        /**
         * Attempts to cancel execution of this task.  This attempt will
         * fail if the task has already completed, has already been cancelled,
         * or could not be cancelled for some other reason. If successful,
         * and this task has not started when <tt>cancel</tt> is called,
         * this task should never run.  If the task has already started,
         * then the <tt>mayInterruptIfRunning</tt> parameter determines
         * whether the thread executing this task should be interrupted in
         * an attempt to stop the task.
         */
        boolean cancel(boolean mayInterruptIfRunning);
    
        /**
         * Returns <tt>true</tt> if this task was cancelled before it completed
         * normally.
         */
        boolean isCancelled();
    
        /**
         * Returns <tt>true</tt> if this task completed.
         *
         */
        boolean isDone();
    
        /**
         * Waits if necessary for the computation to complete, and then
         * retrieves its result.
         *
         * @return the computed result
         */
        V get() throws InterruptedException, ExecutionException;
    
        /**
         * Waits if necessary for at most the given time for the computation
         * to complete, and then retrieves its result, if available.
         *
         * @param timeout the maximum time to wait
         * @param unit the time unit of the timeout argument
         * @return the computed result
         */
        V get(long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException;
    }
### FutureTask（很有用）


FutureTask是一个RunnableFuture<V>

public class FutureTask<V> implements RunnableFuture<V>
RunnableFuture实现了Runnbale又实现了Futrue<V>这两个接口

```
public interface RunnableFuture<V> extends Runnable, Future<V> {
    /**
     * Sets this Future to the result of its computation
     * unless it has been cancelled.
     */
    void run();
}
```


另外FutureTaslk还可以包装Runnable和Callable<V>， 由构造函数注入依赖。

    public FutureTask(Callable<V> callable) {
        if (callable == null)
            throw new NullPointerException();
        this.callable = callable;
        this.state = NEW;       // ensure visibility of callable
    }
     
    public FutureTask(Runnable runnable, V result) {
        this.callable = Executors.callable(runnable, result);
        this.state = NEW;       // ensure visibility of callable
    }
上面代码块可以看出：Runnable注入会被Executors.callable()函数转换为Callable类型，即FutureTask最终都是执行Callable类型的任务。该适配函数的实现如下 ：

    public static <T> Callable<T> callable(Runnable task, T result) {
        if (task == null)
            throw new NullPointerException();
        return new RunnableAdapter<T>(task, result);
    }
RunnableAdapter适配器

    /**
     * A callable that runs given task and returns given result
     */
    static final class RunnableAdapter<T> implements Callable<T> {
        final Runnable task;
        final T result;
        RunnableAdapter(Runnable task, T result) {
            this.task = task;
            this.result = result;
        }
        public T call() {
            task.run();
            return result;
        }
    }
FutureTask实现Runnable，所以能通过Thread包装执行，

FutureTask实现Runnable，所以能通过提交给ExcecuteService来执行

注：ExecuteService：创建线程池实例对象，其中有submit（Runnable）、submit（Callable）方法

ExecturService：https://blog.csdn.net/suifeng3051/article/details/49443835

还可以直接通过get()函数获取执行结果，该函数会阻塞，直到结果返回。

因此FutureTask是Future也是Runnable，又是包装了的Callable( 如果是Runnable最终也会被转换为Callable )。

Callable 和 Future接口的区别

 Callable规定的方法是call()，而Runnable规定的方法是run(). 
 Callable的任务执行后可返回值，而Runnable的任务是不能返回值的。  
  call()方法可抛出异常，而run()方法是不能抛出异常的。 
  运行Callable任务可拿到一个Future对象， Future表示异步计算的结果。 
  它提供了检查计算是否完成的方法，以等待计算的完成，并检索计算的结果。 
  通过Future对象可了解任务执行情况，可取消任务的执行，还可获取任务执行的结果。 
 Callable是类似于Runnable的接口，实现Callable接口的类和实现Runnable的类都是可被其它线程执行的任务。
示例：


```java
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class RunnableFutureTask {
	static ExecutorService executorService = Executors.newSingleThreadExecutor();	//创建一个单线程执行器
	public static void main(String[] args) {
		runnableDemo();	
		futureDemo();
	}
	/**
	 * new Thread(Runnable arg0).start(); 用Thread()方法开启一个新线程
	 * runnable, 无返回值
	 */
	static void runnableDemo() {
		new Thread(new Runnable() {				
			public void run() {
				System.out.println("runnable demo:" + fibc(20));	//有值
			}
			
	}).start();
}
/**
 * Runnable实现的是void run()方法，无返回值
 * Callable实现的是 V call()方法，并且可以返回执行结果
 * Runnable可以提交给Thread,在包装下直接启动一个线程来执行
 * Callable一般都是提交给ExecuteService来执行
 */ 
 
static void futureDemo() {
	try {
		Future<?> result1 = executorService.submit(new Runnable() {
			public void run() {
				fibc(20);
			}
		});
		System.out.println("future result from runnable:"+result1.get());	//run()无返回值所以为空，result1.get()方法会阻塞
		Future<Integer> result2 = executorService.submit(new Callable<Integer>()	 {
			public Integer call() throws Exception {
				return fibc(20);	
			}
		});
		System.out.println("future result from callable:"+result2.get());	//call()有返回值，result2.get()方法会阻塞
		FutureTask<Integer> result3 = new FutureTask<Integer>(new Callable<Integer>() {
			public Integer call() throws Exception {
				return fibc(20);
			}
		});
		executorService.submit(result3);	
		System.out.println("future result from FutureTask:" + result3.get());	//call()有返回值，result3.get()方法会阻塞
		
		/*因为FutureTask实现了Runnable，因此它既可以通过Thread包装来直接执行，也可以提交给ExecuteService来执行*/
		FutureTask<Integer> result4 = new FutureTask<Integer>(new Runnable() {
			public void run() {
				fibc(20);
			}
		},fibc(20));	
		executorService.submit(result4);
		System.out.println("future result from executeService FutureTask :" + result4.get());	//call()有返回值，result3.get()方法会阻塞
		//这里解释一下什么FutureTask实现了Runnable结果不为null，这就用到FutureTask对Runnable的包装，所以Runnable注入会被Executors.callable()函数转换成Callable类型
 
		FutureTask<Integer> result5 = new FutureTask<Integer>(new Runnable() {
			public void run() {
				fibc(20);
			}
		},fibc(20));
		new Thread(result5).start();
		System.out.println("future result from Thread FutureTask :" + result5.get());	//call()有返回值，result5.get()方法会阻塞
		
	} catch (Exception e) {
		e.printStackTrace();
	}finally {
		executorService.shutdown();
	}
}
static int fibc(int num) {
	if (num==0) {
		return 0;
	}
	if (num==1) {
		return 1;
	}
	return fibc(num-1) + fibc(num-2);
}
}
```

运行结果：

runnable demo:6765
future result from runnable:null
future result from callable:6765
future result from FutureTask:6765
future result from executeService FutureTask :6765
future result from Thread FutureTask :6765

下面是关系图，望有助理解

![](C:\Users\lizhenzhong\Desktop\MyFiles\StudyNotes\images\thread002.png)


![](C:\Users\lizhenzhong\Desktop\MyFiles\StudyNotes\images\thread003.png)