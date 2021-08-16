package it.zysk.spotifyrandomizer.mapper;

import com.wrapper.spotify.model_objects.specification.AlbumSimplified;
import com.wrapper.spotify.model_objects.specification.Image;
import com.wrapper.spotify.model_objects.specification.Track;
import it.zysk.spotifyrandomizer.dto.TrackItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface TrackItemMapper {

    @Mappings({
            @Mapping(source = "album.name", target = "albumName"),
            @Mapping(source = "album", target = "albumImageUrl", qualifiedByName = "imagesToAlbumImageUrl"),
    })
    TrackItem trackToPlaylistTrackItem(Track track);

    @Named("imagesToAlbumImageUrl")
    static String imagesToAlbumImageUrl(AlbumSimplified album) {
        Image[] images = album.getImages();
        return images == null || images.length == 0 ? "" : images[0].getUrl();
    }
}
