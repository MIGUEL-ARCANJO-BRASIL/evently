ALTER TABLE tb_event_subscription ADD COLUMN payment_last_four VARCHAR(4);
ALTER TABLE tb_event_subscription ADD COLUMN payment_card_brand VARCHAR(20);
ALTER TABLE tb_event_subscription ADD COLUMN payment_token VARCHAR(255);