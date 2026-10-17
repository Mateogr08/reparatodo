import { useEffect, useState } from "react";
import { obtenerEmpleados } from "../services/EmpleadoService";
import { obtenerAsistencias, registrarAsistencia } from "../services/AsistenciaService";
import "./Empleados.css";

function Asistencia() {
    const [empleados, setEmpleados] = useState([]);
    const [registros, setRegistros] = useState([]);
    const [empleadoId, setEmpleadoId] = useState("");
    const [fecha, setFecha] = useState(new Date().toISOString().slice(0, 10));
    const [presente, setPresente] = useState(true);
    const [observacion, setObservacion] = useState("");
    const [error, setError] = useState("");

    useEffect(() => {
        cargar();
    }, []);

    async function cargar() {
        try {
            const [listaEmpleados, listaAsistencias] = await Promise.all([
                obtenerEmpleados(),
                obtenerAsistencias(),
            ]);
            setEmpleados(listaEmpleados);
            setRegistros(listaAsistencias);
        } catch (err) {
            setError(err.message);
        }
    }

    async function handleSubmit(event) {
        event.preventDefault();
        setError("");
        if (!empleadoId) {
            setError("Selecciona un empleado");
            return;
        }
        try {
            await registrarAsistencia({ empleadoId: Number(empleadoId), fecha, presente, observacion });
            setObservacion("");
            await cargar();
        } catch (err) {
            setError(err.message);
        }
    }

    return (
        <div className="empleados-page">
            <header className="empleados-header">
                <h1>Asistencia</h1>
                <p className="empleados-subtitulo">Registra presencia o ausencia para el cálculo de liquidaciones.</p>
            </header>
            <section className="empleados-card">
                {error ? <p className="empleados-error">{error}</p> : null}
                <form className="empleados-form" onSubmit={handleSubmit}>
                    <fieldset>
                        <legend>Nuevo registro</legend>
                        <label>
                            Empleado
                            <select value={empleadoId} onChange={(e) => setEmpleadoId(e.target.value)}>
                                <option value="">Seleccionar</option>
                                {empleados.map((empleado) => (
                                    <option key={empleado.id} value={empleado.id}>{empleado.nombre}</option>
                                ))}
                            </select>
                        </label>
                        <label>
                            Fecha
                            <input type="date" value={fecha} onChange={(e) => setFecha(e.target.value)} />
                        </label>
                        <label>
                            Estado
                            <select value={presente ? "si" : "no"} onChange={(e) => setPresente(e.target.value === "si")}>
                                <option value="si">Presente</option>
                                <option value="no">Ausente</option>
                            </select>
                        </label>
                        <label>
                            Observación
                            <input value={observacion} onChange={(e) => setObservacion(e.target.value)} />
                        </label>
                    </fieldset>
                    <div className="empleados-acciones">
                        <button type="submit">Guardar asistencia</button>
                    </div>
                </form>
            </section>
            <section className="empleados-card">
                <h2>Registros</h2>
                <div className="empleados-tabla-wrap">
                    <table className="empleados-tabla">
                        <thead>
                            <tr>
                                <th>Empleado</th>
                                <th>Fecha</th>
                                <th>Estado</th>
                                <th>Observación</th>
                            </tr>
                        </thead>
                        <tbody>
                            {registros.map((registro) => (
                                <tr key={registro.id}>
                                    <td>{registro.empleado?.nombre}</td>
                                    <td>{registro.fecha}</td>
                                    <td>{registro.presente ? "Presente" : "Ausente"}</td>
                                    <td>{registro.observacion || "—"}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </section>
        </div>
    );
}

export default Asistencia;
