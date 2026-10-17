import { apiJson, apiBlob } from "./http";

export async function obtenerLiquidaciones() {
    return apiJson("/liquidaciones", {}, "Error al obtener liquidaciones");
}

export async function calcularLiquidacion(datos) {
    return apiJson("/liquidaciones", {
        method: "POST",
        body: JSON.stringify(datos),
    }, "Error al calcular la liquidación");
}

export async function descargarPdfLiquidacion(id) {
    await apiBlob(`/liquidaciones/${id}/pdf`, `liquidacion-${id}.pdf`, "No se pudo descargar el PDF");
}
