package it.zysk.spotifyrandomizer.rest.exception.exceptions;

public class UnableToRetrieveCurrentUsersPlaylists extends RuntimeException {
    private static final String ERROR_MESSAGE = "Unexpected error occurred while retrieving current users playlists";

    public UnableToRetrieveCurrentUsersPlaylists(Throwable cause) {
        super(ERROR_MESSAGE, cause);
    }
}
