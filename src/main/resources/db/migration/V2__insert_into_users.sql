INSERT INTO tb_user (id, name, email, password, cpf, role)
VALUES (gen_random_uuid(),
        'Miguel Arcanjo',
        'miguelbrasildelima@email.com',
        '$2a$10$hBzTWL8q5hPIExEx29x7Ouh1zVWP/4AzY95cocX21aqTKWkNrA9g.',
        '05961055256',
        'ADMIN'),

       (gen_random_uuid(),
        'Euton Aguiar',
        'euton@gmail.com',
        '$2a$10$nCOq9oliIbXdq7sCLZn95u3tVS7j9gvxFfEdwZT2ChoWFnglkvsxe',
        '12345678901',
        'ADMIN'),

       (gen_random_uuid(),
        'Gabriel Halysson',
        'gabriel@gmail.com',
        '$2a$10$vTQULmbdSgvFHExO1EI9EeBZuOQyirEHHBDCe8PdFxMMx0J9wSuOG',
        '12345678902',
        'ADMIN');