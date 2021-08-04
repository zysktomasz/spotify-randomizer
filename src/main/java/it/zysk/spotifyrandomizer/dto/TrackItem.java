package it.zysk.spotifyrandomizer.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.util.Set;

@Value
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
public class TrackItem implements PlaylistTrackItem {
    String id;
    String name;
    String albumName;
    Set<ArtistDTO> artists;
}
