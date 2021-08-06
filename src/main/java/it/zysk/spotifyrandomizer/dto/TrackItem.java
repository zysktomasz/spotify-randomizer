package it.zysk.spotifyrandomizer.dto;

import lombok.Builder;

import java.util.Set;

@Builder
public record TrackItem(String id,
                        String name,
                        String albumName,
                        Set<ArtistDTO> artists) implements PlaylistTrackItem {
    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }
}
