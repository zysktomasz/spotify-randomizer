package it.zysk.spotifyrandomizer.rest.exception.model;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class ApiError {
    int status;
    String title;
    String details;
}
