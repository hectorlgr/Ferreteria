--liquibase formatted sql

--changeset hector:1
CREATE TABLE pedidos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_usuario BIGINT NOT NULL,
    id_venta BIGINT NOT NULL,
    estado VARCHAR(50) NOT NULL,
    fecha_creacion DATETIME NOT NULL
);

--changeset hector:2
INSERT INTO pedidos (id_usuario, id_venta, estado, fecha_creacion) VALUES
(1, 1, 'COMPLETADO', '2026-05-01 10:00:00'),
(2, 2, 'COMPLETADO', '2026-05-02 11:30:00'),
(1, 3, 'COMPLETADO', '2026-05-03 14:15:00'),
(3, 4, 'COMPLETADO', '2026-05-04 09:20:00'),
(2, 5, 'COMPLETADO', '2026-05-05 16:45:00'),
(1, 6, 'EN_PROCESO', '2026-06-08 08:10:00'),
(4, 7, 'EN_PROCESO', '2026-06-09 12:00:00'),
(2, 8, 'CONFIRMADO', '2026-06-09 15:30:00'),
(3, 9, 'CONFIRMADO', '2026-06-09 18:00:00'),
(1, 10, 'CONFIRMADO', '2026-06-09 20:00:00');