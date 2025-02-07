package faang.school.postservice.filter.post;

import faang.school.postservice.dto.post.PostFilterDto;
import faang.school.postservice.model.Post;
import org.springframework.data.jpa.domain.Specification;

public interface PostSpecificationFilter {
    boolean isApplicable(PostFilterDto filters);

    Specification<Post> apply (PostFilterDto filters);
}
