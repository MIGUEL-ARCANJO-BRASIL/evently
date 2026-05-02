CREATE TABLE tb_event_subscription
(
    id                  UUID PRIMARY KEY,
    event_id            UUID         NOT NULL,
    user_name           VARCHAR(50)  NOT NULL,
    user_second_name    VARCHAR(100) NOT NULL,
    user_email          VARCHAR(255) NOT NULL,
    user_phone          VARCHAR(20)  NOT NULL,
    user_second_phone   VARCHAR(20),
    user_city           VARCHAR(100) NOT NULL,
    user_age_range      VARCHAR(20)  NOT NULL,
    checked_in          BOOLEAN          DEFAULT FALSE,
    check_in_date       TIMESTAMP,
    payment_method      VARCHAR(30)  NOT NULL,
    paid_value          DECIMAL(10, 2),
    subscription_date   TIMESTAMP    NOT NULL,
    subscription_status VARCHAR(20),
    CONSTRAINT fk_event
        FOREIGN KEY (event_id) REFERENCES tb_event (id)
);


