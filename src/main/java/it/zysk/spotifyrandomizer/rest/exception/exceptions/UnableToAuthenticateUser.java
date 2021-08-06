package it.zysk.spotifyrandomizer.rest.exception.exceptions;

public class UnableToAuthenticateUser extends RuntimeException {
    private static final String ERROR_MESSAGE = "Unable to authenticate user";

    public UnableToAuthenticateUser(Throwable cause) {
        super(ERROR_MESSAGE, cause);
    }
}
