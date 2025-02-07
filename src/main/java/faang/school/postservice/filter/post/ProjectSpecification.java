package faang.school.postservice.filter.post;

import faang.school.postservice.dto.post.PostFilterDto;
import faang.school.postservice.model.Post;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class ProjectSpecification implements PostSpecificationFilter {
    @Override
    public boolean isApplicable(PostFilterDto filters) {
        return filters.projectId() != null;
    }

    @Override
    public Specification<Post> apply(PostFilterDto filters) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("projectId"), filters.projectId());
    }
}
