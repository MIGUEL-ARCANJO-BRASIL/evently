-- Adicionando CASCADE DELETE para permitir a exclusão de eventos e seus dados vinculados

DO $$ 
DECLARE 
    r RECORD;
BEGIN
    -- 1. Drop FKs de tb_subscription_item (ticket_id e subscription_id)
    FOR r IN (SELECT constraint_name FROM information_schema.key_column_usage 
              WHERE table_name = 'tb_subscription_item' AND column_name IN ('ticket_id', 'subscription_id')) LOOP
        EXECUTE 'ALTER TABLE tb_subscription_item DROP CONSTRAINT ' || r.constraint_name;
    END LOOP;

    -- 2. Drop FKs de tb_event_transaction (subscription_id)
    FOR r IN (SELECT constraint_name FROM information_schema.key_column_usage 
              WHERE table_name = 'tb_event_transaction' AND column_name = 'subscription_id') LOOP
        EXECUTE 'ALTER TABLE tb_event_transaction DROP CONSTRAINT ' || r.constraint_name;
    END LOOP;

    -- 3. Drop FKs de tb_event_subscription (event_id)
    FOR r IN (SELECT constraint_name FROM information_schema.key_column_usage 
              WHERE table_name = 'tb_event_subscription' AND column_name = 'event_id') LOOP
        EXECUTE 'ALTER TABLE tb_event_subscription DROP CONSTRAINT ' || r.constraint_name;
    END LOOP;

    -- 4. Drop FKs de tb_event_ticket (event_id)
    FOR r IN (SELECT constraint_name FROM information_schema.key_column_usage 
              WHERE table_name = 'tb_event_ticket' AND column_name = 'event_id') LOOP
        EXECUTE 'ALTER TABLE tb_event_ticket DROP CONSTRAINT ' || r.constraint_name;
    END LOOP;
END $$;

-- Recriando com ON DELETE CASCADE
ALTER TABLE tb_subscription_item ADD CONSTRAINT fk_sub_item_ticket FOREIGN KEY (ticket_id) REFERENCES tb_event_ticket(id) ON DELETE CASCADE;
ALTER TABLE tb_subscription_item ADD CONSTRAINT fk_sub_item_subscription FOREIGN KEY (subscription_id) REFERENCES tb_event_subscription(id) ON DELETE CASCADE;
ALTER TABLE tb_event_transaction ADD CONSTRAINT fk_transaction_subscription FOREIGN KEY (subscription_id) REFERENCES tb_event_subscription(id) ON DELETE CASCADE;
ALTER TABLE tb_event_subscription ADD CONSTRAINT fk_subscription_event FOREIGN KEY (event_id) REFERENCES tb_event(id) ON DELETE CASCADE;
ALTER TABLE tb_event_ticket ADD CONSTRAINT fk_ticket_event FOREIGN KEY (event_id) REFERENCES tb_event(id) ON DELETE CASCADE;
