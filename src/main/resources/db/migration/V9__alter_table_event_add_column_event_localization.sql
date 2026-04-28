ALTER TABLE tb_event
DROP
COLUMN location;

ALTER TABLE tb_event
    ADD COLUMN loc_cep VARCHAR(9),
    ADD COLUMN loc_address VARCHAR(255),
    ADD COLUMN loc_complement VARCHAR(100),
    ADD COLUMN loc_number VARCHAR(10),
    ADD COLUMN loc_city VARCHAR(100),
    ADD COLUMN loc_state VARCHAR(2),
    ADD COLUMN loc_neighborhood VARCHAR(100);