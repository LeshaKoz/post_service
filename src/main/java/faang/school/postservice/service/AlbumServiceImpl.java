package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.album.PostAlbumDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.AlbumMapper;
import faang.school.postservice.repository.AlbumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {
    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;
    private final UserServiceClient userServiceClient;
    private final PostAlbumService postAlbumService;

    @Override
    public AlbumDto create(AlbumDto albumDto) {
        if (albumRepository.existsByAuthorIdAndTitle(albumDto.authorId(), albumDto.title())) {
            throw new DataValidationException("The album already exists");
        }
        if (albumDto.description().isEmpty()) {
            throw new DataValidationException("the description is empty");
        }
        return albumMapper.toAlbumDto(albumRepository.save(albumMapper.toAlbum(albumDto)));
    }

    public void addPostToAlbum(PostAlbumDto dto) {
        UserDto userDto = userServiceClient.getUser(dto.userId());
        if (userDto == null) {
            throw new DataValidationException("The user is not found");
        }
        if (!albumRepository.existsById(dto.albumId())) {
            throw new DataValidationException("The album is not found");
        }
        if (userDto.id() != dto.albumId()) {
            throw new DataValidationException("The user is not the album owner");
        }
        postAlbumService.save(dto);
    }
}