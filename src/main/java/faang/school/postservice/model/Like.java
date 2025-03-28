package faang.school.postservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Класс, представляющий сущность "Лайк" в системе.
 * Лайк может быть поставлен как к комментарию ({@link Comment}), так и к посту ({@link Post}).
 * Каждый лайк связан с конкретным пользователем через его идентификатор ({@link #userId}).
 *
 * <p>
 * Лайк содержит следующие атрибуты:
 * <ul>
 *     <li><b>id</b> - уникальный идентификатор лайка.</li>
 *     <li><b>userId</b> - идентификатор пользователя, который поставил лайк.</li>
 *     <li><b>comment</b> - комментарий, к которому относится лайк (если лайк поставлен к комментарию).</li>
 *     <li><b>post</b> - пост, к которому относится лайк (если лайк поставлен к посту).</li>
 *     <li><b>createdAt</b> - дата и время создания лайка.</li>
 * </ul>
 *
 * <p>
 *   Лайк может быть связан либо с комментарием, либо с постом, но не с обоими одновременно.
 *  @author gulnaz21
 *  @see Comment
 *  @see Post
 *  */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="likes")
public class Like {

    /**
     * Уникальный идентификатор лайка.
     * Генерируется автоматически
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * Идентификатор пользователя, который поставил лайк.
     */
    @Column(name="user_id", nullable = false)
    private Long userId;

    /**
     * Комментарий, к которому относится лайк.
     */
    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    /**
     * Пост, к которому относится лайк.
     */
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    /**
     * Дата и время создания лайка.
     * Заполняется автоматически при создании записи в базе данных.
     */
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
