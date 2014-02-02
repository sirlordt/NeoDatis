package org.neodatis.odb.test.ee.fromusers.icosystem.neo.dto;

import java.util.ArrayList;
import java.util.List;

public class ComplexDTO implements Entity{
	private final long id;
	private final List<Entity> objects=new ArrayList<Entity>();

	public ComplexDTO(long id){
		this.id = id;
	}

	public long getId(){
		return id;
	}

	public void add(Entity entity){
		this.objects.add(entity);
	}
	
	public void clear() {
		objects.clear();
	}

	@Override
	public int hashCode(){
		return (int) id;
	}

	@Override
	public boolean equals(Object other){
		if(!(other instanceof ComplexDTO)){
			return false;
		}
		return id == ((ComplexDTO) other).id;
	}

	@Override
	public String toString(){
		return "Complex [id=" + id + ", objects=" + objects + "]";
	}
}
