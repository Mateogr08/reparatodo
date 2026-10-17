import { apiJson } from "./http";

export async function obtenerAsistencias(filtros = {}) {
    const parametros = new URLSearchParams();
    if (filtros.empleadoId) parametros.set("empleadoId", filtros.empleadoId);
    if (filtros.desde) parametros.set("desde", filtros.desde);
    if (filtros.hasta) parametros.set("hasta", filtros.hasta);
    const query = parametros.toString();
    return apiJson(`/asistencias${query ? `?${query}` : ""}`, {}, "Error al obtener asistencias");
}

export async function registrarAsistencia(datos) {
    return apiJson("/asistencias", {
        method: "POST",
        body: JSON.stringify(datos),
    }, "Error al registrar la asistencia");
}
