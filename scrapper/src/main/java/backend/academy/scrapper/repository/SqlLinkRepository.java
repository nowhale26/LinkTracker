package backend.academy.scrapper.repository;

import backend.academy.scrapper.repository.entity.Link;
import backend.academy.scrapper.repository.entity.User;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
public class SqlLinkRepository implements LinkRepository {

    private final JdbcTemplate jdbcTemplate;

    public SqlLinkRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Long getOrCreateUserId(Long tgChatId) {
        String sql =
                """
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
            jdbcTemplate.update(
                    updateSql, Timestamp.from(link.getLastUpdated().toInstant()), link.getSiteName(), existingLinkId);
            linkId = existingLinkId;
        } else {
            String insertSql =
                    "INSERT INTO links (user_id, url, last_updated, site_name) VALUES (?, ?, ?, ?) RETURNING id";
            linkId = jdbcTemplate.queryForObject(
                    insertSql,
                    Long.class,
                    userId,
                    link.getUrl(),
                    Timestamp.from(link.getLastUpdated().toInstant()),
                    link.getSiteName());
        }

        if (link.getFilters() != null) {
            if (existingLinkId != null) {
                jdbcTemplate.update("DELETE FROM filters WHERE link_id = ?", linkId);
            }
            for (var filter : link.getFilters()) {
                jdbcTemplate.update(
                        "INSERT INTO filters (link_id, filter) VALUES (?, ?) ON CONFLICT DO NOTHING",
                        linkId,
                        filter.getFilter());
            }
        }
        if (link.getTags() != null) {
            if (existingLinkId != null) {
                jdbcTemplate.update("DELETE FROM tags WHERE link_id = ?", linkId);
            }
            for (var tag : link.getTags()) {
                jdbcTemplate.update(
                        "INSERT INTO tags (link_id, tag) VALUES (?, ?) ON CONFLICT DO NOTHING", linkId, tag.getTag());
            }
        }
    }

    @Override
    public void delete(Long tgChatId, Link link) {
        String sql =
                """
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
        String linkSql =
                """
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

    @Override
    public List<Link> getAllLinks() {
        String sql = "SELECT * FROM links";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Link link = new Link();
            link.setId(rs.getLong("id"));
            link.setUserId(rs.getLong("user_id"));
            link.setUrl(rs.getString("url"));
            link.setLastUpdated(rs.getTimestamp("last_updated").toInstant().atZone(java.time.ZoneId.systemDefault()));
            link.setSiteName(rs.getString("site_name"));
            return link;
        });
    }

    @Override
    public Page<Link> getPagedLinks(int pageNumber, int pageSize) {
        int offset = (pageNumber - 1) * pageSize;

        String countSql = "SELECT COUNT(*) FROM links";
        long totalLinks = jdbcTemplate.queryForObject(countSql, Long.class);

        String sql = "SELECT * FROM links LIMIT ? OFFSET ?";
        List<Link> links = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {
                    Link link = new Link();
                    link.setId(rs.getLong("id"));
                    link.setUserId(rs.getLong("user_id"));
                    link.setUrl(rs.getString("url"));
                    link.setLastUpdated(
                            rs.getTimestamp("last_updated").toInstant().atZone(java.time.ZoneId.systemDefault()));
                    link.setSiteName(rs.getString("site_name"));
                    return link;
                },
                pageSize,
                offset);
        return new PageImpl<>(links, PageRequest.of(pageNumber - 1, pageSize), totalLinks);
    }

    @Override
    public Long getTgChatIdByLink(Link link) {
        Long userID = link.getUserId();
        String sql = """
            SELECT tg_chat_id
            FROM users
            WHERE id = ?
            """;

        return jdbcTemplate.queryForObject(sql, Long.class, userID);
    }

    @Override
    public Long getTgChatIdById(Long id) {
        String sql = "SELECT tg_chat_id FROM users WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, id);
    }

    @Override
    public void save(Long tgchatId, boolean enableTagInUpdates) {}

    @Override
    public User getUserByTgChatId(Long tgChatId) {
        return null;
    }
}
