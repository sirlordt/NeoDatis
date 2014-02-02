package org.neodatis.odb.test.update.nullobject;

import java.util.Date;

/**
 * User
 * 
 * 
 */
public class User {

	private Profile profileId;
	private String login;
	private String name;
	private String password;
	private String email;
	private Date creationDate;
	private Date updateDate;
	private Date lastLogin;
	private boolean status;
	private Integer rejectedLogin;
	private String sessionKey;
	private boolean deleted; // S ou N

	public String toString() {
		return "[" + profileId + "]" + "[" + login + "][" + name + "][" + password + "]" + "[" + email + "][" + creationDate + "]["
				+ lastLogin + "]" + "[" + status + "][" + rejectedLogin + "]" + "][" + sessionKey + "][" + deleted + "]";
	}

	/**
	 * 
	 * @return Returns the creationDate.
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * 
	 * @return
	 */
	public boolean getDeleted() {
		return deleted;
	}

	/**
	 * 
	 * @return Returns the login.
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * @param login
	 *            The login to set.
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	/**
	 * 
	 * @return Returns the email.
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * 
	 * @return Returns the lastLogin.
	 */
	public Date getLastLogin() {
		return lastLogin;
	}

	/**
	 * 
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @return Returns the password.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * 
	 * @return Returns the profileId.
	 */
	public Profile getProfileId() {
		return profileId;
	}

	/**
	 * 
	 * @return Returns the rejectedLogin.
	 */
	public Integer getRejectedLogin() {
		return rejectedLogin;
	}

	/**
	 * 
	 * @return Returns the sessionKey.
	 */
	public String getSessionKey() {
		return sessionKey;
	}

	/**
	 * 
	 * @return Returns the status.
	 */
	public boolean getStatus() {
		return status;
	}

	/**
	 * 
	 * @return Returns the updateDate.
	 */
	public Date getUpdateDate() {
		return updateDate;
	}

	/**
	 * @param creationDate
	 *            The creationDate to set.
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @param email
	 *            The email to set.
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @param lastLogin
	 *            The lastLogin to set.
	 */
	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	/**
	 * @param name
	 *            The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param password
	 *            The password to set.
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @param profileId
	 *            The profileId to set.
	 */
	public void setProfileId(Profile profileId) {
		this.profileId = profileId;
	}

	/**
	 * @param rejectedLogin
	 *            The rejectedLogin to set.
	 */
	public void setRejectedLogin(Integer rejectedLogin) {
		this.rejectedLogin = rejectedLogin;
	}

	/**
	 * @param sessionKey
	 *            The sessionKey to set.
	 */
	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}

	/**
	 * @param status
	 *            The status to set.
	 */
	public void setStatus(boolean status) {
		this.status = status;
	}

	/**
	 * @param updateDate
	 *            The updateDate to set.
	 */
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	/**
	 * 
	 * @param deleted
	 */
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
}
