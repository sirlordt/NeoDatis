package org.neodatis.odb.test.server;

public class TestMemoryLeak {
	public static void main(String[] args) throws InterruptedException {
		String s = null;
		for (int i = 0; i < 10000000; i++) {
			s = "kdjfkldjfkdjkllkgdfklgklfdjglkdfjglkdfsjglkfdjlgjfdlgjkfldjglkfjdlkgjfdljfkdljgkljfglkjfkljfkljdfklgjflk" + i;
			if (i % 100000 == 0) {
				System.out.println(i);
			}
			Thread.sleep(100);
		}

	}
}

class MyMemory {
	private String name;

	public MyMemory(String name) {
		this.name = name;
	}
}