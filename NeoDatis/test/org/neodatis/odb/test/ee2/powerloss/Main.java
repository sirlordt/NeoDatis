/**
 * 
 */
package org.neodatis.odb.test.ee2.powerloss;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.impl.core.layers.layer3.engine.AbstractObjectWriter;
import org.neodatis.odb.impl.core.layers.layer3.engine.DefaultByteArrayConverter;
import org.neodatis.odb.impl.tool.LogUtil;
import org.neodatis.tool.DLogger;
import org.neodatis.tool.ILogger;

/**
 * @author olivier
 * 
 */
public class Main {
	public static ILogger logger = new Log4JLogger();

	private static class Data {
		String text = "test";
	}

	private void initDB() {
		ODB db = ODBFactory.open("test.db");
		while (db.getObjects(Data.class).size() < 1)
			db.store(new Data());
		db.close();
	}

	public static void main(String[] args) throws InterruptedException,
			IOException {
		DLogger.register(logger);
		//OdbConfiguration.setDebugEnabled(true);
		//LogUtil.allOn(true);
		//LogUtil.enable(AbstractObjectWriter.LOG_ID);
		Main main = new Main();
		main.run2();
	}

	public void run() {
		initDB();
		while (true) {
			ODB db = ODBFactory.open("test.db");
			try {
				for (final Data d : db.getObjects(Data.class).toArray(
						new Data[0])) {
					db.store(d);
					db.commit();
					System.out.println("size="
							+ db.getObjects(Data.class).size());
				}
			} finally {
				db.close();
			}
		}
	}

	public void run2() throws IOException {
		initDB();
		while (true) {
			logger.info("==> Opening database");
			ODB db = ODBFactory.open("test.db");
			Data data = ((Data) db.getObjects(Data.class).getFirst());
			logger.info("==> After get");
			data.text = "";
			for (int i = 0; i < 5; ++i)
				data.text += Integer.valueOf((int) (Math.random() * 10))
						.toString();
			logger.info("==> After setting data");
			db.store(data);
			logger.info("==> After store");
			db.defragmentTo("target.db");
			logger.info("==> After defragment");
			db.close();
			logger.info("==> After close (defragment");
			if (!new File("test.db").delete())
				throw new IOException("Delete failed");
			if (!new File("target.db").renameTo(new File("test.db")))
				throw new IOException("Rename failed");
			logger.info("==> Ending wile");
		}
	}

	public void run3() throws IOException {
		RandomAccessFile raf = null;
		int j = 0;
		DefaultByteArrayConverter c = new DefaultByteArrayConverter();
		while (true) {
			raf = new RandomAccessFile("test.neodatis", "rw");
			raf.seek(0);
			//raf.getChannel().force(true);
			for (int i = 0; i < 10; i++) {
				byte[] bb = c.intToByteArray(j);
				raf.write(bb);
				j++;
				logger.info(j);
			}
			raf.getChannel().force(true);
			raf.close();
			
		}
	}
}
