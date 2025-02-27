package faang.school.postservice.service.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.kafka.AlbumCreatedEvent;
import faang.school.postservice.dto.post.AlbumRequestDto;
import faang.school.postservice.dto.post.AlbumResponseDto;
import faang.school.postservice.dto.post.AlbumUsersDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.enums.Visibility;
import faang.school.postservice.exception.KafkaProduceException;
import faang.school.postservice.exception.album.AlbumAccessDeniedException;
import faang.school.postservice.filter.Filter;
import faang.school.postservice.filter.album.AlbumFilterDto;
import faang.school.postservice.mapper.post.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.post.Post;
import faang.school.postservice.repository.post.AlbumRepository;
import faang.school.postservice.repository.post.PostRepository;
import faang.school.postservice.service.kafka.KafkaMessageService;
import faang.school.postservice.strategy.album.VisibilityConverter;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static faang.school.postservice.enums.Visibility.ALL_USERS;
import static faang.school.postservice.enums.Visibility.SELECTED_USERS;
import static java.util.stream.Collectors.toMap;

@Slf4j
@Service
public class AlbumServiceImpl implements AlbumService {

    private final AlbumRepository albumRepository;
    private final PostRepository postRepository;
    private final AlbumMapper albumMapper;
    private final UserContext userContext;
    private final Map<Visibility, VisibilityConverter> visibilities;
    private final UserServiceClient userServiceClient;
    private final List<Filter<Album, AlbumFilterDto>> albumFilters;
    private final KafkaMessageService kafkaMessageService;

    @Value("${kafka.album.created.topic}")
    private String topic;

    public AlbumServiceImpl(AlbumRepository albumRepository, PostRepository postRepository,
                            AlbumMapper albumMapper, UserContext userContext,
                            List<VisibilityConverter> converters, UserServiceClient userServiceClient,
                            List<Filter<Album, AlbumFilterDto>> filters, KafkaMessageService kafkaMessageService) {
        this.albumRepository = albumRepository;
        this.postRepository = postRepository;
        this.albumMapper = albumMapper;
        this.userContext = userContext;
        this.visibilities = converters.stream()
                .collect(toMap(VisibilityConverter::getVisibility, Function.identity()));
        this.userServiceClient = userServiceClient;
        this.albumFilters = filters;
        this.kafkaMessageService = kafkaMessageService;
    }

    @Transactional
    @Override
    public AlbumResponseDto createAlbum(AlbumRequestDto dto) {
        long userId = userContext.getUserId();
        checkExistsUser(userId);
        Album album = albumMapper.toEntity(dto);
        album.setAuthorId(userId);
        album.setVisibility(ALL_USERS);
        Album savedAlbum = albumRepository.save(album);
        saveKafkaMessage(new AlbumCreatedEvent(userId, album.getId(), album.getTitle()));
        return albumMapper.toDto(savedAlbum);
    }

    @Transactional
    @Override
    public AlbumResponseDto addPostToAlbum(long postId, long albumId) {
        long userId = userContext.getUserId();
        checkExistsUser(userId);
        Album album = albumRepository.findById(albumId).orElseThrow(() -> new EntityNotFoundException(
                String.format("Album with id = %d not found", albumId)
        ));
        checkAuthor(userId, albumId);
        Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException(
                String.format("Post with id = %d not found", postId)));
        album.getPosts().add(post);
        return albumMapper.toDto(albumRepository.save(album));
    }

    @Transactional
    @Override
    public void deletePostFromAlbum(long postId, long albumId) {
        long userId = userContext.getUserId();
        checkExistsUser(userId);
        Album album = albumRepository.findById(albumId).orElseThrow(() -> new EntityNotFoundException(
                String.format("Album with id = %d not found", albumId)
        ));
        checkAuthor(userId, albumId);
        Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException(
                String.format("Post with id = %d not found", postId)));
        album.getPosts().remove(post);
        albumRepository.save(album);
    }

    @Transactional
    @Override
    public void addAlbumToFavorites(long albumId) {
        long userId = userContext.getUserId();
        checkExistsUser(userId);
        checkExistsAlbum(albumId);
        albumRepository.addAlbumToFavorites(albumId, userId);
    }

    @Transactional
    @Override
    public void deleteAlbumFromFavorites(long albumId) {
        long userId = userContext.getUserId();
        checkExistsUser(userId);
        checkAuthor(userId, albumId);
        checkExistsAlbum(albumId);
        albumRepository.deleteAlbumFromFavorites(albumId, userId);
    }

    @Override
    public List<AlbumResponseDto> getAllOwnAlbums(AlbumFilterDto filters) {
        long userId = userContext.getUserId();
        List<Album> albums = albumRepository.findByAuthorId(userId);
        for (Filter<Album, AlbumFilterDto> filter : albumFilters) {
            if (filter.isApplicable(filters)) {
                albums = filter.apply(albums, filters);
            }
        }
        return albums.stream()
                .map(albumMapper::toDto)
                .toList();
    }

    @Override
    public List<AlbumResponseDto> getAllAlbumsByFilters(int pageNumber, int pageSize, AlbumFilterDto filters) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<Album> albums = albumRepository.findAll(pageable).toList();
        for (Filter<Album, AlbumFilterDto> filter : albumFilters) {
            if (filter.isApplicable(filters)) {
                albums = filter.apply(albums, filters);
            }
        }
        return albums.stream()
                .map(albumMapper::toDto)
                .toList();
    }

    @Override
    public List<AlbumResponseDto> getAllFavoritesOwnAlbums(AlbumFilterDto filters) {
        long userId = userContext.getUserId();
        List<Album> albums = albumRepository.findFavoritesByAuthorId(userId);
        for (Filter<Album, AlbumFilterDto> filter : albumFilters) {
            if (filter.isApplicable(filters)) {
                albums = filter.apply(albums, filters);
            }
        }
        return albums.stream()
                .map(albumMapper::toDto)
                .toList();
    }

    @Override
    public AlbumResponseDto updateAlbum(long albumId, AlbumRequestDto dto) {
        long userId = userContext.getUserId();
        checkExistsUser(userId);
        checkAuthor(userId, albumId);
        Album album = albumRepository.findById(albumId).orElseThrow(() -> new EntityNotFoundException(
                String.format("Album with id = %d not found", albumId)
        ));
        albumMapper.update(dto, album);
        return albumMapper.toDto(albumRepository.save(album));
    }

    @Override
    public void deleteAlbumById(long albumId) {
        long userId = userContext.getUserId();
        checkExistsUser(userId);
        checkExistsAlbum(albumId);
        checkAuthor(userId, albumId);
        albumRepository.deleteById(albumId);
    }

    @Transactional
    @Override
    public AlbumResponseDto getAlbumById(long id) {
        Album album = albumRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Album with id = %d not found", id)));
        log.info("Album with id = {} founded", album.getId());
        return visibilities.get(album.getVisibility()).apply(album);
    }

    @Transactional
    @Override
    public List<AlbumResponseDto> getAlbumsByAuthorId(long authorId) {
        List<AlbumResponseDto> albums = albumRepository.findByAuthorId(authorId).stream()
                .map(album -> visibilities.get(album.getVisibility()).apply(album))
                .toList();
        log.info("Founded albums with only access for author: {}", authorId);
        return albums;
    }

    @Override
    public void changeVisibilityAlbum(long albumId, Visibility visibility) {
        Album album = albumRepository.findById(albumId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Album with id = %d not found", albumId)));
        long userId = userContext.getUserId();
        checkAuthor(userId, albumId);
        log.info("User with id = {} is author for album with id = {}", userId, album.getId());
        album.setVisibility(visibility);
        albumRepository.save(album);
        log.info("Visibility for album with id = {} changed on {}", album.getId(), visibility);
    }

    @Transactional
    @Override
    public void addUsersForAccessAlbum(long albumId, AlbumUsersDto albumUsersDto) {
        Album album = albumRepository.findById(albumId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Album with id = %d not found", albumId)));
        long userId = userContext.getUserId();
        checkAuthor(userId, albumId);
        checkVisibilityForAlbum(album, SELECTED_USERS);
        albumUsersDto.usersIds().forEach(id -> albumRepository.addUserForVisibilityAtAlbum(album.getId(), id));
    }

    private void checkExistsUser(long userId) {
        UserDto user = userServiceClient.getUser(userId);
        if (user == null) {
            throw new EntityNotFoundException(
                    String.format("User with id = %d not found", userId)
            );
        }
    }

    private void checkExistsAlbum(long albumId) {
        if (!albumRepository.existsById(albumId)) {
            throw new EntityNotFoundException(
                    String.format("Album with id = %d not found", albumId)
            );
        }
    }

    private void checkAuthor(long userId, long albumId) {
        Album album = albumRepository.findById(albumId).orElseThrow(() -> new EntityNotFoundException(
                String.format("Album with id = %d not found", albumId)
        ));
        if (userId != album.getAuthorId()) {
            log.error("User with id = {} isn't author for album with id = {}", userId, album.getId());
            throw new AlbumAccessDeniedException(
                    String.format("User with id = %d isn't author for album with id = %d", userId, album.getId()));
        }
    }

    private void checkVisibilityForAlbum(Album album, Visibility visibility) {
        if (!visibility.equals(album.getVisibility())) {
            log.error("Visibility isn't {} in album with id = {}", visibility, album.getId());
            throw new IllegalArgumentException(
                    String.format("Needed selected_users visibility for add users for access. Album: %d", album.getId()));
        }
    }

    private void saveKafkaMessage(AlbumCreatedEvent albumCreatedEvent) {
        try {
            kafkaMessageService.saveMessage(topic, albumCreatedEvent);
        } catch (JsonProcessingException e) {
            throw new KafkaProduceException(
                    String.format("Failed kafka produce created album event. Goal id = %d", albumCreatedEvent.albumId()));
        }
    }
}
