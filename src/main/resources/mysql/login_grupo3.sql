# Creamos la BD y la usamos
CREATE DATABASE login_grupo3;
Use login_grupo3;

# Creamos la tabla usuario
CREATE TABLE tbl_usuario (
    id_usuario BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(150) NOT NULL UNIQUE,
    -- Le damos 255 de espacio porque BCrypt genera hashes largos
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(50) NOT NULL,
    intentos_fallidos INT DEFAULT 0,
    -- cuenta_bloqueada (0 = false, 1 = true)
    cuenta_bloqueada TINYINT(1) DEFAULT 0,
    token_recuperacion VARCHAR(255)
);

# Estructura de la tabla usuario
DESCRIBE tbl_usuario;

# Todos los registros de la tabla usuario
SELECT * FROM tbl_usuario;
#TRUNCATE tbl_usuario;