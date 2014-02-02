
package org.neodatis.odb.test.ee.fromusers.icosystem;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @version $Id: ExampleDTO.java,v 1.1 2009/09/03 20:42:34 olivier_smadja Exp $
 * @author ashton
 */
public class ExampleDTO implements Comparable<ExampleDTO>{
	private static final long serialVersionUID = 1L;
	private static final long NEW_OBJECT_ID = -1L;

	private final long id;
	private final int hashCode;
	private String name;
	private Set<Long> ls = new TreeSet<Long>();
	private Class<?> clx;

	public ExampleDTO(){
		this(NEW_OBJECT_ID);
	}

	public ExampleDTO(long id){
		this(id, "ExampleDTO [" + id + ']');
	}

	public ExampleDTO(long id,String name){
		this.id = id;
		this.hashCode = (int) id;
		this.name = name;
	}

	public long getId(){
		return id;
	}

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getNumberList(){
		return toString();
	}

	public Class<?> getClx(){
		return clx;
	}

	public Set<Long> getLs(){
		return ls;
	}

	public void setLs(Set<Long> ls){
		this.ls = ls;
	}

	public void generateData(){
		ls.add((long) (Math.random() * 1000L));
		ls.add((long) (Math.random() * 1000L));
		ls.add((long) (Math.random() * 1000L));
		if(id % 2 == 1){
			clx = ArrayList.class;
		}else{
			clx = LinkedHashSet.class;
		}
	}

	@Override
	public int hashCode(){
		return hashCode;
	}

	@Override
	public boolean equals(Object other){
		if(!(other instanceof ExampleDTO)){
			return false;
		}
		return id == ((ExampleDTO) other).id;
	}

	public void initializeWith(ExampleDTO template){
		this.name = template.name;
		this.ls.clear();
		this.ls.addAll(template.ls);
	}

	public int compareTo(ExampleDTO o){
		return name.compareTo(o.getName());
	}

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("NDTO [").append(id).append("] Name [").append(name).append("] ");
		for(Long lx : ls){
			sb.append(lx);
			sb.append(',');
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append("] Class [").append(clx != null ? clx.getName() : "No Class").append(']');
		return sb.toString();
	}
}
