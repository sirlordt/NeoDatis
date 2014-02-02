/*
 NeoDatis ODB : Native Object Database (odb.info@neodatis.org)
 Copyright (C) 2007 NeoDatis Inc. http://www.neodatis.org

 "This file is part of the NeoDatis ODB open source object database".

 NeoDatis ODB is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 NeoDatis ODB is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.neodatis.odb.test.vo.crawler.jdk5;

import java.util.List;

/**
 * Created by IntelliJ IDEA. User: mayworm Date: Jan 22, 2006 Time: 3:49:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class Page {

	// lista de urls existentes na pagina
	protected List outputLinks;

	// Lista de metadados que foram obtidos, a partir do Dublin core
	protected List metadata;

	// conteudo da pagina sem links e figuras
	protected String content;

	// identificador unico gerado com o MD5 do conteudo
	protected long id;

	// Url da pagina
	protected String url;

	// data do primeiro download
	protected int firstFetch;

	// data do proximo fecth
	protected int nextFetch;

	// pontuacao da pagina a partir de um algotimo de rank
	protected float score;

	// caminho para o arquivo fisicamente armazenado
	protected String path;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public List getOutputLinks() {
		return outputLinks;
	}

	public void setOutputLinks(List outputLinks) {
		this.outputLinks = outputLinks;
	}

	public List getMetadata() {
		return metadata;
	}

	public void setMetadata(List metadata) {
		this.metadata = metadata;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getFirstFetch() {
		return firstFetch;
	}

	public void setFirstFetch(int firstFetch) {
		this.firstFetch = firstFetch;
	}

	public int getNextFetch() {
		return nextFetch;
	}

	public void setNextFetch(int nextFetch) {
		this.nextFetch = nextFetch;
	}

	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}

}
