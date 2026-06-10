--liquibase formatted sql

--changeset hector:1
CREATE TABLE promociones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    porcentaje_descuento DOUBLE NOT NULL,
    estado BOOLEAN NOT NULL
);

--changeset hector:2
INSERT INTO promociones (codigo, porcentaje_descuento, estado) VALUES
('FERRETERIA10', 10.0, true),
('DESCUENTO20', 20.0, true),
('VERANO15', 15.0, true),
('MITAD50', 50.0, true),
('VIEJO5', 5.0, false);