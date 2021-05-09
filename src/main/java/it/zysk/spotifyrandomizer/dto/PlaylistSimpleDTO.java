package it.zysk.spotifyrandomizer.dto;

import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PlaylistSimpleDTO {

    private static final String SPOTIFY_EXTERNAL_URL_KEY = "spotify";

    String id;
    String name;
    String ownerDisplayName;
    String webPlayerUrl;
    String coverImageUrl;
    Boolean isPublic;
    String snapshotId;
    Integer tracksCount;

    public static PlaylistSimpleDTO buildFromEntity(PlaylistSimplified entity) {
        // TODO: 26.04.2021 automapper
        return PlaylistSimpleDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .ownerDisplayName(entity.getOwner().getDisplayName())
                .webPlayerUrl(entity.getExternalUrls().get(SPOTIFY_EXTERNAL_URL_KEY))
                .coverImageUrl(entity.getImages()[0].getUrl())
                .isPublic(entity.getIsPublicAccess())
                .snapshotId(entity.getSnapshotId())
                .tracksCount(entity.getTracks().getTotal())
                .build();
    }
}
