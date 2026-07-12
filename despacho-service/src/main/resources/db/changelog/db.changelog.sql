--liquibase formatted sql

--changeset hector:1
CREATE TABLE despachos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id BIGINT NOT NULL UNIQUE,
    direccion VARCHAR(255) NOT NULL,
    estado VARCHAR(50) NOT NULL
);

--changeset hector:2
INSERT INTO despachos (pedido_id, direccion, estado) VALUES
(1, 'Av. Providencia 1234, Santiago', 'ENTREGADO'),
(2, 'Calle Las Araucarias 456, Maipú', 'ENTREGADO'),
(3, 'Av. Apoquindo 7890, Las Condes', 'ENTREGADO'),
(4, 'Pasaje Los Pinos 112, Puente Alto', 'ENTREGADO'),
(5, 'Gran Avenida 4321, San Miguel', 'ENTREGADO'),
(6, 'Av. Providencia 1234, Santiago', 'EN_RUTA'),
(7, 'Calle del Sol 888, La Florida', 'EN_RUTA'),
(8, 'Calle Las Araucarias 456, Maipú', 'PREPARANDO_PAQUETE'),
(9, 'Pasaje Nueva Esperanza 55, Renca', 'RECIBIDO_EN_BODEGA'),
(10, 'Av. Providencia 1234, Santiago', 'RECIBIDO_EN_BODEGA');

--changeset hector:3
ALTER TABLE despachos AUTO_INCREMENT = 100;