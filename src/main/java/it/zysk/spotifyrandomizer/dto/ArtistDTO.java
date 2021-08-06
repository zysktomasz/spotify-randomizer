package it.zysk.spotifyrandomizer.dto;

import lombok.Builder;

@Builder
public record ArtistDTO(String id, String name) {
}
