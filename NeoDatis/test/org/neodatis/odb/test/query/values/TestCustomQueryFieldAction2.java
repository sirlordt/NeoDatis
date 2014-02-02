package org.neodatis.odb.test.query.values;

import org.neodatis.odb.ODB;
import org.neodatis.odb.OID;
import org.neodatis.odb.Values;
import org.neodatis.odb.core.layers.layer2.meta.AttributeValuesMap;
import org.neodatis.odb.impl.core.query.values.CustomQueryFieldAction;
import org.neodatis.odb.impl.core.query.values.ValuesCriteriaQuery;

public class TestCustomQueryFieldAction2 extends CustomQueryFieldAction {

	/** The number of logins */
	private long nbLoggedUsers;

	public TestCustomQueryFieldAction2() {
		this.nbLoggedUsers = 0;
	}

	/** The method that actually computes the logins */
	public void execute(final OID oid, final AttributeValuesMap values) {
		// Gets the name of the user
		String userName = (String) values.get("name");

		// Call an external class (Users) to check if the user is logged in
		if (Sessions.isLogged(userName)) {
			nbLoggedUsers++;
		}
	}

	public Object getValue() {
		return new Long(nbLoggedUsers);
	}

	public boolean isMultiRow() {
		return false;
	}

	public void start() {
		// Nothing to do
	}

	public void end() {
		// Nothing to do
	}

	public static void main(String[] args) throws Exception {
		ODB odb = null;
		CustomQueryFieldAction customAction = new TestCustomQueryFieldAction();

		Values values = odb.getValues(new ValuesCriteriaQuery(Users.class).custom("nbLogins", "nb logged users", customAction)
				.field("name"));

	}
}

class Sessions {

	public static boolean isLogged(String userName) {
		// TODO Auto-generated method stub
		return false;
	}

}

class Users {

}