import { apiJson } from "./http";

export async function iniciarSesion(email, password) {
    return apiJson("/auth/login", {
        method: "POST",
        body: JSON.stringify({ email, password }),
    }, "No se pudo iniciar sesión");
}

export async function cerrarSesion() {
    try {
        await apiJson("/auth/logout", { method: "POST" }, "No se pudo cerrar sesión");
    } catch {
        // se limpia igual en el cliente
    }
}
