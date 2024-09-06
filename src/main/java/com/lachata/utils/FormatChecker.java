package com.lachata.utils;

import com.lachata.exception.TypeParsingException;

/* Useless */
public class FormatChecker {

	public boolean longCheck(Object object) throws TypeParsingException {
		return (object instanceof Long);
	}
	public boolean stringCheck(Object object) throws TypeParsingException {
		return (object instanceof String);
	}

	public boolean integerCheck(Object object) throws TypeParsingException {
		return (object instanceof Integer);
	}
}
