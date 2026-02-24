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
    cuenta_bloqueada TINYINT(1) DEFAULT 0
);

# Todos los registros de la tabla usuario
SELECT * FROM tbl_usuario;

# Creamos la tabla tokens_recuperacion_password
CREATE TABLE tokens_recuperacion_password (
    id_token BIGINT AUTO_INCREMENT PRIMARY KEY,
    -- token_recuperacion: Código UUID único para el correo
    token_recuperacion VARCHAR(255) NOT NULL UNIQUE,
    -- usuario_id: FK hacia tbl_usuario. 
    -- IMPORTANTE: El UNIQUE garantiza la relación OneToOne (un usuario, un token)
    usuario_id BIGINT NOT NULL UNIQUE,
    fecha_expiracion DATETIME NOT NULL,
    -- Relación de integridad: Si se borra el usuario, se borra su token
    CONSTRAINT fk_token_usuario 
        FOREIGN KEY (usuario_id) 
        REFERENCES tbl_usuario(id_usuario) 
        ON DELETE CASCADE
);

# Todos los registros de la tabla usuario
SELECT * FROM tokens_recuperacion_password;