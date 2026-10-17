import { useEffect, useState } from "react";
import { crearBackup, obtenerBackups } from "../services/BackupService";
import "./Empleados.css";

function Backups() {
    const [copias, setCopias] = useState([]);
    const [error, setError] = useState("");
    const [guardando, setGuardando] = useState(false);

    useEffect(() => {
        cargar();
    }, []);

    async function cargar() {
        try {
            setCopias(await obtenerBackups());
        } catch (err) {
            setError(err.message);
        }
    }

    async function crear() {
        setError("");
        setGuardando(true);
        try {
            await crearBackup();
            await cargar();
        } catch (err) {
            setError(err.message);
        } finally {
            setGuardando(false);
        }
    }

    return (
        <div className="empleados-page">
            <header className="empleados-header">
                <h1>Copias de seguridad</h1>
                <p className="empleados-subtitulo">
                    Genera un respaldo de la información y consulta la fecha y hora de cada copia. También se crea una automática a las 2:00.
                </p>
            </header>
            <section className="empleados-card">
                {error ? <p className="empleados-error">{error}</p> : null}
                <div className="empleados-acciones">
                    <button type="button" onClick={crear} disabled={guardando}>
                        {guardando ? "Creando..." : "Crear copia ahora"}
                    </button>
                </div>
            </section>
            <section className="empleados-card">
                <h2>Historial de respaldos</h2>
                <div className="empleados-tabla-wrap">
                    <table className="empleados-tabla">
                        <thead>
                            <tr>
                                <th>Fecha y hora</th>
                                <th>Archivo</th>
                                <th>Origen</th>
                                <th>Tamaño</th>
                            </tr>
                        </thead>
                        <tbody>
                            {copias.map((copia) => (
                                <tr key={copia.id}>
                                    <td>{copia.fechaHora?.replace("T", " ").slice(0, 19)}</td>
                                    <td>{copia.nombreArchivo}</td>
                                    <td>{copia.origen}</td>
                                    <td>{Math.round((copia.tamanoBytes || 0) / 1024)} KB</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </section>
        </div>
    );
}

export default Backups;
