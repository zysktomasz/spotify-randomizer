package it.zysk.spotifyrandomizer.mapper;

import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import it.zysk.spotifyrandomizer.dto.ArtistDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ArtistSimplifiedMapper {
    ArtistDTO artistSimplifiedToArtistDTO(ArtistSimplified artistSimplified);
}
