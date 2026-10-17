const API = "http://localhost:8080/api";

export async function leerError(respuesta, mensajePorDefecto) {
    try {
        const cuerpo = await respuesta.json();
        if (cuerpo?.mensaje) {
            return cuerpo.mensaje;
        }
    } catch {
        // sin JSON
    }
    return mensajePorDefecto;
}

export function authHeaders() {
    const token = localStorage.getItem("token");
    const headers = { "Content-Type": "application/json" };
    if (token) {
        headers.Authorization = `Bearer ${token}`;
    }
    return headers;
}

export async function api(path, opciones = {}) {
    const respuesta = await fetch(`${API}${path}`, {
        ...opciones,
        headers: {
            ...authHeaders(),
            ...(opciones.headers || {}),
        },
    });

    if (respuesta.status === 401) {
        localStorage.removeItem("token");
        localStorage.removeItem("usuario");
        throw new Error("Debe iniciar sesión");
    }

    return respuesta;
}

export async function apiJson(path, opciones = {}, mensajeError) {
    const respuesta = await api(path, opciones);
    if (!respuesta.ok) {
        throw new Error(await leerError(respuesta, mensajeError || "Error en la solicitud"));
    }
    if (respuesta.status === 204) {
        return null;
    }
    return respuesta.json();
}

export async function apiBlob(path, nombreArchivo, mensajeError) {
    const respuesta = await api(path);
    if (!respuesta.ok) {
        throw new Error(await leerError(respuesta, mensajeError || "No se pudo descargar el archivo"));
    }
    const blob = await respuesta.blob();
    const url = URL.createObjectURL(blob);
    const enlace = document.createElement("a");
    enlace.href = url;
    enlace.download = nombreArchivo;
    enlace.click();
    URL.revokeObjectURL(url);
}

export { API };
