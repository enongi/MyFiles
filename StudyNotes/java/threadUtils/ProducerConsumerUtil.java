

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

        //ʹ���̳߳ؽ����̹߳���
        ExecutorService es = Executors.newFixedThreadPool(10);
        /**��ഴ��10���̣߳�ȷ���̲߳��ڿ�ʼʱ�ͱ�����*/

		//new Thread(new ProducerLock(productFactory,"123"), "1��������").start();
        //new Thread(new ConsumerLock(productFactory), "1��������").start();
        //new Thread(new ConsumerLock(productFactory), "2��������").start();
		for(int n = 0; n < 100; n++){
			while (n % 3 == 0) {
				//es.submit(new ProducerLock(productFactory, n + ""));
				new Thread(new ProducerLock(productFactory,"S"+n), "1��������").start();
			}
			while (n % 3 == 1) {
				new Thread(new ProducerLock(productFactory,"S"+n), "2��������").start();
			}
			while (n % 3 == 2) {
				new Thread(new ProducerLock(productFactory,"S"+n), "3��������").start();
			}
		}
		//es.submit(new ConsumerLock(productFactory));
		new Thread(new ConsumerLock(productFactory), "1��������").start();
        

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

    // ������Ʒ
    public void produce(List<T> product) {
        lock.lock();
        try {
            while (capacity == products.size()) {
                //����־��Ŀ���Ǹ��õĹ۲������ߺ�������״̬
                System.out.println("���棺�߳�(" + Thread.currentThread().getName() + ")׼��������Ʒ������Ʒ������");
                try {
                    canProduce.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            products.offer(product);
            System.out.println("�߳�(" + Thread.currentThread().getName() + ")������һ����Ʒ:" + product + ";��ǰʣ����Ʒ" + products.size() + "��");
            canConsume.signal();
        } finally {
            lock.unlock();
        }

    }

    // ���Ѳ�Ʒ
    public synchronized List<T> consume() {
        lock.lock();
        try {
            while (products.size() == 0) {
                try {
                    //����־��Ŀ���Ǹ��õĹ۲������ߺ�������״̬
                    System.out.println("���棺�߳�(" + Thread.currentThread().getName() + ")׼�����Ѳ�Ʒ������ǰû�в�Ʒ");
                    canConsume.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            List<T> product = products.poll();
            System.out.println("�߳�(" + Thread.currentThread().getName() + ")������һ����Ʒ:" + product + ";��ǰʣ����Ʒ" + products.size() + "��");
            canProduce.signal();
            return product;
        } finally {
            lock.unlock();
        }
    }
}

// ������
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

// ������
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