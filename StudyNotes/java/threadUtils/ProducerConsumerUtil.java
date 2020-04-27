

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ProducerConsumerUtil<T> {
    public static void main(String[] args) {
        ProductFactoryLock productFactory = new ProductFactoryLock(10);
		String str = "";

        //使用线程池进行线程管理
        ExecutorService es = Executors.newFixedThreadPool(10);
        /**最多创建10个线程，确保线程不在开始时就被阻塞*/

		//new Thread(new ProducerLock(productFactory,"123"), "1号生产者").start();
        //new Thread(new ConsumerLock(productFactory), "1号消费者").start();
        //new Thread(new ConsumerLock(productFactory), "2号消费者").start();
		for(int n = 0; n < 100; n++){
			while (n % 3 == 0) {
				//es.submit(new ProducerLock(productFactory, n + ""));
				new Thread(new ProducerLock(productFactory,"S"+n), "1号生产者").start();
			}
			while (n % 3 == 1) {
				new Thread(new ProducerLock(productFactory,"S"+n), "2号生产者").start();
			}
			while (n % 3 == 2) {
				new Thread(new ProducerLock(productFactory,"S"+n), "3号生产者").start();
			}
		}
		//es.submit(new ConsumerLock(productFactory));
		new Thread(new ConsumerLock(productFactory), "1号消费者").start();
        

        es.shutdown();
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
                    canProduce.await();
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
                    canConsume.await();
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
class ProducerLock<T> implements Runnable {
    private ProductFactoryLock productFactory;
	private String str;

    public ProducerLock(ProductFactoryLock productFactory,String str) {
        this.productFactory = productFactory;
		this.str = str;
    }

    public void run() {
		List<String> list = new ArrayList<>();
		list.add(str);
		productFactory.produce(list);
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
}

// 消费者
class ConsumerLock<T> implements Runnable {
    private ProductFactoryLock productFactory;

    public ConsumerLock(ProductFactoryLock productFactory) {
        this.productFactory = productFactory;
    }

    public void run() {
		productFactory.consume();
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
}