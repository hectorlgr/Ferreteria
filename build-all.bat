@echo off
echo Limpiando contenedores e imagenes anteriores...
FOR /f %%i IN ('docker ps -aq') DO docker rm -f %%i
FOR /f %%i IN ('docker images -aq') DO docker rmi -f %%i

echo Construyendo eureka-server...
cd eureka-server
call .\mvnw clean package -DskipTests
cd ..

echo Construyendo api-gateway...
cd api-gateway
call .\mvnw clean package -DskipTests
cd ..

echo Construyendo auth-service...
cd auth-service
call .\mvnw clean package -DskipTests
cd ..

echo Construyendo usuario-service...
cd usuario-service
call .\mvnw clean package -DskipTests
cd ..

echo Construyendo catalogo-service...
cd catalogo-service
call .\mvnw clean package -DskipTests
cd ..

echo Construyendo venta-service...
cd venta-service
call .\mvnw clean package -DskipTests
cd ..

echo Construyendo pedido-service...
cd pedido-service
call .\mvnw clean package -DskipTests
cd ..

echo Construyendo despacho-service...
cd despacho-service
call .\mvnw clean package -DskipTests
cd ..

echo Construyendo inventario-service...
cd inventario-service
call .\mvnw clean package -DskipTests
cd ..

echo Construyendo resena-service...
cd resena-service
call .\mvnw clean package -DskipTests
cd ..

echo Construyendo promocion-service...
cd promocion-service
call .\mvnw clean package -DskipTests
cd ..

echo Construyendo soporte-service...
cd soporte-service
call .\mvnw clean package -DskipTests
cd ..

echo ¡Todas las compilaciones terminadas con exito!
pause