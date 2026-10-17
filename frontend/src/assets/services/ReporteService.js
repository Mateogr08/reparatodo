import { apiJson, apiBlob } from "./http";

export async function consultarReporte(tipo, desde, hasta) {
    const parametros = new URLSearchParams({ tipo, desde, hasta });
    return apiJson(`/reportes?${parametros}`, {}, "Error al generar el reporte");
}

export async function exportarReportePdf(tipo, desde, hasta) {
    const parametros = new URLSearchParams({ tipo, desde, hasta });
    await apiBlob(`/reportes/pdf?${parametros}`, `reporte-${tipo.toLowerCase()}.pdf`, "No se pudo exportar el PDF");
}
