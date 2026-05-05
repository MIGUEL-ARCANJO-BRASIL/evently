-- Criação da tabela de transações (caso não exista) com deleção em cascata
CREATE TABLE IF NOT EXISTS tb_event_transaction (
    id UUID PRIMARY KEY,
    subscription_id UUID NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    payment_method VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL,
    gateway_transaction_id VARCHAR(255),
    processed_at TIMESTAMP,
    CONSTRAINT fk_subscription_transaction FOREIGN KEY (subscription_id) REFERENCES tb_event_subscription (id) ON DELETE CASCADE
);

-- Garante que a restrição de chave estrangeira tenha o ON DELETE CASCADE caso a tabela já exista
DO $$
BEGIN
    -- Remove a restrição gerada automaticamente pelo Hibernate se ela existir
    IF EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fkar4we3ufvfba6bq4rarwl7dpl') THEN
        ALTER TABLE tb_event_transaction DROP CONSTRAINT fkar4we3ufvfba6bq4rarwl7dpl;
    END IF;

    -- Tenta adicionar a nova restrição com cascata (se ainda não existir com este nome)
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_subscription_transaction') THEN
        ALTER TABLE tb_event_transaction 
        ADD CONSTRAINT fk_subscription_transaction 
        FOREIGN KEY (subscription_id) REFERENCES tb_event_subscription (id) 
        ON DELETE CASCADE;
    END IF;
END $$;
