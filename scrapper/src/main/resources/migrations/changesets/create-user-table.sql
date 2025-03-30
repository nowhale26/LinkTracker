CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    tg_chat_id BIGINT UNIQUE NOT NULL,
    enable_tag_in_updates BOOLEAN DEFAULT FALSE NOT NULL
);
