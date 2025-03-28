package backend.academy.scrapper.repository;

import backend.academy.scrapper.repository.entity.Link;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.Set;

public interface LinkRepository {

    void save(Long tgChatId, Link link);

    void delete(Long tgChatId, Link link);

    void delete(Long tgChatId);

    Set<Link> get(Long tgChatId);

    void register(Long tgChatId);

    List<Link> getAllLinks();

    Page<Link> getPagedLinks(int pageNumber, int pageSize);

    List<Long> getTgChatIdsByLink(Link link);

    Long getTgChatIdById(Long id);

}
