--liquibase formatted sql

--changeset hector:1
CREATE TABLE ventas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    fecha DATETIME NOT NULL,
    total INT NOT NULL
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
-- Insertamos una venta de prueba inicial (El cliente ID 3 compró el Taladro ID 1)
INSERT INTO ventas (usuario_id, fecha, total) VALUES 
(3, NOW(), 45000);

--changeset hector:4
INSERT INTO detalle_ventas (venta_id, producto_id, cantidad, precio_unitario, subtotal) VALUES 
(1, 1, 1, 45000, 45000);