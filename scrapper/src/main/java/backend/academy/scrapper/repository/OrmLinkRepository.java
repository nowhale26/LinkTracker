package backend.academy.scrapper.repository;

import backend.academy.scrapper.repository.entity.Link;
import backend.academy.scrapper.repository.entity.User;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

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

    @Override
    public void save(Long tgChatId, Link link) {
        Long userId = getOrCreateUserId(tgChatId);
        link.setUserId(userId);

        Link existingLink = jpaLinksRepository.findByUserIdAndUrl(userId, link.getUrl());

        if (existingLink != null) {
            existingLink.setFilters(link.getFilters());
            existingLink.setTags(link.getTags());

            if (existingLink.getFilters() != null) {
                existingLink.getFilters().forEach(filter -> filter.setLink(existingLink));
            }
            if (existingLink.getTags() != null) {
                existingLink.getTags().forEach(tag -> tag.setLink(existingLink));
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
}
