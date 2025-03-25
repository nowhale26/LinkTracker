package backend.academy.scrapper.repository;

import backend.academy.scrapper.repository.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserRepository extends JpaRepository<User, Long> {
    User findByTgChatId(Long tgChatId);
    void deleteByTgChatId(Long tgChatId);
}
