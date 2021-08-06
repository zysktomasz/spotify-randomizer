package it.zysk.spotifyrandomizer.dto;

import lombok.Builder;

@Builder
public record PlaylistTrackDTO(String addedAt, PlaylistTrackItem trackItem) {
}
