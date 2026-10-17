import { useEffect, useState } from "react";
import { obtenerEmpleados } from "../services/EmpleadoService";
import { calcularLiquidacion, descargarPdfLiquidacion, obtenerLiquidaciones } from "../services/LiquidacionService";
import { emitirPago, obtenerPagos } from "../services/PagoService";
import "./Empleados.css";

function moneda(valor) {
    return Number(valor || 0).toLocaleString("es-CO", { style: "currency", currency: "COP", maximumFractionDigits: 0 });
}

function Liquidaciones({ puedePagar = true }) {
    const [empleados, setEmpleados] = useState([]);
    const [liquidaciones, setLiquidaciones] = useState([]);
    const [pagos, setPagos] = useState([]);
    const [empleadoId, setEmpleadoId] = useState("");
    const [periodo, setPeriodo] = useState(new Date().toISOString().slice(0, 7));
    const [bonificaciones, setBonificaciones] = useState("0");
    const [error, setError] = useState("");
    const [pago, setPago] = useState({ liquidacionId: "", medio: "DEPOSITO", referencia: "" });

    useEffect(() => {
        cargar();
    }, []);

    async function cargar() {
        try {
            const [listaEmpleados, listaLiquidaciones] = await Promise.all([
                obtenerEmpleados().catch(() => []),
                obtenerLiquidaciones(),
            ]);
            setEmpleados(listaEmpleados);
            setLiquidaciones(listaLiquidaciones);
            if (puedePagar) {
                setPagos(await obtenerPagos().catch(() => []));
            }
        } catch (err) {
            setError(err.message);
        }
    }

    async function handleCalcular(event) {
        event.preventDefault();
        setError("");
        try {
            await calcularLiquidacion({
                empleadoId: Number(empleadoId),
                periodo,
                bonificaciones: Number(bonificaciones || 0),
            });
            await cargar();
        } catch (err) {
            setError(err.message);
        }
    }

    async function handlePago(event) {
        event.preventDefault();
        setError("");
        try {
            await emitirPago({
                liquidacionId: Number(pago.liquidacionId),
                medio: pago.medio,
                referencia: pago.referencia,
            });
            setPago({ liquidacionId: "", medio: "DEPOSITO", referencia: "" });
            await cargar();
        } catch (err) {
            setError(err.message);
        }
    }

    return (
        <div className="empleados-page">
            <header className="empleados-header">
                <h1>Liquidaciones y pagos</h1>
                <p className="empleados-subtitulo">
                    Calcula AFP, ISAPRE/EPS, impuestos y sueldo líquido según asistencia, genera el PDF y emite el pago.
                </p>
            </header>
            {error ? <p className="empleados-error">{error}</p> : null}

            {puedePagar ? (
                <section className="empleados-card">
                    <h2>Calcular liquidación</h2>
                    <form className="empleados-form" onSubmit={handleCalcular}>
                        <fieldset>
                            <legend>Periodo</legend>
                            <label>
                                Empleado
                                <select value={empleadoId} onChange={(e) => setEmpleadoId(e.target.value)} required>
                                    <option value="">Seleccionar</option>
                                    {empleados.map((empleado) => (
                                        <option key={empleado.id} value={empleado.id}>{empleado.nombre}</option>
                                    ))}
                                </select>
                            </label>
                            <label>
                                Periodo
                                <input type="month" value={periodo} onChange={(e) => setPeriodo(e.target.value)} required />
                            </label>
                            <label>
                                Bonificaciones
                                <input type="number" min="0" value={bonificaciones} onChange={(e) => setBonificaciones(e.target.value)} />
                            </label>
                        </fieldset>
                        <div className="empleados-acciones">
                            <button type="submit">Calcular</button>
                        </div>
                    </form>
                </section>
            ) : null}

            <section className="empleados-card">
                <h2>Liquidaciones</h2>
                <div className="empleados-tabla-wrap">
                    <table className="empleados-tabla">
                        <thead>
                            <tr>
                                <th>Empleado</th>
                                <th>Periodo</th>
                                <th>Días</th>
                                <th>Bruto</th>
                                <th>Bonificaciones</th>
                                <th>AFP</th>
                                <th>ISAPRE</th>
                                <th>Impuestos</th>
                                <th>Líquido</th>
                                <th>Estado</th>
                                <th></th>
                            </tr>
                        </thead>
                        <tbody>
                            {liquidaciones.map((item) => (
                                <tr key={item.id}>
                                    <td>{item.empleado?.nombre}</td>
                                    <td>{item.periodo}</td>
                                    <td>{item.diasTrabajados}</td>
                                    <td>{moneda(item.sueldoBruto)}</td>
                                    <td>{moneda(item.bonificaciones)}</td>
                                    <td>{moneda(item.afp)}</td>
                                    <td>{moneda(item.isapre)}</td>
                                    <td>{moneda(item.impuestos)}</td>
                                    <td>{moneda(item.sueldoLiquido)}</td>
                                    <td>{item.estado}</td>
                                    <td>
                                        <button type="button" className="secundario" onClick={() => descargarPdfLiquidacion(item.id)}>
                                            PDF
                                        </button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </section>

            {puedePagar ? (
                <>
                    <section className="empleados-card">
                        <h2>Emitir pago</h2>
                        <form className="empleados-form" onSubmit={handlePago}>
                            <fieldset>
                                <legend>Cheque o depósito</legend>
                                <label>
                                    Liquidación
                                    <select value={pago.liquidacionId} onChange={(e) => setPago({ ...pago, liquidacionId: e.target.value })} required>
                                        <option value="">Seleccionar</option>
                                        {liquidaciones.filter((item) => item.estado === "CALCULADA").map((item) => (
                                            <option key={item.id} value={item.id}>
                                                {item.empleado?.nombre} — {item.periodo}
                                            </option>
                                        ))}
                                    </select>
                                </label>
                                <label>
                                    Medio
                                    <select value={pago.medio} onChange={(e) => setPago({ ...pago, medio: e.target.value })}>
                                        <option value="DEPOSITO">Depósito bancario</option>
                                        <option value="CHEQUE">Cheque</option>
                                    </select>
                                </label>
                                <label>
                                    Referencia
                                    <input
                                        placeholder="Número de cheque o cuenta"
                                        value={pago.referencia}
                                        onChange={(e) => setPago({ ...pago, referencia: e.target.value })}
                                        required
                                    />
                                </label>
                            </fieldset>
                            <div className="empleados-acciones">
                                <button type="submit">Emitir pago</button>
                            </div>
                        </form>
                    </section>
                    <section className="empleados-card">
                        <h2>Pagos emitidos</h2>
                        <div className="empleados-tabla-wrap">
                            <table className="empleados-tabla">
                                <thead>
                                    <tr>
                                        <th>Fecha</th>
                                        <th>Empleado</th>
                                        <th>Medio</th>
                                        <th>Valor</th>
                                        <th>Referencia</th>
                                        <th>Resultado</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {pagos.map((item) => (
                                        <tr key={item.id}>
                                            <td>{item.fechaEmision?.replace("T", " ").slice(0, 19)}</td>
                                            <td>{item.liquidacion?.empleado?.nombre}</td>
                                            <td>{item.medio}</td>
                                            <td>{moneda(item.valor)}</td>
                                            <td>{item.referencia}</td>
                                            <td>{item.resultado}</td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    </section>
                </>
            ) : null}
        </div>
    );
}

export default Liquidaciones;
