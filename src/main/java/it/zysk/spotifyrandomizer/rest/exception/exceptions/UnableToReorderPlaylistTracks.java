package it.zysk.spotifyrandomizer.rest.exception.exceptions;

public class UnableToReorderPlaylistTracks extends RuntimeException {
    private static final String ERROR_MESSAGE = "Unexpected error occurred while reordering playlists tracks";

    public UnableToReorderPlaylistTracks(Throwable cause) {
        super(ERROR_MESSAGE, cause);
    }
}
