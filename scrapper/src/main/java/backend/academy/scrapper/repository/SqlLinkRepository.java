package backend.academy.scrapper.repository;

import backend.academy.scrapper.repository.entity.Link;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class SqlLinkRepository implements LinkRepository {

    private final JdbcTemplate jdbcTemplate;

    public SqlLinkRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Long getOrCreateUserId(Long tgChatId) {
        String sql = """
                INSERT INTO users (tg_chat_id)
                VALUES (?)
                ON CONFLICT (tg_chat_id)
                DO UPDATE SET tg_chat_id = EXCLUDED.tg_chat_id
                RETURNING id
            """;
        return jdbcTemplate.queryForObject(sql, Long.class, tgChatId);
    }

    @Override
    public void save(Long tgChatId, Link link) {
        Long userId = getOrCreateUserId(tgChatId);
        String existingLinkSql = "SELECT id FROM links WHERE user_id = ? AND url = ?";

        Long existingLinkId = null;
        try {
            existingLinkId = jdbcTemplate.queryForObject(existingLinkSql, Long.class, userId, link.getUrl());
        } catch (EmptyResultDataAccessException e) {
            log.info("Ссылка еще не существует");
        }

        Long linkId;
        if (existingLinkId != null) {
            String updateSql = "UPDATE links SET last_updated = ?, site_name = ? WHERE id = ?";
            jdbcTemplate.update(updateSql,
                Timestamp.from(link.getLastUpdated().toInstant()),
                link.getSiteName(),
                existingLinkId
            );
            linkId = existingLinkId;
        } else {
            String insertSql = "INSERT INTO links (user_id, url, last_updated, site_name) VALUES (?, ?, ?, ?) RETURNING id";
            linkId = jdbcTemplate.queryForObject(insertSql, Long.class,
                userId,
                link.getUrl(),
                Timestamp.from(link.getLastUpdated().toInstant()),
                link.getSiteName()
            );
        }

        if (link.getFilters() != null) {
            if (existingLinkId != null) {
                jdbcTemplate.update("DELETE FROM filters WHERE link_id = ?", linkId);
            }
            for (var filter : link.getFilters()) {
                jdbcTemplate.update("INSERT INTO filters (link_id, filter) VALUES (?, ?) ON CONFLICT DO NOTHING",
                    linkId,
                    filter.getFilter()
                );
            }
        }
        if (link.getTags() != null) {
            if (existingLinkId != null) {
                jdbcTemplate.update("DELETE FROM tags WHERE link_id = ?", linkId);
            }
            for (var tag : link.getTags()) {
                jdbcTemplate.update("INSERT INTO tags (link_id, tag) VALUES (?, ?) ON CONFLICT DO NOTHING",
                    linkId,
                    tag.getTag()
                );
            }
        }

    }

    @Override
    public void delete(Long tgChatId, Link link) {
        String sql = """
                DELETE FROM links
                WHERE user_id = (SELECT id FROM users WHERE tg_chat_id = ?)
                AND url = ?
            """;
        jdbcTemplate.update(sql, tgChatId, link.getUrl());
    }

    @Override
    public void delete(Long tgChatId) {
        String sql = "DELETE FROM users WHERE tg_chat_id = ?";
        jdbcTemplate.update(sql, tgChatId);
    }

    @Override
    public Set<Link> get(Long tgChatId) {
        String linkSql = """
                SELECT l.url
                FROM links l
                JOIN users u ON l.user_id = u.id
                WHERE u.tg_chat_id = ?
            """;
        List<String> urls = jdbcTemplate.query(linkSql, (rs, rowNum) -> rs.getString("url"), tgChatId);

        Set<Link> links = new HashSet<>();
        for (String url : urls) {
            Link link = new Link();
            link.setUrl(url);
            links.add(link);
        }
        return links;
    }

    @Override
    public void register(Long tgChatId) {
        String sql = "INSERT INTO users (tg_chat_id) VALUES (?) ON CONFLICT DO NOTHING";
        jdbcTemplate.update(sql, tgChatId);
    }
}
