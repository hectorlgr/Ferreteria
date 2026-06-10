--liquibase formatted sql

--changeset hector:1
CREATE TABLE resenas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_producto BIGINT NOT NULL,
    id_usuario BIGINT NOT NULL,
    calificacion INT NOT NULL CHECK (calificacion >= 1 AND calificacion <= 5),
    comentario VARCHAR(500),
    fecha_resena DATETIME NOT NULL
);

--changeset hector:2
-- Insertando 10 registros de prueba asociados a los productos iniciales del catálogo
INSERT INTO resenas (id_producto, id_usuario, calificacion, comentario, fecha_resena) VALUES
(1, 1, 5, 'Excelente taladro, muy potente y cumple con lo prometido.', '2026-06-01 10:00:00'),
(1, 2, 4, 'Buen producto, pero el cable es un poco corto para mi gusto.', '2026-06-02 11:30:00'),
(2, 1, 5, 'El martillo es muy cómodo, liviano y resistente.', '2026-06-03 14:15:00'),
(3, 3, 3, 'Cumple su función básica, calidad acorde al precio pagado.', '2026-06-04 09:20:00'),
(4, 2, 5, 'La sierra circular es una bestia, cortó la madera sin esfuerzo.', '2026-06-05 16:45:00'),
(5, 1, 4, 'Buena huincha, los números se leen claro y se retrae bien.', '2026-06-06 08:10:00'),
(6, 4, 5, 'El set de llaves allen viene muy completo y en buen estuche.', '2026-06-07 12:00:00'),
(7, 2, 2, 'El esmeril se calentó muy rápido al primer uso, pediré garantía.', '2026-06-08 15:30:00'),
(8, 3, 5, 'Alicate de muy buena calidad, aísla bien y el agarre es firme.', '2026-06-08 18:00:00'),
(9, 1, 4, 'El nivel de burbuja es preciso y de buen material de aluminio.', '2026-06-08 20:00:00');