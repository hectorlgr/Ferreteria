--liquibase formatted sql

--changeset hector:1
CREATE TABLE despachos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    venta_id BIGINT NOT NULL UNIQUE,
    direccion VARCHAR(255) NOT NULL,
    estado VARCHAR(50) NOT NULL
);

--changeset hector:2
INSERT INTO despachos (venta_id, direccion, estado) VALUES
(1, 'Av. Providencia 1234, Depto 54', 'EN_RUTA');