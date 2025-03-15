package backend.academy.scrapper.repository;

import backend.academy.scrapper.common.exception.BusinessException;
import backend.academy.scrapper.repository.model.Link;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        String linkSql = "INSERT INTO links (user_id, url, last_updated, site_name) VALUES (?, ?, ?, ?) RETURNING id";
        try {
            Long linkId = jdbcTemplate.queryForObject(linkSql, Long.class, userId, link.getUrl(), Timestamp.from(link.getLastUpdated().toInstant()), link.getSiteName());

            if (link.getFilters() != null) {
                for (var filter : link.getFilters()) {
                    jdbcTemplate.update("INSERT INTO filters (link_id, filter) VALUES (?, ?) ON CONFLICT DO NOTHING", linkId, filter.getFilter());
                }
            }

            if (link.getTags() != null) {
                for (var tag : link.getTags()) {
                    jdbcTemplate.update("INSERT INTO tags (link_id, tag) VALUES (?, ?) ON CONFLICT DO NOTHING", linkId, tag.getTag());
                }
            }
        } catch (DataAccessException e) {
            System.err.println("SQL Error: " + e.getMessage());
            System.err.println("Root Cause: " + e.getRootCause());
            throw new BusinessException(e.getMessage(),"400", Arrays.toString(e.getStackTrace()));
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
