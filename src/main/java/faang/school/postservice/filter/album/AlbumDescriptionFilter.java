package faang.school.postservice.filter.album;

import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.model.Album;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class AlbumDescriptionFilter implements AlbumFilter {
    @Override
    public boolean isApplicable(AlbumFilterDto albumFilterDto) {
        String descriptionPattern = albumFilterDto.getDescriptionPattern();
        return Objects.nonNull(descriptionPattern) && !descriptionPattern.isBlank();
    }

    @Override
    public Stream<Album> apply(Stream<Album> albums, AlbumFilterDto albumFilterDto) {
        String descriptionPattern = albumFilterDto.getDescriptionPattern().trim().toLowerCase();
        return albums.filter(album -> album.getDescription().trim().toLowerCase().contains(descriptionPattern));
    }
}
