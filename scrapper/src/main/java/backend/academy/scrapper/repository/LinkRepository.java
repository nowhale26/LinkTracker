package backend.academy.scrapper.repository;

import backend.academy.scrapper.repository.model.Link;
import java.util.Set;

public interface LinkRepository {

    void save(Long tgChatId, Link link);

    void delete(Long tgChatId, Link link);

    void delete(Long tgChatId);

    Set<Link> get(Long tgChatId);

    void register(Long tgChatId);
}
