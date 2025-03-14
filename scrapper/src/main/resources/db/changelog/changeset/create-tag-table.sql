CREATE TABLE tags (
    id SERIAL PRIMARY KEY,
    link_id INTEGER NOT NULL,
    tag TEXT NOT NULL,
    FOREIGN KEY (link_id) REFERENCES links(id) ON DELETE CASCADE,
    UNIQUE (link_id, tag)
);
