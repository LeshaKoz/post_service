package faang.school.postservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.service.LikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
public class LikeControllerTest {

    @InjectMocks
    private LikeController likeController;

    @Mock
    private LikeService likeService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(likeController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testPositivePutLikeOnPost() throws Exception {

    }

    @Test
    public void testPositiveRemoveLikeOnPost() throws Exception {

    }

    @Test
    public void testPositivePutLikeOnComment() throws Exception {

    }

    @Test
    public void testPositiveRemoveLikeOnComment() throws Exception {

    }


}
