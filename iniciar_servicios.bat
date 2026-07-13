@echo off
title Orquestador de Microservicios - Ferreteria Online
cls
echo =====================================================================
echo    INICIANDO ECOSISTEMA DE MICROSERVICIOS - FERRETERIA
echo =====================================================================
echo.

:: ---------------------------------------------------------------------
:: PASO 1: EUREKA SERVER
:: ---------------------------------------------------------------------
echo [PASO 1/3] Levantando Servidor de Descubrimiento (Eureka)...
cd /d "%~dp0\eureka-server"
start "EUREKA-SERVER (Puerto 8761)" cmd /k "mvnw spring-boot:run"

echo Esperando a que Eureka este totalmente operativo (12 segundos)...
timeout /t 12 /nobreak
echo.

:: ---------------------------------------------------------------------
:: PASO 2: MICROSERVICIOS EN PARALELO
:: ---------------------------------------------------------------------
echo [PASO 2/3] Lanzando nucleo de Microservicios en paralelo...

cd /d "%~dp0\auth-service"
start "AUTH-SERVICE (Puerto 9096)" cmd /k "mvnw spring-boot:run"

cd /d "%~dp0\usuario-service"
start "USUARIO-SERVICE (Puerto 9092)" cmd /k "mvnw spring-boot:run"

cd /d "%~dp0\catalogo-service"
start "CATALOGO-SERVICE (Puerto 9091)" cmd /k "mvnw spring-boot:run"

cd /d "%~dp0\inventario-service"
start "INVENTARIO-SERVICE (Puerto 9093)" cmd /k "mvnw spring-boot:run"

cd /d "%~dp0\despacho-service"
start "DESPACHO-SERVICE (Puerto 9095)" cmd /k "mvnw spring-boot:run"

cd /d "%~dp0\venta-service"
start "VENTA-SERVICE (Puerto 9094)" cmd /k "mvnw spring-boot:run"

cd /d "%~dp0\resena-service"
start "RESENA-SERVICE (Puerto 9097)" cmd /k "mvnw spring-boot:run"

cd /d "%~dp0\pedido-service"
start "PEDIDO-SERVICE (Puerto 9098)" cmd /k "mvnw spring-boot:run"

cd /d "%~dp0\promocion-service"
start "PROMOCION-SERVICE (Puerto 9099)" cmd /k "mvnw spring-boot:run"

cd /d "%~dp0\soporte-service"
start "SOPORTE-SERVICE (Puerto 9100)" cmd /k "mvnw spring-boot:run"

echo Esperando a que los servicios se registren en Eureka (10 segundos)...
timeout /t 10 /nobreak
echo.

:: ---------------------------------------------------------------------
:: PASO 3: API GATEWAY
:: ---------------------------------------------------------------------
echo [PASO 3/3] Levantando API Gateway (Puerta de entrada)...
cd /d "%~dp0\api-gateway"
start "API-GATEWAY (Puerto 9090)" cmd /k "mvnw spring-boot:run"

echo.
echo =====================================================================
echo    ¡TODO EL ECOSISTEMA HA SIDO LANZADO!
echo    Revisa las ventanas independientes para verificar los logs.
echo =====================================================================
echo.
pause