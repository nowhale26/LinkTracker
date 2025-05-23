CREATE TABLE IF NOT EXISTS filters(
    id      BIGSERIAL PRIMARY KEY,
    link_id BIGINT NOT NULL,
    filter  TEXT   NOT NULL,
    FOREIGN KEY (link_id) REFERENCES links (id) ON DELETE CASCADE,
    UNIQUE (link_id, filter)
);
