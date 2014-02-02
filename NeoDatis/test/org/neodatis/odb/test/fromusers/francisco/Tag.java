package org.neodatis.odb.test.fromusers.francisco;

import java.io.Serializable;

import org.neodatis.odb.impl.tool.UUID;

public class Tag implements Serializable {

	private Long id = UUID.getRandomLongId();
	private String name;
	private transient Long weight;

	public Tag(String name) {
		this.name = name;
	}

	public Tag(String name, Long weight) {
		this.name = name;
		this.weight = weight;
	}

	public Long getId() {
		return id;
	}

	protected void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getWeight() {
		return weight;
	}

	public void setWeight(Long weight) {
		this.weight = weight;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tag other = (Tag) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}