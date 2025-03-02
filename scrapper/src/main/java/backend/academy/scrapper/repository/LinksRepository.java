package backend.academy.scrapper.repository;

import java.util.Set;

public interface LinksRepository {

    void save(Long userId, Link link);

    void delete(Long userId, Link link);

    void delete(Long userId);

    Set<Link> get(Long userId);

    void register(Long userId);
}
