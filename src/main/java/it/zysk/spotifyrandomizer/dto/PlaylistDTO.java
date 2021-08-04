package it.zysk.spotifyrandomizer.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
public class PlaylistDTO {

    String id;
    String name;
    String ownerDisplayName;
    String webPlayerUrl;
    String coverImageUrl;
    Boolean isPublic;
    String snapshotId;
    Integer tracksCount;
}