/*
 * DadosUsuario.java
 *
 * Created on 14 de Outubro de 2007, 08:30
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.neodatis.odb.test.list.update;

import java.util.List;

/**
 * 
 * @author gusto
 */
public class DadosUsuario {
	private String oid;
	private String login;
	private String nome;
	private String email;
	private String senha;
	private String confirmaSenha;
	private List publicados;

	/** Creates a new instance of DadosUsuario */
	public DadosUsuario() {

	}

	public DadosUsuario(String login, String senha) {
		this.login = login;
		this.senha = senha;
	}

	public DadosUsuario(String login, String senha, String email, String confirmaSenha) {
		this.login = login;
		this.senha = senha;
		this.confirmaSenha = confirmaSenha;
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getConfirmaSenha() {
		return confirmaSenha;
	}

	public void setConfirmaSenha(String confirmaSenha) {
		this.confirmaSenha = confirmaSenha;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public List getPublicados() {
		return publicados;
	}

	public void setPublicados(List publicados) {
		this.publicados = publicados;
	}

}
