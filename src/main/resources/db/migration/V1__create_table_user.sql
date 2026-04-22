CREATE TABLE tb_user (
                         id UUID NOT NULL,
                         name VARCHAR(255) NOT NULL,
                         email VARCHAR(255) NOT NULL UNIQUE,
                         password VARCHAR(255),
                         cpf VARCHAR(14) NOT NULL UNIQUE,
                         role VARCHAR(50) NOT NULL,

                         CONSTRAINT pk_tb_user PRIMARY KEY (id)
);