package it.zysk.spotifyrandomizer.mapper;

import com.wrapper.spotify.model_objects.specification.Track;
import it.zysk.spotifyrandomizer.dto.TrackItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface TrackItemMapper {

    @Mappings({
            @Mapping(source = "album.name", target = "albumName")
    })
    TrackItem trackToPlaylistTrackItem(Track track);
}
