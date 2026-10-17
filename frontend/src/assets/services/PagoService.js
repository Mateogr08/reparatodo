import { apiJson } from "./http";

export async function obtenerPagos() {
    return apiJson("/pagos", {}, "Error al obtener los pagos");
}

export async function emitirPago(datos) {
    return apiJson("/pagos", {
        method: "POST",
        body: JSON.stringify(datos),
    }, "Error al emitir el pago");
}
