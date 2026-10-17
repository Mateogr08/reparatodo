import { apiJson } from "./http";

export async function obtenerBackups() {
    return apiJson("/backups", {}, "Error al listar copias de seguridad");
}

export async function crearBackup() {
    return apiJson("/backups", { method: "POST" }, "Error al crear la copia de seguridad");
}
