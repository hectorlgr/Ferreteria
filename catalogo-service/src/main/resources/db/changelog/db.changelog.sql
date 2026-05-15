--liquibase formatted sql

--changeset hector:1
CREATE TABLE productos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    descripcion VARCHAR(255),
    marca VARCHAR(255),
    precio INT NOT NULL,
    habilitado BOOLEAN NOT NULL DEFAULT TRUE
);

--changeset hector:2
INSERT INTO productos (nombre, descripcion, marca, precio, habilitado) VALUES
('Taladro Percutor', 'Taladro de 700W con percutor', 'Makita', 45000, TRUE),
('Martillo Carpintero', 'Martillo con mango de fibra de vidrio', 'Stanley', 8500, TRUE),
('Destornillador Cruz', 'Punta imantada 6x100mm', 'Bosch', 3500, TRUE),
('Sierra Circular', 'Sierra de 1500W disco 7-1/4', 'DeWalt', 85000, TRUE),
('Huincha de medir 5m', 'Cinta métrica retráctil', 'Stanley', 4000, TRUE),
('Set Llaves Allen', 'Set de 9 piezas hexagonales', 'Truper', 6000, TRUE),
('Esmeril Angular', 'Esmeril de 4-1/2 pulgadas 820W', 'Makita', 35000, TRUE),
('Alicate Universal', 'Alicate aislado 8 pulgadas', 'Bosch', 5500, TRUE),
('Nivel de Burbuja', 'Nivel de aluminio 14 pulgadas', 'Truper', 7000, TRUE),
('Soldadora Inverter', 'Soldadora 130 Amperios', 'Indura', 120000, TRUE);