package faang.school.postservice.service;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.BusinessException;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.model.ElementType;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeMapper likeMapper;
    private final LikeRepository likeRepository;
    private final PostService postService;
    private final CommentService commentService;
    private final UserService userService;

    public Like findById(@NotNull Long likeId) {
        return likeRepository.findById(likeId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Лайка по такому %d не существует", likeId))
                );
    }

    @Transactional
    public LikeDto userLike(LikeDto dto, ElementType type) {
        UserDto userDto = userService.getUserByContext();
        Like like = assignElement(dto, userDto, type);

        return mapToDto(likeRepository.save(like), type);
    }

    private Like assignElement(LikeDto dto,
                               UserDto userDto,
                               ElementType type) {

        Like like = likeMapper.toEntity(dto);
        like.setUserId(userDto.id());

        if (type == ElementType.COMMENT) {
            if (likeRepository
                    .findByCommentIdAndUserId(dto.elementId(), userDto.id())
                    .isPresent()) {
                throw new BusinessException("Под комментом у юзера уже стоит лайк");
            }
            like.setComment(commentService.getCommentById(dto.elementId()));
        } else if (type == ElementType.POST) {
            if (likeRepository
                    .findByPostIdAndUserId(dto.elementId(), userDto.id())
                    .isPresent()) {
                throw new BusinessException("Под постом у юзера уже стоит лайк");
            }
            like.setPost(postService.findById(dto.elementId()));
        }

        return like;
    }

    private LikeDto mapToDto(Like like, ElementType type) {
        if (type == ElementType.COMMENT) {
            return likeMapper.toCommentDto(like);
        } else {
            return likeMapper.toPostDto(like);
        }
    }

    @Transactional
    public void removeLike(Long likeId, LikeDto dto, ElementType type) {
        UserDto userDto = userService.getUserByContext();

        if (type == ElementType.COMMENT) {
            if (canUserDeleteLike(likeId, userDto.id())) {
                throw new BusinessException("Пользователь не может удалить лайк на этом комментарии " +
                        "так как id не совпадают");
            }
            likeRepository.deleteByCommentIdAndUserId(dto.elementId(), userDto.id());

        } else if (type == ElementType.POST) {
            if (canUserDeleteLike(likeId, userDto.id())) {
                throw new BusinessException("Пользователь не может удалить лайк на этом посту " +
                        "так как id не совпадают");
            }
            likeRepository.deleteByPostIdAndUserId(dto.elementId(), userDto.id());
        }
    }

    private boolean canUserDeleteLike(Long likeId, Long userId) {
        return !findById(likeId).getUserId().equals(userId);
    }
}
