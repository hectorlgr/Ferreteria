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
(1, 'Av. Providencia 1234, Dpto 54', 'PREPARANDO'),
(2, 'Calle Los Pinos 5678, Casa', 'PREPARANDO'),
(3, 'Av. Las Condes 4321, Oficina', 'PREPARANDO'),
(4, 'Calle Falsa 123, Dpto 456', 'PREPARANDO'),
(5, 'Av. Siempre Viva 742, Dpto 742', 'EN_RUTA'),
(6, 'Calle Principal 456, Oficina', 'EN_RUTA'),
(7, 'Av. Central 789, Casa', 'EN_RUTA'),
(8, 'Calle Secundaria 321, Oficina', 'ENTREGADO'),
(9, 'Av. Las Flores 654, Casa', 'ENTREGADO'),
(10, 'Calle del Sol 987, Casa', 'ENTREGADO');