package com.grupo3.login.model;

/* Colocamos los roles permitidos en el sistema.
 * El uso de este Enum restringe las opciones disponibles, evitando errores
 * al asignar permisos o el ingreso de roles inexistentes en la base de datos. */
public enum Roles {
    USUARIO,
    ADMINISTRADOR
}
