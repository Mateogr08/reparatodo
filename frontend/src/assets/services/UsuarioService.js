import { api, apiJson, leerError } from "./http";

export async function obtenerUsuarios() {
    return apiJson("/usuarios", {}, "Error al obtener los usuarios");
}

export async function crearUsuario(usuario) {
    return apiJson("/usuarios", {
        method: "POST",
        body: JSON.stringify(usuario),
    }, "Error al crear el usuario");
}

export async function actualizarUsuario(id, usuario) {
    return apiJson(`/usuarios/${id}`, {
        method: "PUT",
        body: JSON.stringify(usuario),
    }, "Error al actualizar el usuario");
}

export async function eliminarUsuario(id) {
    const respuesta = await api(`/usuarios/${id}`, { method: "DELETE" });
    if (!respuesta.ok) {
        throw new Error(await leerError(respuesta, "Error al eliminar el usuario"));
    }
}
