package com.lachata.checker;

public class FormatChecker {
	public boolean longCheck(Object object) {
		return (object instanceof Long);
	}
	public boolean stringCheck(Object object) {
		return (object instanceof String);
	}

	public boolean integerCheck(Object object) {
		return (object instanceof Integer);
	}
}
