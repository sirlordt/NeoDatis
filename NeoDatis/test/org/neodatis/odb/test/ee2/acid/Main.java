/**
 * 
 */
package org.neodatis.odb.test.ee2.acid;

import java.io.File;
import java.io.IOException;

import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;

/**
 * @author olivier
 *
 */
public class Main {
	 private static class Data
	  {
	    String text = "test";
	  }

	  private void initDB()
	  {
	    ODB db = ODBFactory.open("test.db");
	    while (db.getObjects(Data.class).size() < 1)
	      db.store(new Data());
	    db.close();
	  }

	  public static void main(String[] args) throws InterruptedException, IOException
	  {
	    Main main = new Main();
	    main.run2();
	  }

	  public void run()
	  {
	    initDB();
	    while (true)
	    {
	      ODB db = ODBFactory.open("test.db");
	      try
	      {
	        for (final Data d : db.getObjects(Data.class).toArray(new Data[0]))
	        {
	          db.store(d);
	          db.commit();
	          System.out.println("size=" + db.getObjects(Data.class).size());
	        }
	      }
	      finally
	      {
	        db.close();
	      }
	    }
	  }

	  public void run2() throws IOException
	  {
	    initDB();
	    int x=0;
	    while (true)
	    {
	      ODB db = ODBFactory.open("test.db");
	      Data data = ((Data) db.getObjects(Data.class).getFirst());
	      System.out.println("text: " + data.text);
	      data.text = "";
	      for (int i=0; i<5; ++i)
	        data.text += Integer.valueOf((int) (Math.random() * 10)).toString();
	      db.store(data);
	      db.defragmentTo("target.db");
	      
	      if(x==1000){
	    	  System.exit(1);
	      }
	      
	      db.close();
	      if (!new File("test.db").delete())
	          throw new IOException("Delete failed");
	      if (!new File("target.db").renameTo(new File("test.db")))
	        throw new IOException("Rename failed");
	      
	      x++;
	      
	    }
	  }

}
