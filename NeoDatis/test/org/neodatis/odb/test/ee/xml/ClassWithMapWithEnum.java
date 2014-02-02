/**
 * 
 */
package org.neodatis.odb.test.ee.xml;

import java.util.HashMap;
import java.util.Map;

import org.neodatis.odb.test.enumeration.UserRole;
import org.neodatis.odb.test.vo.login.Function;

/**
 * @author olivier
 *
 */
public class ClassWithMapWithEnum extends TestExportImport {
	protected String name;
	protected Map<UserRole, Function> roles;
	
	public ClassWithMapWithEnum( String name){
		this.name = name;
		roles = new HashMap<UserRole, Function>();
	}
	public ClassWithMapWithEnum add(UserRole role, Function f){
		roles.put(role, f);
		return this;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Map<UserRole, Function> getRoles() {
		return roles;
	}
	public void setRoles(Map<UserRole, Function> roles) {
		this.roles = roles;
	}
	
	
}
