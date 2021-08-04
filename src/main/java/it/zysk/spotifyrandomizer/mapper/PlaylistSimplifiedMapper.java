package it.zysk.spotifyrandomizer.mapper;

import com.wrapper.spotify.model_objects.specification.ExternalUrl;
import com.wrapper.spotify.model_objects.specification.Image;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;
import it.zysk.spotifyrandomizer.dto.PlaylistDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface PlaylistSimplifiedMapper {

    String SPOTIFY_EXTERNAL_URL_KEY = "spotify";

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "name", target = "name"),
            @Mapping(source = "owner.displayName", target = "ownerDisplayName"),
            @Mapping(source = "externalUrls", target = "webPlayerUrl", qualifiedByName = "externalUrlsToWebPlayerUrl"),
            @Mapping(source = "isPublicAccess", target = "isPublic"),
            @Mapping(source = "images", target = "coverImageUrl", qualifiedByName = "imagesToCoverImageUrl"),
            @Mapping(source = "snapshotId", target = "snapshotId"),
            @Mapping(source = "tracks.total", target = "tracksCount"),
    })
    PlaylistDTO playlistSimplifiedToPlaylistDTO(PlaylistSimplified playlistSimplified);

    @Named("externalUrlsToWebPlayerUrl")
    static String externalUrlsToWebPlayerUrl(ExternalUrl externalUrls) {
        return externalUrls.get(SPOTIFY_EXTERNAL_URL_KEY);
    }

    @Named("imagesToCoverImageUrl")
    static String imagesToCoverImageUrl(Image[] images) {
        return images.length == 0 ? null : images[0].getUrl();
    }
}
