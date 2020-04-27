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

        //ʹ���̳߳ؽ����̹߳���
        ExecutorService es = Executors.newFixedThreadPool(10);
        //��ഴ��10���̣߳�ȷ���̲߳��ڿ�ʼʱ�ͱ�����

        
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
		// ���ݴ����ʵ������һ��δ������
		FutureTask<Integer> future = new FutureTask<Integer>(callable);
		// ��δ����������´������߳��У����������̴߳���
		new Thread(future).start();
		try {
			Integer result = future.get(); // ��ȡδ�������ִ�н��
			System.out.println(Thread.currentThread().getName() + "���̵߳�ִ�н��="+result);
		} catch (InterruptedException | ExecutionException e) {
			// get������һֱ�ȵ�δ�������ִ����ɣ����ڵȴ��ڼ�����յ��ж��źţ��������ò�׽�ж��쳣
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

    // ������Ʒ
    public void produce(List<T> product) {
        lock.lock();
        try {
            while (capacity == products.size()) {
                //����־��Ŀ���Ǹ��õĹ۲������ߺ�������״̬
                System.out.println("���棺�߳�(" + Thread.currentThread().getName() + ")׼��������Ʒ������Ʒ������");
                try {
   //                 canProduce.await();
					notifyAll();// �����������߳� ������Ʒ
                    wait();// �������߳����ߣ�ֹͣ����
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
                    //canConsume.await();
					notifyAll();// �����������߳� ������Ʒ
                    wait();// �������߳����ߣ�ֹͣ����
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

// ������
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
		// ���´�ӡ�������־��������ǰʱ�䡢��ǰ�̡߳������ֵ����Ϣ
		System.out.println(Thread.currentThread().getName() + "�������ɵ������="+random);
		return random;
    }

}