import { api, apiJson, leerError } from "./http";

export async function obtenerEmpleados() {
    return apiJson("/empleados", {}, "Error al obtener los empleados");
}

export async function crearEmpleado(empleado) {
    return apiJson("/empleados", {
        method: "POST",
        body: JSON.stringify(empleado),
    }, "Error al registrar el empleado");
}

export async function actualizarEmpleado(id, empleado) {
    return apiJson(`/empleados/${id}`, {
        method: "PUT",
        body: JSON.stringify(empleado),
    }, "Error al actualizar el empleado");
}

export async function eliminarEmpleado(id, rutConfirmado) {
    const parametros = new URLSearchParams({ rutConfirmado });
    const respuesta = await api(`/empleados/${id}?${parametros.toString()}`, { method: "DELETE" });
    if (!respuesta.ok) {
        throw new Error(await leerError(respuesta, "Error al eliminar el empleado"));
    }
}

export async function obtenerAuditoria(id) {
    return apiJson(`/empleados/${id}/auditoria`, {}, "Error al obtener la auditoría");
}
