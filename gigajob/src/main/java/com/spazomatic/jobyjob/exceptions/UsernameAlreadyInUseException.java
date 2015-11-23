package com.spazomatic.jobyjob.exceptions;

public class UsernameAlreadyInUseException extends Exception {
	
	public UsernameAlreadyInUseException(String username) {
		super("The username '" + username + "' is already in use.");
	}
}