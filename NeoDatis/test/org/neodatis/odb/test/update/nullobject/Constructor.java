package org.neodatis.odb.test.update.nullobject;

import java.util.Date;

/**
 * Fornecedor
 * 
 * @author Jeremias
 * 
 */
public class Constructor {
	private String name;
	private String description;
	private boolean deleted; // S ou N
	private Date creationDate;
	private Date updateDate;
	private User user;

	public Date getCreationDate() {
		return creationDate;
	}

	public boolean getDeleted() {
		return deleted;
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public User getUser() {
		return user;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
