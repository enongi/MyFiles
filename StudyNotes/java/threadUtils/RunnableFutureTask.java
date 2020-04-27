import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
 
public class RunnableFutureTask {
	static ExecutorService executorService = Executors.newSingleThreadExecutor();	//����һ�����߳�ִ����
	public static void main(String[] args) {
		runnableDemo();	
		futureDemo();
	}
	/**
	 * new Thread(Runnable arg0).start(); ��Thread()��������һ�����߳�
	 * runnable, �޷���ֵ
	 */
	static void runnableDemo() {
		new Thread(new Runnable() {				
			public void run() {
				System.out.println("runnable demo:" + fibc(20));	//��ֵ
			}
			
		}).start();
	}
	/**
	 * Runnableʵ�ֵ���void run()�������޷���ֵ
	 * Callableʵ�ֵ��� V call()���������ҿ��Է���ִ�н��
	 * Runnable�����ύ��Thread,�ڰ�װ��ֱ������һ���߳���ִ��
	 * Callableһ�㶼���ύ��ExecuteService��ִ��
	 */ 
	 
	static void futureDemo() {
		try {
			Future<?> result1 = executorService.submit(new Runnable() {
				public void run() {
					fibc(20);
				}
			});
			System.out.println("future result from runnable:"+result1.get());	//run()�޷���ֵ����Ϊ�գ�result1.get()����������
			Future<Integer> result2 = executorService.submit(new Callable<Integer>()	 {
				public Integer call() throws Exception {
					return fibc(20);	
				}
			});
			System.out.println("future result from callable:"+result2.get());	//call()�з���ֵ��result2.get()����������
			FutureTask<Integer> result3 = new FutureTask<Integer>(new Callable<Integer>() {
				public Integer call() throws Exception {
					return fibc(20);
				}
			});
			executorService.submit(result3);	
			System.out.println("future result from FutureTask:" + result3.get());	//call()�з���ֵ��result3.get()����������
			
			/*��ΪFutureTaskʵ����Runnable��������ȿ���ͨ��Thread��װ��ֱ��ִ�У�Ҳ�����ύ��ExecuteService��ִ��*/
			FutureTask<Integer> result4 = new FutureTask<Integer>(new Runnable() {
				public void run() {
					fibc(20);
				}
			},fibc(20));	
			executorService.submit(result4);
			System.out.println("future result from executeService FutureTask :" + result4.get());	//call()�з���ֵ��result3.get()����������
			//�������һ��ʲôFutureTaskʵ����Runnable�����Ϊnull������õ�FutureTask��Runnable�İ�װ������Runnableע��ᱻExecutors.callable()����ת����Callable����
 
			FutureTask<Integer> result5 = new FutureTask<Integer>(new Runnable() {
				public void run() {
					fibc(20);
				}
			},fibc(20));
			new Thread(result5).start();
			System.out.println("future result from Thread FutureTask :" + result5.get());	//call()�з���ֵ��result5.get()����������
			
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