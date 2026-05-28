--liquibase formatted sql

--changeset hector:1
CREATE TABLE inventario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    producto_id BIGINT NOT NULL UNIQUE,
    cantidad INT NOT NULL
);

--changeset hector:2
INSERT INTO inventario (producto_id, cantidad) VALUES
(1, 50),  -- Taladro Percutor
(2, 120), -- Martillo Carpintero
(3, 200), -- Destornillador Cruz
(4, 15),  -- Sierra Circular
(5, 85),  -- Huincha de medir 5m
(6, 60),  -- Set Llaves Allen
(7, 30),  -- Esmeril Angular
(8, 150), -- Alicate Universal
(9, 45),  -- Nivel de Burbuja
(10, 10) -- Soldadora Inverter