package faang.school.postservice.service.album;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.album.AlbumCreateDto;
import faang.school.postservice.dto.album.AlbumEditDto;
import faang.school.postservice.dto.album.AlbumFilterDto;
import faang.school.postservice.dto.album.AlbumReadDto;
import faang.school.postservice.exception.BusinessException;
import faang.school.postservice.filter.album.AlbumFilter;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.service.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AlbumService {
    private final UserService userService;
    private final AlbumMapper albumMapper;
    private final AlbumRepository albumRepository;
    private final UserContext userContext;
    private final List<AlbumFilter> filters;

    public AlbumReadDto createAlbum(AlbumCreateDto dto) {
        validateUserExists(dto.getAuthorId());
        validateAlbumTitleIsDistinct(dto.getTitle(), dto.getAuthorId());

        Album album = albumMapper.toEntity(dto);

        return albumMapper.toReadDto(albumRepository.save(album));
    }

    public AlbumReadDto addAlbumToFavorites(long albumId) {
        Album album = getAlbumById(albumId);

        albumRepository.addAlbumToFavorites(albumId, userContext.getUserId());

        return albumMapper.toReadDto(album);
    }

    public AlbumReadDto deleteAlbumFromFavorites(long albumId) {
        Album album = getAlbumById(albumId);

        albumRepository.deleteAlbumFromFavorites(albumId, userContext.getUserId());

        return albumMapper.toReadDto(album);
    }

    public AlbumReadDto findAlbumById(long albumId) {
        return albumMapper.toReadDto(getAlbumById(albumId));
    }

    public List<AlbumReadDto> findAuthorAlbumsByFilters(AlbumFilterDto filterDto, long authorId) {
        List<Album> albums = getAlbumsByAuthorId(authorId);
        List<AlbumFilter> applicableFilters = getApplicableFilters(filterDto);

        return applyFilters(albums, applicableFilters, filterDto);
    }

    public List<AlbumReadDto> findAllAlbumsByFilters(AlbumFilterDto filterDto) {
        List<Album> albums = getAllAlbums();
        List<AlbumFilter> applicableFilters = getApplicableFilters(filterDto);

        return applyFilters(albums, applicableFilters, filterDto);
    }

    public List<AlbumReadDto> findFavoriteAlbumsByFilters(AlbumFilterDto filterDto) {
        List<Album> albums = albumRepository.findFavoriteAlbumsByUserId(userContext.getUserId());
        List<AlbumFilter> applicableFilters = getApplicableFilters(filterDto);

        return applyFilters(albums, applicableFilters, filterDto);
    }

    public AlbumReadDto editAlbum(AlbumEditDto dto) {
        if (!isUserInteractOwnAlbum(dto.getAuthorId())) {
            throw new BusinessException("Изменение чужого альбома невозможно");
        }

        Album album = getAlbumById(dto.getId());
        albumMapper.update(album, dto);

        return albumMapper.toReadDto(albumRepository.save(album));
    }

    public void deleteAlbum(long albumId) {
        if (!albumRepository.existsById(albumId)) {
            throw new EntityNotFoundException(String.format("Альбом с ID %d не найден", albumId));
        }

        albumRepository.deleteById(albumId);
    }

    private void validateUserExists(long authorId) {
        if (!userService.isUserExists(authorId)) {
            throw new BusinessException(String.format("Пользователь с ID %d не найден", authorId));
        }
    }

    private boolean isUserInteractOwnAlbum(long authorId) {
        return Objects.equals(userContext.getUserId(), authorId);
    }

    private void validateAlbumTitleIsDistinct(String title, long authorId) {
        if (albumRepository.existsByTitleAndAuthorId(title, authorId)) {
            throw new BusinessException(String.format("Пользователь с ID %d уже имеет альбом с таким названием", authorId));
        }
    }

    private Album getAlbumById(long albumId) {
        return albumRepository.findById(albumId).orElseThrow(() -> new EntityNotFoundException("Альбом не найден"));
    }

    private List<Album> getAlbumsByAuthorId(long authorId) {
        return albumRepository.findAllByAuthorId(authorId);
    }

    private List<Album> getAllAlbums() {
        return albumRepository.findAll();
    }

    private List<AlbumFilter> getApplicableFilters(AlbumFilterDto filterDto) {
        return filters.stream()
                .filter(albumFilter -> albumFilter.isApplicable(filterDto))
                .toList();
    }

    private List<AlbumReadDto> applyFilters(List<Album> albums, List<AlbumFilter> applicableFilters, AlbumFilterDto filterDto) {
        if (applicableFilters.isEmpty()) {
            return albums.stream()
                    .map(albumMapper::toReadDto)
                    .toList();
        }

        return applicableFilters.stream()
                .reduce(albums.stream(),
                        ((albumStream, albumFilter) -> albumFilter.apply(albumStream, filterDto)),
                        (list2, list1) -> list1)
                .map(albumMapper::toReadDto)
                .toList();
    }
}
