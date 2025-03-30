package backend.academy.scrapper.repository;

import backend.academy.scrapper.repository.entity.Link;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaLinksRepository extends JpaRepository<Link, Long> {
    Set<Link> findByUserId(Long userId);

    void deleteByUserIdAndUrl(Long userId, String url);

    Link findByUserIdAndUrl(Long userId, String url);
    // @Query("SELECT l FROM Link l LEFT JOIN FETCH l.tags LEFT JOIN FETCH l.filters")
    Page<Link> findAll(Pageable pageable);

    @Query("SELECT l.userId FROM Link l WHERE l.url = :url")
    Set<Long> findUserIdsByUrl(@Param("url") String url);
}
