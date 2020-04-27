
import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
public class ThreadOrderlyDemo{
    public static void main(String[] args){
//       ExecutorService executorService = Executors.newFixedThreadPool(10);
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 10,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
        CountDownLatch countDownLatch = new CountDownLatch(1000); //����ʱ���������
        ArrayList<Future> futures = new ArrayList<>();

        for (int i = 0; i < 1000; i++) {
            final int finalI = i;
            Future<?> future = threadPoolExecutor.submit((Runnable) ()->{

                try {
                    System.out.println("###########corePoolSize: " + threadPoolExecutor.getCorePoolSize());
                    System.out.println("###########taskCount: " + threadPoolExecutor.getTaskCount());
                    System.out.println("###########i: " + finalI);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
//                    countDownLatch.countDown();  //��������Ƿ��쳣����Ҫ������,����ᱻ�����޷�����
                }
            });
            futures.add(future);
        }


//        try {
//            countDownLatch.await(); //��֤�̳߳��е����е���������ɺ����̲߳Ż��������ִ�У�
//     
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        
//���淽��Ҳ���� ��֤"�̳߳��е����е���������ɺ����̲߳Ż��������ִ��"
        AtomicInteger atomicInteger = new AtomicInteger(0);
        while (true) {
            atomicInteger.getAndIncrement();
            System.out.println("atomicInteger: " + atomicInteger);
            boolean isAllTheadDone = true;

            for (Future future : futures) {
                if (!future.isDone()) {
                    isAllTheadDone = false;
                }
            }

            try {
                Thread.sleep(10L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (isAllTheadDone) {
				System.out.println(isAllTheadDone);
                break;
            }
        }



//		System.out.println(countDownLatch);

        System.out.println("game over");
    }

};