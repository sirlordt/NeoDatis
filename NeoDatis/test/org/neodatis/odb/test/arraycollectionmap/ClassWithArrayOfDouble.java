package org.neodatis.odb.test.arraycollectionmap;

public class ClassWithArrayOfDouble {
	protected String name;
	protected Double[] doubles;
	
	public ClassWithArrayOfDouble(String name){
		this.name = name;
		doubles = new Double[10];
	}

	public void setDouble(int index, Double value){
		doubles[index] = value;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double[] getDoubles() {
		return doubles;
	}

	public void setDoubles(Double[] doubles) {
		this.doubles = doubles;
	}
	

}
