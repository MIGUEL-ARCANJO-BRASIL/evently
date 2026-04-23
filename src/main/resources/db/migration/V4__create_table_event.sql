CREATE TABLE tb_event
(
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title           VARCHAR(255) NOT NULL,
    description     TEXT,
    event_date       TIMESTAMP    NOT NULL,
    location        VARCHAR(255) NOT NULL,
    total_slots     INTEGER      NOT NULL,
    available_slots INTEGER      NOT NULL,
    cover_image     VARCHAR(255),
    event_status    VARCHAR(50)  NOT NULL,
    category_id     UUID REFERENCES tb_category (id),
    organizer_id    UUID REFERENCES tb_user (id)
);