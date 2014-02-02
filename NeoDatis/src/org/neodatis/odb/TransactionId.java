package org.neodatis.odb;

import java.io.Serializable;

public interface TransactionId extends Serializable {

	long getId1();

	long getId2();

	DatabaseId getDatabaseId();

	TransactionId next();

	TransactionId prev();

}