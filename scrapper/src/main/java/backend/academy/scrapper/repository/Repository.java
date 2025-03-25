package backend.academy.scrapper.repository;

import backend.academy.scrapper.common.exception.BusinessException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import backend.academy.scrapper.repository.entity.Link;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Repository implements LinkRepository {

    private HashMap<Long, Set<Link>> repository = new HashMap<>();

    @Override
    public void save(Long userId, Link link) {
        Set<Link> links;
        if (repository.containsKey(userId)) {
            links = repository.get(userId);
            if (links.contains(link)) {
                links.remove(link);
            }
        } else {
            links = new HashSet<>();
        }
        links.add(link);
        repository.put(userId, links);
    }

    @Override
    public void delete(Long userId, Link link) {
        Set<Link> links = repository.get(userId);
        boolean removedLink = links.remove(link);
        if (!removedLink) {
            throw new BusinessException("Данная ссылка не найдена в базе данных", "404", "Ссылка не найдена");
        }
        repository.put(userId, links);
    }

    @Override
    public void delete(Long userId) {
        var removed = repository.remove(userId);
        if (removed == null) {
            throw new BusinessException("Чат с таким id не существует в базе данных", "404", "Чат не существует");
        }
    }

    @Override
    public Set<Link> get(Long userId) {
        return repository.get(userId);
    }

    @Override
    public void register(Long userId) {
        Set<Link> links = new HashSet<>();
        repository.put(userId, links);
    }

    public HashMap<Long, Set<Link>> getRepository() {
        return repository;
    }
}
