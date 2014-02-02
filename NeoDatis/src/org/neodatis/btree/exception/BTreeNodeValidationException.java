package org.neodatis.btree.exception;

public class BTreeNodeValidationException extends RuntimeException {

	public BTreeNodeValidationException() {
		super();
	}

	public BTreeNodeValidationException(String message, Throwable cause) {
		super(message, cause);
	}

	public BTreeNodeValidationException(String message) {
		super(message);
	}

}
