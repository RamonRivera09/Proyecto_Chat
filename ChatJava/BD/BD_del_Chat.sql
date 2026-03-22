-- 1. Creamos la base de datos
CREATE DATABASE chat_app;

-- 2. Le decimos a MySQL que use esa base de datos
USE chat_app;

-- 3. Creamos la tabla de usuarios
CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario VARCHAR(50) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL
);