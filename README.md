# Sistema Marketplace para Ferretería

## Descripción del Proyecto
Este proyecto consiste en el desarrollo del backend para un sistema de gestión integral de una Ferretería en formato Marketplace, construido bajo una robusta arquitectura de **microservicios** utilizando **Spring Boot** y **Java 21**. 

El sistema digitaliza y automatiza el ciclo de vida completo de las operaciones de la ferretería: desde la gestión del catálogo de productos y control de inventario, hasta el procesamiento de ventas, orquestación de despachos, aplicación de promociones y gestión de usuarios.

A nivel técnico, la solución implementa:
* **Patrón de Arquitectura CSR:** Clara separación de capas en `Controller` (enrutamiento REST), `Service` (lógica de negocio) y `Repository` (acceso a datos MySQL).
* **Comunicación REST:** Integración entre microservicios utilizando `WebClient` con manejo de excepciones y validación de reglas de dominio.
* **Calidad de Código:** Alta cobertura de pruebas unitarias implementadas con JUnit y Mockito (≥ 80% verificado mediante Jacoco).
* **Seguridad y Enrutamiento:** Centralización de las peticiones a través de un **API Gateway** dinámico y descubrimiento de servicios gestionado por **Netflix Eureka**.

---

## Integrantes
* Héctor Gutierrez
* Pablo Catalan

---

## Lista de Microservicios
El ecosistema está compuesto por los siguientes servicios:

1. **`eureka-server`** (Puerto: 8761): Servidor de descubrimiento de Spring Cloud.
2. **`api-gateway`** (Puerto: 9090): Punto único de entrada, enrutamiento y documentación centralizada.
3. **`catalogo-service`**: Gestión de información de los productos de la ferretería.
4. **`venta-service`**: Orquestación de compras, cálculos de totales y enlace con otros dominios.
5. **`usuario-service`**: Administración de perfiles de clientes.
6. **`pedido-service`**: Gestión logística de los pedidos generados.
7. **`promocion-service`**: Validación y aplicación de cupones de descuento.
8. **`resena-service`**: Sistema de calificaciones y comentarios de productos.
9. **`inventario-service`**: Descuento y adición de stock físico.
10. **`despacho-service`**: Gestión de envíos y cálculo de fletes.
11. **`auth-service`**: Generación y validación de tokens JWT para seguridad (Oauth2).

---

## Rutas del API Gateway
Todas las peticiones del frontend deben dirigirse al API Gateway (`http://localhost:9090`). El Gateway se encargará de rutear las peticiones internamente usando los predicados configurados:

| Dominio / Servicio | Ruta Expuesta en Gateway | URL Base Interna |
| :--- | :--- | :--- |
| **Catálogo** | `/api/productos/**` | `lb://catalogo-service` |
| **Ventas** | `/api/ventas/**` | `lb://venta-service` |
| **Usuarios** | `/api/usuarios/**` | `lb://usuario-service` |
| **Pedidos** | `/api/pedidos/**` | `lb://pedido-service` |
| **Promociones** | `/api/promociones/**` | `lb://promocion-service` |
| **Reseñas** | `/api/resenas/**` | `lb://resena-service` |
| **Inventario** | `/api/inventario/**` | `lb://inventario-service` |
| **Despachos** | `/api/despachos/**` | `lb://despacho-service` |
| **Autenticación** | `/auth/**` | `lb://auth-service` |

---

## Documentación Interoperable (Swagger / OpenAPI)
La documentación interactiva de toda la plataforma está centralizada. No es necesario visitar la documentación de cada microservicio por separado.

* **URL de Swagger UI:** [http://localhost:9090/doc/swagger-ui.html](http://localhost:9090/doc/swagger-ui.html)

---

## Instrucciones de Ejecución

### Prerrequisitos
* **Java Development Kit (JDK):** Versión 21 (para ejecución manual).
* **Maven:** Instalado en el sistema.
* **Docker y Docker Compose:** Instalados en el sistema (opción recomendada y automatizada).

### Opción 1: Despliegue Automatizado con Docker
Cada microservicio cuenta con su propio `Dockerfile` optimizado para empaquetar la aplicación. El proyecto incluye un archivo centralizado `docker-compose.yml` que orquesta la infraestructura de bases de datos (MySQL), las herramientas de soporte y el ciclo de vida de todos los contenedores de la red interna.

Para compilar, construir las imágenes y levantar todo el ecosistema de microservicios con un solo comando, ejecuta desde la raíz del proyecto:
Este comando descargará e instanciará los contenedores necesarios, configurará las redes virtuales internas y dejará el ecosistema completamente operativo sin configuraciones manuales adicionales.

Opción 2: Ejecución Manual Local
En caso de que no sea con docker, se puede usar el script automatizado local iniciar_servicios.bat si estás en entorno Windows.

Pruebas Unitarias y Cobertura (Jacoco)
Para verificar la estabilidad de las reglas de negocio y asegurar el cumplimiento de la cobertura obligatoria (superior o igual al 80%), puedes ejecutar el ciclo de pruebas de Maven en cualquiera de los módulos:
Los reportes de cobertura interactivos de Jacoco se generarán automáticamente y se pueden consultar abriendo el archivo target/site/jacoco/index.html en el navegador web de tu preferencia.
