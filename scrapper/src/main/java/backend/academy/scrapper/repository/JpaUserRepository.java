package backend.academy.scrapper.repository;

import java.util.Optional;
import backend.academy.scrapper.repository.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserRepository extends JpaRepository<User, Long> {
    User findByTgChatId(Long tgChatId);
    void deleteByTgChatId(Long tgChatId);
}
