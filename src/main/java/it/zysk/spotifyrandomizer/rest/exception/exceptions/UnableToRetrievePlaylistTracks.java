package it.zysk.spotifyrandomizer.rest.exception.exceptions;

public class UnableToRetrievePlaylistTracks extends RuntimeException {
    private static final String ERROR_MESSAGE_FORMAT = "Unexpected error occurred while retrieving tracks " +
            "for playlist with playlist_id = %s";

    public UnableToRetrievePlaylistTracks(String playlistId, Throwable cause) {
        super(String.format(ERROR_MESSAGE_FORMAT, playlistId), cause);
    }
}