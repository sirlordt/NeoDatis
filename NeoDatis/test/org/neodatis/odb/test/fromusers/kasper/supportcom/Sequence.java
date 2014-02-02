package org.neodatis.odb.test.fromusers.kasper.supportcom;

public class Sequence {
	 private Long id;
	    
	    private String className;

	    public Long getId() {
	            return id;
	    }

	    public void setId(Long id) {
	            this.id = id;
	    }

	    public String getClassName() {
	            return className;
	    }

	    public void setClassName(String className) {
	            this.className = className;
	    }
	    
	    public void increment() {
	            this.id++;
	    }


}
