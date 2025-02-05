package faang.school.postservice.model.post;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "hashtag")
public class Hashtag {

    @Id
    private String name;

    @ToString.Exclude
    @ManyToMany
    @JoinTable(
            name = "post_hashtag",
            joinColumns = @JoinColumn(name = "hashtag_name"),
            inverseJoinColumns = @JoinColumn(name = "post_id")
    )
    private List<Post> posts;
}
