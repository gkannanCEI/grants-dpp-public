@echo off
FOR /F "tokens=*" %%i IN (.env) DO SET %%i
mvn spring-boot:run