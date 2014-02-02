package org.neodatis.tool.mutex;

import org.neodatis.odb.test.ODBTest;

public class TestMutex extends ODBTest {

	public void test1() throws InterruptedException {
		Mutex mutex = null;

		mutex = MutexFactory.get("ola").acquire("1");
		println("ok - mutex 1");
		boolean b = MutexFactory.get("ola").attempt(1000);
		assertFalse(b);
		mutex.release("1");

	}

	public void test11() throws InterruptedException {
		Mutex mutex = null;

		mutex = MutexFactory.get("ola");
		boolean b = mutex.attempt(1000);
		assertTrue(b);
		b = mutex.attempt(500);
		assertFalse(b);
		mutex.release("1");

	}

	public void test2() throws InterruptedException {
		Mutex mutex = null;
		int nb = 1;
		ThreadGroup threadGroup = new ThreadGroup("test");
		Thread[] threads = new Thread[nb];
		for (int i = 0; i < nb; i++) {
			Thread thread1 = new MyThread(threadGroup, (i + 1), "oli");
			thread1.start();
			threads[i] = thread1;
		}
		for (int i = 0; i < threads.length; i++) {
			threads[i].join();
		}
		assertEquals(nb, MyThread.nbs);
		assertEquals(1, MyThread.max);

	}

	public void setUp() {
		// MutexFactory.setDebug(true);
	}
}

class MyThread extends Thread {
	public static int max = 0;
	public static int nb = 0;
	public static int nbs = 0;
	private String mutexName;
	private int index;

	public MyThread(ThreadGroup group, int index, String name) {
		super(group, "i=" + index);
		this.mutexName = name;
		this.index = index;
	}

	public String getMutexName() {
		return mutexName;
	}

	public void setMutexName(String name) {
		this.mutexName = name;
	}

	public void run() {
		Mutex mutex = null;
		try {
			// println("-Getting mutex - index "+index);
			mutex = MutexFactory.get(mutexName).acquire("1");
			// println("- 1 - Got mutex - index "+index+" - owners="+mutex.getNbOwners());
			nb++;
			nbs++;
			Thread.sleep(10);
			if (nb > max) {
				max = nb;
			}
			// println("- 2 - Got mutex - index "+index +
			// " - nb="+nb+" - max="+max + " - nbs="+nbs);

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (mutex != null) {
				mutex.release("" + index);
			}
			nb--;
			// println("- Released mutex - index "+index);
		}
	}

}