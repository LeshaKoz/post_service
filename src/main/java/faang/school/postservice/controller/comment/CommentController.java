package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.CommentFiltersDto;
import faang.school.postservice.dto.comment.CommentRequestDto;
import faang.school.postservice.dto.comment.CommentResponseDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.service.comment.CommentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public CommentResponseDto createComment(@Valid @RequestBody CommentRequestDto commentRequestDto) {
        return commentService.createComment(commentRequestDto);
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(@NotNull @PathVariable Long commentId) {
        commentService.deleteComment(commentId);
    }

    @PutMapping("/{commentId}")
    public CommentResponseDto updateComment(@NotNull @PathVariable Long commentId,
                              @Valid @RequestBody CommentUpdateDto commentUpdateDto) {
        return commentService.updateComment(commentId, commentUpdateDto);
    }

    @GetMapping
    public List<CommentResponseDto> getCommentsByFilters(@Valid CommentFiltersDto commentFiltersDto) {
        return commentService.getComments(commentFiltersDto);
    }

    @PostMapping("/{commentId}/image")
    public void uploadImage(@Valid @Positive @PathVariable Long commentId,
                            @NotEmpty @RequestParam("file") MultipartFile file) {
        commentService.uploadImage(commentId, file);
    }
}
