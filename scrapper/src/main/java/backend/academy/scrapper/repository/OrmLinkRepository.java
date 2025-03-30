package backend.academy.scrapper.repository;

import backend.academy.scrapper.common.exception.ScrapperException;
import backend.academy.scrapper.repository.entity.Link;
import backend.academy.scrapper.repository.entity.User;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public class OrmLinkRepository implements LinkRepository {

    private final JpaLinksRepository jpaLinksRepository;
    private final JpaUserRepository jpaUserRepository;

    public OrmLinkRepository(JpaLinksRepository jpaLinksRepository, JpaUserRepository jpaUserRepository) {
        this.jpaLinksRepository = jpaLinksRepository;
        this.jpaUserRepository = jpaUserRepository;
    }

    private Long getOrCreateUserId(Long tgChatId) {
        User existingUser = jpaUserRepository.findByTgChatId(tgChatId);
        if (existingUser != null) {
            return existingUser.getId();
        } else {
            User newUser = new User();
            newUser.setTgChatId(tgChatId);
            return jpaUserRepository.save(newUser).getId();
        }
    }

    @Transactional
    @Override
    public void save(Long tgChatId, Link link) {
        Long userId = getOrCreateUserId(tgChatId);
        link.setUserId(userId);

        Link existingLink = jpaLinksRepository.findByUserIdAndUrl(userId, link.getUrl());

        if (existingLink != null) {
            existingLink.setLastUpdated(link.getLastUpdated());
            if (link.getFilters() != null) {
                existingLink.getFilters().clear();
                link.getFilters().forEach(filter -> {
                    filter.setLink(existingLink);
                    existingLink.getFilters().add(filter);
                });
            }
            if (link.getTags() != null) {
                existingLink.getTags().clear();
                link.getTags().forEach(tag -> {
                    tag.setLink(existingLink);
                    existingLink.getTags().add(tag);
                });
            }

            jpaLinksRepository.save(existingLink);
        } else {
            if (link.getFilters() != null) {
                link.getFilters().forEach(filter -> filter.setLink(link));
            }
            if (link.getTags() != null) {
                link.getTags().forEach(tag -> tag.setLink(link));
            }
            jpaLinksRepository.save(link);
        }
    }

    @Transactional
    @Override
    public void delete(Long tgChatId, Link link) {
        Long userId = jpaUserRepository.findByTgChatId(tgChatId).getId();
        if (userId != null) {
            jpaLinksRepository.deleteByUserIdAndUrl(userId, link.getUrl());
        }
    }

    @Override
    public void delete(Long tgChatId) {
        jpaUserRepository.deleteByTgChatId(tgChatId);
    }

    @Override
    public Set<Link> get(Long tgChatId) {
        Long userId = jpaUserRepository.findByTgChatId(tgChatId).getId();
        if (userId != null) {
            return jpaLinksRepository.findByUserId(userId);
        }
        return new HashSet<>();
    }

    @Override
    public void register(Long tgChatId) {
        if (jpaUserRepository.findByTgChatId(tgChatId) == null) {
            User user = new User();
            user.setTgChatId(tgChatId);
            jpaUserRepository.save(user);
        }
    }

    @Override
    public List<Link> getAllLinks() {
        return jpaLinksRepository.findAll();
    }

    @Override
    public Page<Link> getPagedLinks(int pageNumber, int pageSize) {
        return jpaLinksRepository.findAll(PageRequest.of(pageNumber, pageSize));
    }

    @Override
    public Long getTgChatIdByLink(Link link) {
        Long userID = link.getUserId();
        return jpaUserRepository
                .findById(userID)
                .map(User::getTgChatId)
                .orElseThrow(() -> new ScrapperException("Id not found", "500", "DB exception"));
    }

    @Override
    public Long getTgChatIdById(Long id) {
        if (jpaUserRepository.findById(id).isPresent()) {
            return jpaUserRepository
                    .findById(id)
                    .map(User::getTgChatId)
                    .orElseThrow(() -> new ScrapperException("Id not found", "500", "DB exception"));
        }
        return null;
    }

    @Override
    public void save(Long tgChatId, boolean enableTagInUpdates) {
        User existingUser = jpaUserRepository.findByTgChatId(tgChatId);
        if (existingUser != null) {
            existingUser.setEnableTagInUpdates(enableTagInUpdates);
            jpaUserRepository.save(existingUser);
        } else {
            User user = new User();
            user.setTgChatId(tgChatId);
            user.setEnableTagInUpdates(enableTagInUpdates);
            jpaUserRepository.save(user);
        }
    }

    @Override
    public User getUserByTgChatId(Long tgChatId) {
        return jpaUserRepository.findByTgChatId(tgChatId);
    }
}
