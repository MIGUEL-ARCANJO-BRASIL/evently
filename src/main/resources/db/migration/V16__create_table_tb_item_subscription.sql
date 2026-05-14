CREATE TABLE IF NOT EXISTS tb_subscription_item
(
    id              UUID             NOT NULL,
    subscription_id UUID             NOT NULL,
    ticket_id       UUID             NOT NULL,
    quantity        INTEGER          NOT NULL,
    unit_price      DOUBLE PRECISION NOT NULL,

    CONSTRAINT pk_tb_subscription_item PRIMARY KEY (id),

    CONSTRAINT fk_subscription_item_on_subscription
        FOREIGN KEY (subscription_id)
            REFERENCES tb_event_subscription (id),

    CONSTRAINT fk_subscription_item_on_ticket
        FOREIGN KEY (ticket_id)
            REFERENCES tb_event_ticket (id)
);