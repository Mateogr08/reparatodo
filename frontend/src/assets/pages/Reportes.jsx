import { useState } from "react";
import { consultarReporte, exportarReportePdf } from "../services/ReporteService";
import "./Empleados.css";

function Reportes() {
    const hoy = new Date().toISOString().slice(0, 10);
    const [tipo, setTipo] = useState("NOMINA");
    const [desde, setDesde] = useState(hoy.slice(0, 8) + "01");
    const [hasta, setHasta] = useState(hoy);
    const [resultado, setResultado] = useState(null);
    const [error, setError] = useState("");

    async function consultar(event) {
        event.preventDefault();
        setError("");
        try {
            setResultado(await consultarReporte(tipo, desde, hasta));
        } catch (err) {
            setError(err.message);
        }
    }

    async function exportar() {
        setError("");
        try {
            await exportarReportePdf(tipo, desde, hasta);
        } catch (err) {
            setError(err.message);
        }
    }

    return (
        <div className="empleados-page">
            <header className="empleados-header">
                <h1>Reportes de administración</h1>
                <p className="empleados-subtitulo">Consulta asistencia, costos de nómina y descuentos, y expórtalos a PDF.</p>
            </header>
            <section className="empleados-card">
                {error ? <p className="empleados-error">{error}</p> : null}
                <form className="empleados-form" onSubmit={consultar}>
                    <fieldset>
                        <legend>Filtros</legend>
                        <label>
                            Tipo
                            <select value={tipo} onChange={(e) => setTipo(e.target.value)}>
                                <option value="ASISTENCIA">Asistencia</option>
                                <option value="NOMINA">Costos de nómina</option>
                                <option value="DESCUENTOS">Descuentos e impuestos</option>
                            </select>
                        </label>
                        <label>
                            Desde
                            <input type="date" value={desde} onChange={(e) => setDesde(e.target.value)} />
                        </label>
                        <label>
                            Hasta
                            <input type="date" value={hasta} onChange={(e) => setHasta(e.target.value)} />
                        </label>
                    </fieldset>
                    <div className="empleados-acciones">
                        <button type="submit">Consultar</button>
                        <button type="button" className="secundario" onClick={exportar}>Exportar PDF</button>
                    </div>
                </form>
            </section>
            {resultado ? (
                <section className="empleados-card">
                    <h2>Resultado</h2>
                    <div className="empleados-tabla-wrap">
                        <table className="empleados-tabla">
                            <tbody>
                                {Object.entries(resultado)
                                    .filter(([clave]) => clave !== "detalle")
                                    .map(([clave, valor]) => (
                                        <tr key={clave}>
                                            <th>{clave}</th>
                                            <td>{String(valor)}</td>
                                        </tr>
                                    ))}
                            </tbody>
                        </table>
                    </div>
                </section>
            ) : null}
        </div>
    );
}

export default Reportes;
