CREATE TABLE links (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    url TEXT NOT NULL,
    last_updated TIMESTAMP WITH TIME ZONE,
    site_name TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
