package backend.academy.scrapper.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
public class Repository implements LinksRepository {

    private HashMap<Long, Set<Link>> repository = new HashMap<>();

    @Override
    public void save(long userId, Link link) {
        Set<Link> links;
        if (repository.containsKey(userId)) {
            links = repository.get(userId);

        } else {
            links = new HashSet<>();
        }
        //links.add(link);
        repository.put(userId, links);
    }

    @Override
    public void delete(long userId, Link link) {
        Set<Link> links = repository.get(userId);
        links.remove(link);
        repository.put(userId, links);
    }

    @Override
    public void delete(long userId) {
        repository.remove(userId);
    }

    @Override
    public Set<Link> get(long userId) {
        return repository.get(userId);
    }

    @Override
    public void register(long userId) {
        Set<Link> links = new HashSet<>();
        repository.put(userId, links);
    }
}
