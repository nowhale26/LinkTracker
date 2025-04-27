package backend.academy.scrapper.repository;

import backend.academy.scrapper.links.model.EnableDigestRequest;
import backend.academy.scrapper.repository.entity.Link;
import backend.academy.scrapper.repository.entity.User;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;

public interface LinkRepository {

    void save(Long tgChatId, Link link);

    void delete(Long tgChatId, Link link);

    void delete(Long tgChatId);

    Set<Link> get(Long tgChatId);

    void register(Long tgChatId);

    List<Link> getAllLinks();

    Page<Link> getPagedLinks(int pageNumber, int pageSize);

    Long getTgChatIdByLink(Link link);

    Long getTgChatIdById(Long id);

    void save(Long tgchatId, boolean enableTagInUpdates);

    void save(Long tgChatId, EnableDigestRequest request);

    User getUserByTgChatId(Long tgChatId);

    Set<User> getByEnabledDigest();
}
