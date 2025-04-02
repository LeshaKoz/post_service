package faang.school.postservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Класс, представляющий сущность "Комментарий" в системе.
 * Комментарии могут оставлять пользователи под постами. Каждый комментарий содержит текст,
 * идентификатор автора, ссылку на пост, дату создания и обновления, а также может содержать
 * прикрепленные изображения (большое и маленькое).
 *
 * @author Zhltsk-V
 * @version 1.0
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "comment")
public class Comment {

    /**
     * Уникальный идентификатор комментария. Генерируется автоматически при сохранении в базу данных.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Текст комментария. Не может быть пустым и ограничен 4096 символами.
     */
    @Column(name = "content", nullable = false, length = 4096)
    private String content;

    /**
     * Идентификатор автора комментария. Не может быть null.
     */
    @Column(name = "author_id", nullable = false)
    private Long authorId;

    /**
     * Список лайков, оставленных под этим комментарием.
     * Лайки связаны с комментарием через отношение "один ко многим".
     */
    @OneToMany(mappedBy = "comment", orphanRemoval = true)
    private List<Like> likes;

    /**
     * Пост, к которому относится комментарий. Комментарий всегда связан с одним постом.
     * Связь реализована через отношение "многие к одному".
     */
    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    /**
     * Дата и время создания комментария. Заполняется автоматически при создании комментария.
     */
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Дата и время последнего обновления комментария. Заполняется автоматически при изменении комментария.
     */
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Ключ файла большого изображения, прикрепленного к комментарию.
     * Может быть null, если изображение не прикреплено.
     */
    @Column(name = "large_image_file_key")
    private String largeImageFileKey;

    /**
     * Ключ файла маленького изображения, прикрепленного к комментарию.
     * Может быть null, если изображение не прикреплено.
     */
    @Column(name = "small_image_file_key")
    private String smallImageFileKey;

    /**
     * Проверяет, принадлежит ли комментарий к указанному посту.
     *
     * @param postId идентификатор поста для проверки принадлежности
     * @return true если комментарий принадлежит указанному посту,
     *         false если пост не установлен или идентификатор не совпадает
     * @throws NullPointerException если переданный postId равен null
     */
    public boolean belongsToPost(Long postId) {
        return this.post != null && postId.equals(this.post.getId());
    }
}
