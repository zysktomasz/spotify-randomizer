package it.zysk.spotifyrandomizer.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
public class ArtistDTO {
    String id;
    String name;
}
