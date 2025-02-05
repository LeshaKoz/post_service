package faang.school.postservice.service.album;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumCreateDto;
import faang.school.postservice.dto.album.AlbumReadDto;
import faang.school.postservice.exception.BusinessException;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.service.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlbumService {
    private final UserService userService;
    private final AlbumMapper albumMapper;
    private final AlbumRepository albumRepository;
    private final UserContext userContext;

    public AlbumReadDto createAlbum(AlbumCreateDto albumCreateDto) {
        validateUserExists(albumCreateDto.getAuthorId());
        Album album = albumMapper.toEntity(albumCreateDto);

        return albumMapper.toReadDto(albumRepository.save(album));
    }

    public AlbumReadDto addAlbumToFavorites(long albumId) {

        Album album = albumRepository.findById(albumId).orElseThrow(() -> new EntityNotFoundException("Альбом не найден"));

        albumRepository.addAlbumToFavorites(albumId, userContext.getUserId());

        return albumMapper.toReadDto(album);
    }

    private void validateUserExists(long authorId) {
        if (!userService.isUserExists(authorId)) {
            throw new BusinessException(String.format("Создание альбома невозможно, пользователь с ID %d не найден", authorId));
        }
    }
}
