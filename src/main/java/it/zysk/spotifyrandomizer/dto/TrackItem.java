package it.zysk.spotifyrandomizer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrackItem implements PlaylistTrackItem {
    private String id;
    private String name;
    private String albumName;
    private String albumImageUrl;
    private Set<ArtistDTO> artists;
}
