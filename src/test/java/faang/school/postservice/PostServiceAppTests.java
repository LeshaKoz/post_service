package faang.school.postservice;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = PostServiceApp.class)
class PostServiceAppTests {
    @Test
    void contextLoads() {
        Assertions.assertThat(40 + 2).isEqualTo(42);
    }
}
