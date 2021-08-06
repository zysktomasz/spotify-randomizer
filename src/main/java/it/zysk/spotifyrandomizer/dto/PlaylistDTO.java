package it.zysk.spotifyrandomizer.dto;

import lombok.Builder;

@Builder
public record PlaylistDTO(String id,
                          String name,
                          String ownerDisplayName,
                          String webPlayerUrl,
                          String coverImageUrl,
                          Boolean isPublic,
                          String snapshotId,
                          Integer tracksCount) {
}