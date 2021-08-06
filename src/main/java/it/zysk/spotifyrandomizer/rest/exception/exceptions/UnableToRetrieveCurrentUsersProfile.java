package it.zysk.spotifyrandomizer.rest.exception.exceptions;

public class UnableToRetrieveCurrentUsersProfile extends RuntimeException {
    private static final String ERROR_MESSAGE = "Unexpected error occurred while retrieving current users profile";

    public UnableToRetrieveCurrentUsersProfile(Throwable cause) {
        super(ERROR_MESSAGE, cause);
    }
}
