package it.zysk.spotifyrandomizer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistDTO {
    private String id;
    private String name;
    private String ownerDisplayName;
    private String webPlayerUrl;
    private String coverImageUrl;
    private Boolean isPublic;
    private String snapshotId;
    private Integer tracksCount;
}