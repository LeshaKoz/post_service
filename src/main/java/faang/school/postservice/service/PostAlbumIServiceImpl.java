package faang.school.postservice.service;

import faang.school.postservice.dto.album.PostAlbumDto;
import faang.school.postservice.mapper.PostAlbumMapper;
import faang.school.postservice.repository.PostAlbumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostAlbumIServiceImpl implements PostAlbumService {
    private final PostAlbumRepository postAlbumRepository;
    private final PostAlbumMapper postAlbumMapper;

    @Override
    public void save(PostAlbumDto dto) {
        postAlbumRepository.save(postAlbumMapper.toPostAlbum(dto));
    }
}
