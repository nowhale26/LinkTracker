package backend.academy.scrapper.repository;

import java.util.Set;

public interface LinksRepository {

    void save(long userId, Link link);
    void delete(long userId, Link link);
    void delete(long userId);
    Set<Link> get(long userId);
    void register(long userId);

}
