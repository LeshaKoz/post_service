package faang.school.postservice.controller;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.LikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
public class LikeControllerTest {

    private static final Long POST_ID = 2L;
    private static final Long COMMENT_ID = 3L;

    private MockMvc mockMvc;
    private UserDto user;
    @Mock
    private LikeService likeService;
    @InjectMocks
    private LikeController likeController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(likeController).build();
        user = new UserDto(1L, "Join", "join@mail.ru");
    }

    @Test
    public void testGetAllUsersWhoLikedPost() throws Exception {
        when(likeService.getAllUsersWhoLikedPost(POST_ID)).thenReturn(List.of(user));

        mockMvc.perform(get("/likes/posts/2").header("X-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username", is("Join")))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].email", is("join@mail.ru")));
    }

    @Test
    public void testGetAllUsersWhoLikedComment() throws Exception {
        when(likeService.getAllUsersWhoLikedComment(COMMENT_ID)).thenReturn(List.of(user));

        mockMvc.perform(get("/likes/comments/3").header("X-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username", is("Join")))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].email", is("join@mail.ru")));
    }
}
