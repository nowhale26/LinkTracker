package backend.academy.scrapper.repository;

import backend.academy.scrapper.common.exception.ScrapperException;
import backend.academy.scrapper.links.model.EnableDigestRequest;
import backend.academy.scrapper.repository.entity.Link;
import backend.academy.scrapper.repository.entity.User;
import jakarta.transaction.Transactional;
import java.time.LocalTime;
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
        return jpaUserRepository.findByTgChatId(tgChatId).map(User::getId).orElseGet(() -> {
            User newUser = new User();
            newUser.setTgChatId(tgChatId);
            return jpaUserRepository.save(newUser).getId();
        });
    }

    @Transactional
    @Override
    public void save(Long tgChatId, Link link) {
        Long userId = getOrCreateUserId(tgChatId);
        link.setUserId(userId);

        Link linkToSave = jpaLinksRepository
                .findByUserIdAndUrl(userId, link.getUrl())
                .map(existingLink -> {
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

                    return existingLink;
                })
                .orElseGet(() -> {
                    if (link.getFilters() != null) {
                        link.getFilters().forEach(filter -> filter.setLink(link));
                    }

                    if (link.getTags() != null) {
                        link.getTags().forEach(tag -> tag.setLink(link));
                    }

                    return link;
                });

        jpaLinksRepository.save(linkToSave);
    }

    @Transactional
    @Override
    public void delete(Long tgChatId, Link link) {
        jpaUserRepository
                .findByTgChatId(tgChatId)
                .map(User::getId)
                .ifPresent(userId -> jpaLinksRepository.deleteByUserIdAndUrl(userId, link.getUrl()));
    }

    @Override
    public void delete(Long tgChatId) {
        jpaUserRepository.deleteByTgChatId(tgChatId);
    }

    @Override
    public Set<Link> get(Long tgChatId) {
        return jpaUserRepository
                .findByTgChatId(tgChatId)
                .map(User::getId)
                .map(jpaLinksRepository::findByUserId)
                .orElseGet(HashSet::new);
    }

    @Override
    public void register(Long tgChatId) {
        if (jpaUserRepository.findByTgChatId(tgChatId).isEmpty()) {
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
        User userToSave = jpaUserRepository
                .findByTgChatId(tgChatId)
                .map(existingUser -> {
                    existingUser.setEnableTagInUpdates(enableTagInUpdates);
                    return existingUser;
                })
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setTgChatId(tgChatId);
                    newUser.setEnableTagInUpdates(enableTagInUpdates);
                    return newUser;
                });

        jpaUserRepository.save(userToSave);
    }

    @Override
    public void save(Long tgChatId, EnableDigestRequest request) {
        LocalTime digestTime;
        if(request.getEnableDigest()){
            digestTime = request.getDigestTime();
        } else {
            digestTime = null;
        }
        User userToSave = jpaUserRepository
            .findByTgChatId(tgChatId)
            .map(existingUser -> {
                existingUser.setDigestTime(digestTime);
                return existingUser;
            })
            .orElseGet(() -> {
                User newUser = new User();
                newUser.setTgChatId(tgChatId);
                newUser.setDigestTime(digestTime);
                return newUser;
            });

        jpaUserRepository.save(userToSave);
    }

    @Override
    public User getUserByTgChatId(Long tgChatId) {
        var user = jpaUserRepository.findByTgChatId(tgChatId);
        return user.orElse(null);
    }

    @Override
    public Set<User> getByEnabledDigest() {
        List<User> users = jpaUserRepository.findAll();
        Set<User> usersWithEnabledDigest = new HashSet<>();
        for(var user : users){
            if(user.getDigestTime()!=null){
                usersWithEnabledDigest.add(user);
            }
        }
        return usersWithEnabledDigest;
    }
}
