const API_URL = "http://localhost:8080/api/empleados";

async function leerError(respuesta, mensajePorDefecto) {
    try {
        const cuerpo = await respuesta.json();
        if (cuerpo?.mensaje) {
            return cuerpo.mensaje;
        }
    } catch {
        // respuesta sin JSON
    }
    return mensajePorDefecto;
}

export async function obtenerEmpleados() {
    const respuesta = await fetch(API_URL);

    if (!respuesta.ok) {
        throw new Error(await leerError(respuesta, "Error al obtener los empleados"));
    }

    return await respuesta.json();
}

export async function crearEmpleado(empleado) {
    const respuesta = await fetch(API_URL, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(empleado),
    });

    if (!respuesta.ok) {
        throw new Error(await leerError(respuesta, "Error al registrar el empleado"));
    }

    return await respuesta.json();
}

export async function actualizarEmpleado(id, empleado) {
    const respuesta = await fetch(`${API_URL}/${id}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(empleado),
    });

    if (!respuesta.ok) {
        throw new Error(await leerError(respuesta, "Error al actualizar el empleado"));
    }

    return await respuesta.json();
}

export async function eliminarEmpleado(id, rutConfirmado) {
    const parametros = new URLSearchParams({ rutConfirmado });
    const respuesta = await fetch(`${API_URL}/${id}?${parametros.toString()}`, {
        method: "DELETE",
    });

    if (!respuesta.ok) {
        throw new Error(await leerError(respuesta, "Error al eliminar el empleado"));
    }
}
