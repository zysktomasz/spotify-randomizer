package it.zysk.spotifyrandomizer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistTrackDTO {
    private String addedAt;
    private PlaylistTrackItem trackItem;
}
