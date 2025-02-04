package faang.school.postservice.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import faang.school.postservice.dto.like.comment.LikeCommentDto;
import faang.school.postservice.dto.like.comment.LikeCommentDtoResponse;
import faang.school.postservice.dto.like.post.LikePostDto;
import faang.school.postservice.dto.like.post.LikePostDtoResponse;
import faang.school.postservice.service.like.LikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class LikeControllerTest {

    public static final String END_POINT_LIKES_POST = "/api/v1/likes/post";
    public static final String END_POINT_LIKES_COMMENT = "/api/v1/likes/comment";
    public static final String END_POINT_LIKES_POST_DELETE = "/api/v1/likes/post/1";
    public static final String END_POINT_LIKES_COMMENT_DELETE = "/api/v1/likes/comment/2";
    private MockMvc mockMvc;

    @Mock
    private LikeService likeService;

    @InjectMocks
    private LikeController likeController;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new Jdk8Module());

    private LikePostDto likePostDto;
    private LikeCommentDto likeCommentDto;
    private LikePostDtoResponse likePostDtoResponse;
    private LikeCommentDtoResponse likeCommentDtoResponse;

    @BeforeEach
    public void setUp() {

        mockMvc = MockMvcBuilders.standaloneSetup(likeController).build();
        likePostDto = new LikePostDto(1L, 2L, 3L);
        likeCommentDto = new LikeCommentDto(1L, 2L, 3L, 4L);
        likePostDtoResponse = new LikePostDtoResponse(1L, 2L, 3L);
        likeCommentDtoResponse = new LikeCommentDtoResponse(1L, 2L, 3L, 4L);
    }

    @Test
    void testLikeForPostValidRequest() throws Exception {

        Mockito.when(likeService.createLikeForPost(any(LikePostDto.class))).thenReturn(likePostDtoResponse);
        mockMvc.perform(post(END_POINT_LIKES_POST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(likePostDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(2L));
    }

    @Test
    void testLikeForCommentValidRequest() throws Exception {

        Mockito.when(likeService.createLikeForComment(any(LikeCommentDto.class))).thenReturn(likeCommentDtoResponse);
        mockMvc.perform(post(END_POINT_LIKES_COMMENT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(likeCommentDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(2L));
    }

    @Test
    void testDeleteLikeFromPostShouldReturn200WhenValidId() throws Exception {

        doNothing().when(likeService).deleteLikeFromPost(1L);
        mockMvc.perform(delete(END_POINT_LIKES_POST_DELETE))
                .andExpect(status().isOk());

        verify(likeService, times(1)).deleteLikeFromPost(1L);
    }

    @Test
    void testDeleteLikeFromCommentShouldReturn200WhenValidId() throws Exception {

        doNothing().when(likeService).deleteLikeFromComment(2L);
        mockMvc.perform(delete(END_POINT_LIKES_COMMENT_DELETE))
                .andExpect(status().isOk());

        verify(likeService, times(1)).deleteLikeFromComment(2L);
    }

    @Test
    void testLikeForPostShouldReturn400WhenInvalidData() throws Exception {

        LikePostDto invalidRequest = new LikePostDto(null, null, null);
        mockMvc.perform(post(END_POINT_LIKES_POST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}