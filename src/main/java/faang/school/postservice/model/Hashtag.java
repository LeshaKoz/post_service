package faang.school.postservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "hashtag")
public class Hashtag extends BaseEntity {

    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @ManyToMany(mappedBy = "hashtags")
    private List<Post> posts;
}