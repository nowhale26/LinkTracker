package backend.academy.scrapper.repository;

import backend.academy.scrapper.repository.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserRepository extends JpaRepository<User, Long> {
    Optional<User> findByTgChatId(Long tgChatId);

    void deleteByTgChatId(Long tgChatId);
}
