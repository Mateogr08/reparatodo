const API_URL = "http://localhost:8080/api/usuarios";

export async function obtenerUsuarios() {
    const respuesta = await fetch(API_URL);

    if (!respuesta.ok) {
        throw new Error("Error al obtener los usuarios");
    }

    return await respuesta.json();
}

export async function crearUsuario(usuario) {
    const respuesta = await fetch(API_URL, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(usuario),
    });

    if (!respuesta.ok) {
        throw new Error("Error al crear el usuario");
    }

    return await respuesta.json();
}