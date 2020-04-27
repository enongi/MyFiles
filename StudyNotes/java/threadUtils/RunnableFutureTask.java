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