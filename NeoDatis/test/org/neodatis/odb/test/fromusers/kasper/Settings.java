package org.neodatis.odb.test.fromusers.kasper;

import java.io.Serializable;

/**
 * This class contains all the setting parameters to make IBSLogic a
 * provider-specific instance.
 * 
 * @author Kasper Benjamin Hansen (created January 2009)
 * 
 */
public class Settings implements Serializable {

	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}