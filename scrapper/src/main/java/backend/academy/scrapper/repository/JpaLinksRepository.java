package backend.academy.scrapper.repository;

import backend.academy.scrapper.repository.model.Link;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaLinksRepository extends JpaRepository<Link, Long> {
    Set<Link> findByUserId(Long userId);
    void deleteByUserIdAndUrl(Long userId, String url);
}
