package faang.school.postservice.validator.album;

import faang.school.postservice.model.Album;

public interface AlbumValidator {
    void validateUserExists(long userId);

    void validateTitle(String title, long userId);

    void validateAuthor(Album album, long userId);
}
