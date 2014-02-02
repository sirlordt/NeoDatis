package org.neodatis.odb;

import java.io.Serializable;

public interface DatabaseId extends Serializable {

	long[] getIds();

}