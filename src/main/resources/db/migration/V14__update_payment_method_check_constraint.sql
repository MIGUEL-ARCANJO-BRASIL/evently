-- Atualiza a restrição de verificação (CHECK constraint) para incluir o novo método de pagamento GRATUITO
DO $$
BEGIN
    -- Remove a restrição antiga se ela existir
    IF EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'tb_event_transaction_payment_method_check') THEN
        ALTER TABLE tb_event_transaction DROP CONSTRAINT tb_event_transaction_payment_method_check;
    END IF;

    -- Note: Se o Hibernate recriar a restrição, ele usará os valores atuais do Enum.
    -- Para garantir que o banco aceite 'GRATUITO', podemos adicionar uma nova restrição ou apenas deixar sem se confiarmos na validação do Java.
    -- No entanto, para seguir o padrão do Hibernate, vamos adicionar uma que inclua GRATUITO.
    ALTER TABLE tb_event_transaction 
    ADD CONSTRAINT tb_event_transaction_payment_method_check 
    CHECK (payment_method IN ('CARTAO_CREDITO', 'CARTAO_DEBITO', 'PIX', 'BOLETO', 'GRATUITO'));

END $$;

-- Também precisamos garantir que a tabela de inscrições aceite o novo valor, caso tenha uma restrição similar
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'tb_event_subscription_payment_method_check') THEN
        ALTER TABLE tb_event_subscription DROP CONSTRAINT tb_event_subscription_payment_method_check;
    END IF;

    ALTER TABLE tb_event_subscription 
    ADD CONSTRAINT tb_event_subscription_payment_method_check 
    CHECK (payment_method IN ('CARTAO_CREDITO', 'CARTAO_DEBITO', 'PIX', 'BOLETO', 'GRATUITO'));
END $$;
