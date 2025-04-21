package backend.academy.scrapper.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tg_chat_id", unique = true, nullable = false)
    private Long tgChatId;

    @Column(name = "enable_tag_in_updates", nullable = false)
    private boolean enableTagInUpdates;

    @Column(name = "digest_time")
    private LocalTime digestTime;
}
