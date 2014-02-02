package org.neodatis.odb.test.ee.fromusers.icosystem.neo.dto;


public class ParentDTO implements Entity{
	private final long id;
	private SimpleDTO simpleDTO;
	private SubClassWithList withSubClass;

	public ParentDTO(long id){
		this.id = id;
	}

	public long getId(){
		return id;
	}

	public SimpleDTO getSimpleDTO(){
		return simpleDTO;
	}

	public void setSimpleDTO(SimpleDTO simpleDTO){
		this.simpleDTO = simpleDTO;
	}

	public SubClassWithList getWithSubClass(){
		return withSubClass;
	}

	public void setWithSubClass(SubClassWithList withSubClass){
		this.withSubClass = withSubClass;
	}

	@Override
	public int hashCode(){
		return (int) id;
	}

	@Override
	public boolean equals(Object other){
		if(!(other instanceof ParentDTO)){
			return false;
		}
		return id == ((ParentDTO) other).id;
	}

	@Override
	public String toString(){
		return "ParentDTO ["+id+"] [simpleDTO=" + simpleDTO + ", withSubClass=" + withSubClass + "]";
	}
}
