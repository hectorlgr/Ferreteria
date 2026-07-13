@echo off
echo Limpiando carpetas target de todos los microservicios...

echo Limpiando eureka-server...
cd eureka-server
call .\mvnw clean
cd ..

echo Limpiando api-gateway...
cd api-gateway
call .\mvnw clean
cd ..

echo Limpiando auth-service...
cd auth-service
call .\mvnw clean
cd ..

echo Limpiando usuario-service...
cd usuario-service
call .\mvnw clean
cd ..

echo Limpiando catalogo-service...
cd catalogo-service
call .\mvnw clean
cd ..

echo Limpiando venta-service...
cd venta-service
call .\mvnw clean
cd ..

echo Limpiando pedido-service...
cd pedido-service
call .\mvnw clean
cd ..

echo Limpiando despacho-service...
cd despacho-service
call .\mvnw clean
cd ..

echo Limpiando inventario-service...
cd inventario-service
call .\mvnw clean
cd ..

echo Limpiando resena-service...
cd resena-service
call .\mvnw clean
cd ..

echo Limpiando promocion-service...
cd promocion-service
call .\mvnw clean
cd ..

echo Limpiando soporte-service...
cd soporte-service
call .\mvnw clean
cd ..

echo ¡Limpieza completada con exito!
pause