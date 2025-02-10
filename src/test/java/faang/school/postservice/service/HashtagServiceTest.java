package faang.school.postservice.service;

import faang.school.postservice.dto.hashtag.HashtagCreateDto;
import faang.school.postservice.dto.hashtag.HashtagUpdateDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.HashtagMapperImpl;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.repository.HashtagRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class HashtagServiceTest {

    @Spy
    private HashtagMapperImpl hashtagMapper;

    @Mock
    private HashtagRepository hashtagRepository;

    @InjectMocks
    private HashtagService hashtagService;

    @Test
    public void testCreateSuccessfully() {
        HashtagCreateDto createDto = HashtagCreateDto.builder()
                .name("Hashtag").build();
        Mockito.when(hashtagRepository.findByName(createDto.name())).thenReturn(Optional.empty());
        Hashtag newHashtag = hashtagMapper.toEntity(createDto);

        hashtagService.create(createDto);
        Mockito.verify(hashtagRepository, Mockito.times(1)).save(newHashtag);
    }

    @Test
    public void testCreateWithExistName() {
        HashtagCreateDto createDto = HashtagCreateDto.builder()
                .name("Hashtag").build();

        Mockito.when(hashtagRepository.findByName(createDto.name())).thenReturn(Optional.of(new Hashtag()));

        assertThrows(DataValidationException.class, () -> hashtagService.create(createDto));
    }

    @Test
    public void testUpdateSuccessfully() {
        HashtagUpdateDto updateDto = HashtagUpdateDto.builder()
                .id(1L).name("Hashtag").build();
        Hashtag hashtag = Hashtag.builder()
                .id(1L).name("Old name").build();

        Mockito.when(hashtagRepository.findById(1L)).thenReturn(Optional.ofNullable(hashtag));
        Mockito.when(hashtagRepository.findByName(updateDto.name())).thenReturn(Optional.empty());

        hashtagMapper.updateEntityFromDto(updateDto, hashtag);

        hashtagService.update(updateDto);
        Mockito.verify(hashtagRepository, Mockito.times(1)).save(hashtag);
    }

    @Test
    public void testUpdateWithExistName() {
        HashtagUpdateDto updateDto = HashtagUpdateDto.builder()
                .id(1L).name("Hashtag").build();
        Hashtag hashtag = Hashtag.builder()
                .id(1L).build();

        Mockito.when(hashtagRepository.findById(1L)).thenReturn(Optional.ofNullable(hashtag));
        Mockito.when(hashtagRepository.findByName(updateDto.name())).thenReturn(Optional.of(new Hashtag()));

        assertThrows(DataValidationException.class, () -> hashtagService.update(updateDto));
    }

    @Test
    public void testGetHashtagSuccessfully() {
        Mockito.when(hashtagRepository.findById(1L)).thenReturn(Optional.of(new Hashtag()));

        hashtagService.getHashtag(1L);
        Mockito.verify(hashtagRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    public void testRemoveSuccessfully() {
        hashtagService.remove(1L);
        Mockito.verify(hashtagRepository, Mockito.times(1)).deleteById(1L);
    }

}
