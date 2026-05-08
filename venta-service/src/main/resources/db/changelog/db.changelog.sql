--liquibase formatted sql

--changeset hector:1
CREATE TABLE ventas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    fecha DATETIME NOT NULL,
    total INT NOT NULL,
    costo_despacho INT NOT NULL
);

--changeset hector:2
CREATE TABLE detalle_ventas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    venta_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario INT NOT NULL,
    subtotal INT NOT NULL,
    CONSTRAINT fk_venta FOREIGN KEY (venta_id) REFERENCES ventas(id)
);

--changeset hector:3
INSERT INTO ventas (usuario_id, fecha, total, costo_despacho) VALUES 
(1, NOW(), 46500, 1500),
(2, NOW(), 18500, 1500),
(3, NOW(), 5000, 1500),
(4, NOW(), 171500, 1500),
(5, NOW(), 5500, 1500),
(6, NOW(), 13500, 1500),
(7, NOW(), 36500, 1500),
(8, NOW(), 18000, 1500),
(9, NOW(), 15500, 1500),
(10, NOW(), 481500, 1500);

--changeset hector:4
INSERT INTO detalle_ventas (venta_id, producto_id, cantidad, precio_unitario, subtotal) VALUES 
(1, 1, 1, 45000, 45000),
(2, 2, 2, 8500, 17000),
(3, 3, 1, 3500, 3500),
(4, 4, 2, 85000, 170000),
(5, 5, 1, 4000, 4000),
(6, 6, 2, 6000, 12000),
(7, 7, 1, 35000, 35000),
(8, 8, 3, 5500, 16500),
(9, 9, 2, 7000, 14000),
(10, 10, 4, 120000, 480000);