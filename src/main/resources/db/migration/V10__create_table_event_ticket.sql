CREATE TABLE IF NOT EXISTS tb_event_ticket
(
    id              UUID PRIMARY KEY,
    name            VARCHAR(255)   NOT NULL,
    value           DECIMAL(10, 2) NOT NULL,
    expiration_date DATE,
    quantity        INTEGER,
    event_id        UUID           NOT NULL, -- Chave estrangeira

    CONSTRAINT fk_ticket_event FOREIGN KEY (event_id) REFERENCES tb_event (id)
);

ALTER TABLE tb_event
DROP COLUMN value;