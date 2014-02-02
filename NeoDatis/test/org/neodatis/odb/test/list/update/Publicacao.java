package org.neodatis.odb.test.list.update;

public class Publicacao {
	private String name;
	private String texto;

	public Publicacao(String name, String texto) {
		super();
		this.name = name;
		this.texto = texto;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public String toString() {
		return name + ":" + texto;
	}

}
