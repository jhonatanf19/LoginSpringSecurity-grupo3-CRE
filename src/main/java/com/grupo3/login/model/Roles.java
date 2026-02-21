package com.grupo3.login.model;

// Colocamos los únicos roles que estaremos usando.
public enum Roles {
    USUARIO,
    ADMINISTRADOR
}

/* Importante:
 * El enum sirve para restringir los roles permitidos,
 * evitando que se ingresen roles inexistentes en la BD. */