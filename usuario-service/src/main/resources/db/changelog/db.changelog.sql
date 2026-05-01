--liquibase formatted sql

--changeset hector:1
CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    apellido VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(50) NOT NULL
);

--changeset hector:2
INSERT INTO usuarios (nombre, apellido, email, password, rol) VALUES
('Hector', 'Gutierrez', 'hg.admin@ferreteria.cl', '123456', 'ADMIN'),
('Pablo', 'Catalan', 'pc.admin@ferreteria.cl', '123456', 'ADMIN'),
('Ambar', 'Uzcategui', 'au.operaciones@ferreteria.cl', '123456', 'OPERADOR'),
('Eduardo', 'Valbuena', 'eduardo.valbuena@gmail.com', '123456', 'CLIENTE'),
('Kristian', 'Camacho', ' kristian.camacho@gmail.com', '123456', 'CLIENTE'),
('Macarena', 'Garcia', 'macarena.garcia@gmail.com', '123456', 'CLIENTE'),
('Pedro', 'Diaz', 'pedro.diaz@gmail.com', '123456', 'CLIENTE'),
('Pedro', 'Rojas', 'pedro.rojas@gmail.com', '123456', 'CLIENTE'),
('Diego', 'Silva', 'diego.silva@gmail.com', '123456', 'CLIENTE'),
('Jorge', 'Castro', 'jorge.castro@gmail.com', '123456', 'CLIENTE');