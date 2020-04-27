import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.*;

public class CallableDemo {
    public static void main(String[] args) {
        /*ProductFactoryLock productFactory = new ProductFactoryLock(10);
        String str = "";

        //使用线程池进行线程管理
        ExecutorService es = Executors.newFixedThreadPool(10);
        //最多创建10个线程，确保线程不在开始时就被阻塞

        
        for (int n = 0; n < 100; n++) {
			System.out.println(n);
            ProducerLock producerLock = new ProducerLock(productFactory, "S" + n);
            FutureTask futureTask = new FutureTask(producerLock);
            while (n % 3 == 0) {
                es.submit(producerLock);
                //new Thread(futureTask).start();
            }
            while (n % 3 == 1) {
                es.submit(producerLock);
                //new Thread(futureTask).start();
            }
            while (n % 3 == 2) {
                es.submit(producerLock);
                //new Thread(futureTask).start();
            }
            try {
                Object po = futureTask.get();
                System.out.println(po);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        ConsumerLock consumerLock = new ConsumerLock(productFactory);
        FutureTask task = new FutureTask(consumerLock);
        es.submit(consumerLock);
        //new Thread(task).start();
        try {
            Object so = task.get();
            System.out.println(so);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        es.shutdown();*/


		Callable callable = new MyCallable();
		// 根据代码段实例创建一个未来任务
		FutureTask<Integer> future = new FutureTask<Integer>(callable);
		// 把未来任务放入新创建的线程中，并启动分线程处理
		new Thread(future).start();
		try {
			Integer result = future.get(); // 获取未来任务的执行结果
			System.out.println(Thread.currentThread().getName() + "主线程的执行结果="+result);
		} catch (InterruptedException | ExecutionException e) {
			// get方法会一直等到未来任务的执行完成，由于等待期间可能收到中断信号，因此这里得捕捉中断异常
			e.printStackTrace();
		}
    }

}

class ProductFactoryLock<T> {
    private LinkedBlockingQueue<List<T>> products;
    private int capacity = 0;
    private Lock lock = new ReentrantLock();
    private Condition canProduce = lock.newCondition();
    private Condition canConsume = lock.newCondition();

    public ProductFactoryLock(int capacity) {
        this.capacity = capacity;
        products = new LinkedBlockingQueue<>();
    }

    // 生产产品
    public void produce(List<T> product) {
        lock.lock();
        try {
            while (capacity == products.size()) {
                //打日志的目的是更好的观察消费者和生产者状态
                System.out.println("警告：线程(" + Thread.currentThread().getName() + ")准备生产产品，但产品池已满");
                try {
   //                 canProduce.await();
					notifyAll();// 唤醒消费者线程 生产商品
                    wait();// 生产者线程休眠，停止生产
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            products.offer(product);
            System.out.println("线程(" + Thread.currentThread().getName() + ")生产了一件产品:" + product + ";当前剩余商品" + products.size() + "个");
            canConsume.signal();
        } finally {
            lock.unlock();
        }

    }

    // 消费产品
    public synchronized List<T> consume() {
        lock.lock();
        try {
            while (products.size() == 0) {
                try {
                    //打日志的目的是更好的观察消费者和生产者状态
                    System.out.println("警告：线程(" + Thread.currentThread().getName() + ")准备消费产品，但当前没有产品");
                    //canConsume.await();
					notifyAll();// 唤醒生产者线程 生产商品
                    wait();// 消费者线程休眠，停止消费
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            List<T> product = products.poll();
            System.out.println("线程(" + Thread.currentThread().getName() + ")消费了一件产品:" + product + ";当前剩余商品" + products.size() + "个");
            canProduce.signal();
            return product;
        } finally {
            lock.unlock();
        }
    }
}

// 生产者
class ProducerLock<T> implements Callable {
    private ProductFactoryLock productFactory;
    private String str;

    public ProducerLock(ProductFactoryLock productFactory, String str) {
        this.productFactory = productFactory;
        this.str = str;
    }

//    public void run() {
//        List<String> list = new ArrayList<>();
//        list.add(str);
//        productFactory.produce(list);
//        try {
//            Thread.sleep(10);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public Object call() throws Exception {
        List<String> list = new ArrayList<>();
        list.add(str);
        productFactory.produce(list);
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return list;
    }
}

// 消费者
class ConsumerLock<T> implements Callable {
    private ProductFactoryLock productFactory;

    public ConsumerLock(ProductFactoryLock productFactory) {
        this.productFactory = productFactory;
    }

//    public void run() {
//        productFactory.consume();
//        try {
//            Thread.sleep(200);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public Object call() throws Exception {
        List consume = productFactory.consume();
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return consume;
    }
}



class MyCallable implements Callable
{
	 @Override
    public Integer call() throws Exception {
		int random = new Random().nextInt(100);
		// 以下打印随机数日志，包括当前时间、当前线程、随机数值等信息
		System.out.println(Thread.currentThread().getName() + "任务生成的随机数="+random);
		return random;
    }

}