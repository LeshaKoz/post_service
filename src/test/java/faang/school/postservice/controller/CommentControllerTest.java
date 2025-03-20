package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
public class CommentControllerTest {
    private final CommentDto commentDto;

    @Mock
    private CommentService service;

    @InjectMocks
    private CommentController controller;

    @Test
    public void positiveCreateComment() {
        when(service.createComment(1L, 1L, commentDto)).thenReturn(commentDto);

        controller.createComment(1L, 1L, commentDto);

        verify(service, times(1)).createComment(1L, 1L, commentDto);
    }

    @Test
    public void positiveEditComment() {
        when(service.editComment(commentDto, 1L, "Тест")).thenReturn(commentDto);

        controller.editComment(commentDto, 1L, "Тест");

        verify(service, times(1)).editComment(commentDto, 1L, "Тест");
    }

    @Test
    public void positiveGetAllComments() {
        CommentDto firstDto = CommentDto.builder().id(1L).content("Комментарий").build();
        List<CommentDto> result = List.of(firstDto);

        when(service.getAllComments(1L)).thenReturn(result);

        controller.getAllComments(1L);

        verify(service, times(1)).getAllComments(1L);
    }

    @Test
    public void positiveDeleteComment() {
        doNothing().when(service).deleteComment(1L);

        controller.deleteComment(1L);

        verify(service, times(1)).deleteComment(1L);
    }

}
