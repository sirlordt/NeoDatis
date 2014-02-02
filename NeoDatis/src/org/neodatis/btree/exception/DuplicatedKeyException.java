package org.neodatis.btree.exception;

public class DuplicatedKeyException extends BTreeException {

	public DuplicatedKeyException() {
		super();
	}

	public DuplicatedKeyException(String message, Throwable cause) {
		super(message, cause);
	}

	public DuplicatedKeyException(String message) {
		super(message);
	}
}
