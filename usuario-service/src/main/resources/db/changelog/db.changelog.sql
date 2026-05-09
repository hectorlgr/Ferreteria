--liquibase formatted sql

--changeset hector:1
CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE
);

--changeset hector:2
INSERT INTO usuarios (nombre, email) VALUES
('Hector Gutierrez', 'hg.admin@ferreteria.cl'),
('Pablo Catalan', 'pc.admin@ferreteria.cl'),
('Ambar Uzcategui', 'au.operaciones@ferreteria.cl'),
('Eduardo Valbuena', 'eduardo.valbuena@gmail.com'),
('Kristian Camacho', 'kristian.camacho@gmail.com'),
('Macarena Garcia', 'macarena.garcia@gmail.com'),
('Pedro Diaz', 'pedro.diaz@gmail.com'),
('Pedro Rojas', 'pedro.rojas@gmail.com'),
('Diego Silva', 'diego.silva@gmail.com'),
('Jorge Castro', 'jorge.castro@gmail.com');