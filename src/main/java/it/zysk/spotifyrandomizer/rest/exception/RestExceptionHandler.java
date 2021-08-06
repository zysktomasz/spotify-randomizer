package it.zysk.spotifyrandomizer.rest.exception;

import it.zysk.spotifyrandomizer.rest.exception.exceptions.UnableToAuthenticateUser;
import it.zysk.spotifyrandomizer.rest.exception.exceptions.UnableToRetrieveCurrentUsersPlaylists;
import it.zysk.spotifyrandomizer.rest.exception.exceptions.UnableToRetrieveCurrentUsersProfile;
import it.zysk.spotifyrandomizer.rest.exception.exceptions.UnableToRetrievePlaylistTracks;
import it.zysk.spotifyrandomizer.rest.exception.model.ApiError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
class RestExceptionHandler {

    @ExceptionHandler({
            UnableToRetrieveCurrentUsersProfile.class,
            UnableToRetrieveCurrentUsersPlaylists.class,
            UnableToRetrievePlaylistTracks.class
    })
    ResponseEntity<ApiError> handleSpotifyServiceExceptions(Exception exception) {
        return handleException(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    ResponseEntity<ApiError> handleUnableToAuthenticateUser(UnableToAuthenticateUser exception) {
        return handleException(exception, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler
    ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException exception) {
        return handleException(exception, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ApiError> handleException(Exception exception, HttpStatus httpStatus) {
        log.error(exception.getMessage(), exception);

        var apiError = ApiError.builder()
                .status(httpStatus.value())
                .title(httpStatus.getReasonPhrase())
                .details(exception.getMessage())
                .build();

        return ResponseEntity.status(httpStatus)
                .contentType(MediaType.APPLICATION_JSON)
                .body(apiError);
    }
}
