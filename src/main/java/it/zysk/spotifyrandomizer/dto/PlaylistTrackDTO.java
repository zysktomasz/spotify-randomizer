package it.zysk.spotifyrandomizer.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
public class PlaylistTrackDTO {
    String addedAt; // todo: parse to ZonedDateTime object
    PlaylistTrackItem trackItem;
}
