class MyThread implements Runnable{ // ʵ��Runnable�ӿ� 
    public void run(){  // ��дrun()���� 
		System.out.println(Thread.currentThread().getName() + "��ʼ���С�") ; 

        while(true){ 
            System.out.println(Thread.currentThread().getName() + "�������С�") ; 
        } 
		//System.out.println(Thread.currentThread().getName() + "�������С�") ; 
    } 
}; 
public class ThreadDaemonDemo{ 
    public static void main(String args[]){ 
        MyThread mt = new MyThread() ;  // ʵ����Runnable������� 
        Thread t = new Thread(mt,"�߳�");     // ʵ����Thread���� 
        t.setDaemon(true) ; // ���߳��ں�̨���� 
        t.start() ; // �����߳� 
    } 
};