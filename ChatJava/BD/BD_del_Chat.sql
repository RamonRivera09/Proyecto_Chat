CREATE database chat_app;
USE chat_app;
CREATE TABLE usuarios (
  id INT AUTO_INCREMENT PRIMARY KEY,
  usuario VARCHAR(50) NOT NULL UNIQUE,
  contrasena VARCHAR(255) NOT NULL,
  foto VARCHAR(255) NULL,
  codigo VARCHAR(100) NOT NULL UNIQUE,
  correo VARCHAR(150) NULL,
  estado VARCHAR(50) NOT NULL,
  conectado VARCHAR(50) NOT NULL
);
