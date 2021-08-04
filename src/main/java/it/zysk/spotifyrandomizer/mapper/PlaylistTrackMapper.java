package it.zysk.spotifyrandomizer.mapper;

import com.wrapper.spotify.model_objects.IPlaylistItem;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.model_objects.specification.Track;
import it.zysk.spotifyrandomizer.dto.PlaylistTrackDTO;
import it.zysk.spotifyrandomizer.dto.PlaylistTrackItem;
import jdk.jfr.Name;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PlaylistTrackMapper {

    TrackItemMapper trackItemMapper = Mappers.getMapper(TrackItemMapper.class);

    @Mappings({
            @Mapping(source = "track", target = "trackItem"),
    })
    PlaylistTrackDTO playlistTrackToPlaylistTrackDTO(PlaylistTrack playlistTrack);

    @Name("trackToPlaylistTrackItem")
    static PlaylistTrackItem trackToPlaylistTrackItem(IPlaylistItem iPlaylistItem) {
        if (iPlaylistItem instanceof Track track) {
            return trackItemMapper.trackToPlaylistTrackItem(track);
        }

        // todo: add handling of mapper for Episode (podcast)
        return null;
    }
}
