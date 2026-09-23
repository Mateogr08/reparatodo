import { useEffect, useState } from "react";
import {
    obtenerEmpleados,
    crearEmpleado,
    actualizarEmpleado,
    eliminarEmpleado,
} from "../services/EmpleadoService";
import {
    validarCampo,
    validarFormulario,
    formatearRut,
    fechaMinimaNacimiento,
    fechaMaximaNacimiento,
} from "../utils/validarEmpleado";
import "./Empleados.css";

const FORMULARIO_VACIO = {
    nombre: "",
    rut: "",
    fechaNacimiento: "",
    direccion: "",
    telefono: "",
    correo: "",
    cargo: "",
    jornada: "",
    sueldoBruto: "",
    descuentos: "",
};

function formatearMoneda(valor) {
    if (valor === null || valor === undefined || valor === "") {
        return "—";
    }
    return Number(valor).toLocaleString("es-CL", {
        style: "currency",
        currency: "CLP",
        maximumFractionDigits: 0,
    });
}

function Campo({ label, error, children }) {
    return (
        <label className={error ? "campo-invalido" : undefined}>
            {label}
            {children}
            {error ? <span className="campo-error">{error}</span> : null}
        </label>
    );
}

function Empleados() {
    const [empleados, setEmpleados] = useState([]);
    const [formulario, setFormulario] = useState(FORMULARIO_VACIO);
    const [errores, setErrores] = useState({});
    const [idEditando, setIdEditando] = useState(null);
    const [error, setError] = useState("");
    const [guardando, setGuardando] = useState(false);
    const [cargaFallida, setCargaFallida] = useState(false);
    const [empleadoAEliminar, setEmpleadoAEliminar] = useState(null);
    const [rutConfirmado, setRutConfirmado] = useState("");
    const [errorEliminar, setErrorEliminar] = useState("");
    const [eliminando, setEliminando] = useState(false);

    useEffect(() => {
        cargarEmpleados();
    }, []);

    async function cargarEmpleados() {
        try {
            const datos = await obtenerEmpleados();
            setEmpleados(datos);
            setCargaFallida(false);
        } catch (err) {
            console.error(err);
            setCargaFallida(true);
            setError("No se pudo cargar la lista de empleados. Revisa que el backend esté en marcha.");
        }
    }

    function handleChange(event) {
        const { name, value } = event.target;
        setFormulario((previo) => {
            const actualizado = { ...previo, [name]: value };
            setErrores((previos) => ({
                ...previos,
                [name]: validarCampo(name, value, actualizado),
                ...(name === "sueldoBruto"
                    ? { descuentos: validarCampo("descuentos", actualizado.descuentos, actualizado) }
                    : {}),
            }));
            return actualizado;
        });
    }

    function handleBlur(event) {
        const { name, value } = event.target;
        if (name === "rut" && value.trim()) {
            const formateado = formatearRut(value);
            setFormulario((previo) => ({ ...previo, rut: formateado }));
            setErrores((previos) => ({
                ...previos,
                rut: validarCampo("rut", formateado, { ...formulario, rut: formateado }),
            }));
        }
    }

    function armarPayload() {
        return {
            nombre: formulario.nombre.trim(),
            rut: formulario.rut.trim(),
            fechaNacimiento: formulario.fechaNacimiento || null,
            direccion: formulario.direccion.trim() || null,
            telefono: formulario.telefono.trim() || null,
            correo: formulario.correo.trim() || null,
            cargo: formulario.cargo.trim() || null,
            jornada: formulario.jornada || null,
            sueldoBruto: formulario.sueldoBruto === "" ? null : Number(formulario.sueldoBruto),
            descuentos: formulario.descuentos === "" ? 0 : Number(formulario.descuentos),
        };
    }

    async function handleSubmit(event) {
        event.preventDefault();
        setError("");

        const erroresFormulario = validarFormulario(formulario);
        setErrores(erroresFormulario);
        if (Object.keys(erroresFormulario).length > 0) {
            setError("Revisa los campos marcados antes de guardar.");
            return;
        }

        setGuardando(true);
        try {
            const payload = armarPayload();
            if (idEditando) {
                await actualizarEmpleado(idEditando, payload);
            } else {
                await crearEmpleado(payload);
            }
            setFormulario(FORMULARIO_VACIO);
            setErrores({});
            setIdEditando(null);
            await cargarEmpleados();
        } catch (err) {
            console.error(err);
            setError(err.message || "No se pudo guardar el empleado.");
        } finally {
            setGuardando(false);
        }
    }

    function comenzarEdicion(empleado) {
        setError("");
        setErrores({});
        setIdEditando(empleado.id);
        setFormulario({
            nombre: empleado.nombre || "",
            rut: empleado.rut || "",
            fechaNacimiento: empleado.fechaNacimiento || "",
            direccion: empleado.direccion || "",
            telefono: empleado.telefono || "",
            correo: empleado.correo || "",
            cargo: empleado.cargo || "",
            jornada: empleado.jornada || "",
            sueldoBruto: empleado.sueldoBruto ?? "",
            descuentos: empleado.descuentos ?? "",
        });
        window.scrollTo({ top: 0, behavior: "smooth" });
    }

    function cancelarEdicion() {
        setIdEditando(null);
        setFormulario(FORMULARIO_VACIO);
        setErrores({});
        setError("");
    }

    function abrirEliminar(empleado) {
        setEmpleadoAEliminar(empleado);
        setRutConfirmado("");
        setErrorEliminar("");
    }

    function cerrarEliminar() {
        if (eliminando) {
            return;
        }
        setEmpleadoAEliminar(null);
        setRutConfirmado("");
        setErrorEliminar("");
    }

    async function confirmarEliminar(event) {
        event.preventDefault();
        if (!empleadoAEliminar) {
            return;
        }

        setErrorEliminar("");
        if (!rutConfirmado.trim()) {
            setErrorEliminar("Escribe el RUT para confirmar la eliminación.");
            return;
        }

        setEliminando(true);
        try {
            await eliminarEmpleado(empleadoAEliminar.id, rutConfirmado.trim());
            if (idEditando === empleadoAEliminar.id) {
                cancelarEdicion();
            }
            setEmpleadoAEliminar(null);
            setRutConfirmado("");
            await cargarEmpleados();
        } catch (err) {
            console.error(err);
            setErrorEliminar(err.message || "No se pudo eliminar el empleado.");
        } finally {
            setEliminando(false);
        }
    }

    return (
        <div className="empleados-page">
            <header className="empleados-header">
                <p className="empleados-marca">Reparatodo</p>
                <h1>Gestión de empleados</h1>
                <p className="empleados-subtitulo">
                    Registra datos personales y laborales, y mantén actualizada la ficha de cada persona.
                </p>
            </header>

            <section className="empleados-card">
                <h2>{idEditando ? "Editar empleado" : "Registrar empleado"}</h2>

                {error ? <p className="empleados-error" role="alert">{error}</p> : null}

                <form onSubmit={handleSubmit} className="empleados-form" noValidate>
                    <fieldset>
                        <legend>Datos personales</legend>
                        <Campo label="Nombre y apellido *" error={errores.nombre}>
                            <input
                                name="nombre"
                                type="text"
                                value={formulario.nombre}
                                onChange={handleChange}
                                autoComplete="name"
                            />
                        </Campo>
                        <Campo label="RUT *" error={errores.rut}>
                            <input
                                name="rut"
                                type="text"
                                placeholder="12.345.678-5"
                                value={formulario.rut}
                                onChange={handleChange}
                                onBlur={handleBlur}
                            />
                        </Campo>
                        <Campo label="Fecha de nacimiento *" error={errores.fechaNacimiento}>
                            <input
                                name="fechaNacimiento"
                                type="date"
                                min={fechaMinimaNacimiento()}
                                max={fechaMaximaNacimiento()}
                                value={formulario.fechaNacimiento}
                                onChange={handleChange}
                            />
                        </Campo>
                        <Campo label="Dirección *" error={errores.direccion}>
                            <input
                                name="direccion"
                                type="text"
                                placeholder="Calle, número, comuna"
                                value={formulario.direccion}
                                onChange={handleChange}
                            />
                        </Campo>
                        <Campo label="Teléfono *" error={errores.telefono}>
                            <input
                                name="telefono"
                                type="tel"
                                placeholder="+56 9 1234 5678"
                                value={formulario.telefono}
                                onChange={handleChange}
                            />
                        </Campo>
                        <Campo label="Correo *" error={errores.correo}>
                            <input
                                name="correo"
                                type="email"
                                placeholder="nombre@correo.cl"
                                value={formulario.correo}
                                onChange={handleChange}
                            />
                        </Campo>
                    </fieldset>

                    <fieldset>
                        <legend>Datos laborales</legend>
                        <Campo label="Cargo / profesión *" error={errores.cargo}>
                            <input
                                name="cargo"
                                type="text"
                                value={formulario.cargo}
                                onChange={handleChange}
                            />
                        </Campo>
                        <Campo label="Jornada *" error={errores.jornada}>
                            <select name="jornada" value={formulario.jornada} onChange={handleChange}>
                                <option value="">Seleccionar</option>
                                <option value="Completa">Completa</option>
                                <option value="Parcial">Parcial</option>
                                <option value="Turnos">Turnos</option>
                            </select>
                        </Campo>
                        <Campo label="Sueldo bruto *" error={errores.sueldoBruto}>
                            <input
                                name="sueldoBruto"
                                type="number"
                                min="400000"
                                max="50000000"
                                step="1"
                                value={formulario.sueldoBruto}
                                onChange={handleChange}
                            />
                        </Campo>
                        <Campo label="Descuentos" error={errores.descuentos}>
                            <input
                                name="descuentos"
                                type="number"
                                min="0"
                                step="1"
                                value={formulario.descuentos}
                                onChange={handleChange}
                            />
                        </Campo>
                    </fieldset>

                    <div className="empleados-acciones">
                        <button type="submit" disabled={guardando}>
                            {guardando
                                ? "Guardando..."
                                : idEditando
                                  ? "Guardar cambios"
                                  : "Registrar empleado"}
                        </button>
                        {idEditando ? (
                            <button type="button" className="secundario" onClick={cancelarEdicion}>
                                Cancelar edición
                            </button>
                        ) : null}
                    </div>
                </form>
            </section>

            <section className="empleados-card">
                <h2>Empleados registrados</h2>

                {cargaFallida ? (
                    <p className="empleados-vacio">No se pudo conectar con el servidor para listar empleados.</p>
                ) : empleados.length === 0 ? (
                    <p className="empleados-vacio">No hay empleados registrados.</p>
                ) : (
                    <div className="empleados-tabla-wrap">
                        <table className="empleados-tabla">
                            <thead>
                                <tr>
                                    <th>Nombre</th>
                                    <th>RUT</th>
                                    <th>Cargo</th>
                                    <th>Jornada</th>
                                    <th>Sueldo bruto</th>
                                    <th>Descuentos</th>
                                    <th>Contacto</th>
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody>
                                {empleados.map((empleado) => (
                                    <tr key={empleado.id}>
                                        <td>
                                            <strong>{empleado.nombre}</strong>
                                            {empleado.fechaNacimiento ? (
                                                <div className="detalle">{empleado.fechaNacimiento}</div>
                                            ) : null}
                                            {empleado.direccion ? (
                                                <div className="detalle">{empleado.direccion}</div>
                                            ) : null}
                                        </td>
                                        <td>{empleado.rut}</td>
                                        <td>{empleado.cargo || "—"}</td>
                                        <td>{empleado.jornada || "—"}</td>
                                        <td>{formatearMoneda(empleado.sueldoBruto)}</td>
                                        <td>{formatearMoneda(empleado.descuentos)}</td>
                                        <td>
                                            {empleado.telefono || "—"}
                                            {empleado.correo ? (
                                                <div className="detalle">{empleado.correo}</div>
                                            ) : null}
                                        </td>
                                        <td className="empleados-fila-acciones">
                                            <button type="button" className="secundario" onClick={() => comenzarEdicion(empleado)}>
                                                Editar
                                            </button>
                                            <button type="button" className="peligro" onClick={() => abrirEliminar(empleado)}>
                                                Eliminar
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}
            </section>

            {empleadoAEliminar ? (
                <div className="empleados-modal-fondo" onClick={cerrarEliminar} role="presentation">
                    <div
                        className="empleados-modal"
                        role="dialog"
                        aria-modal="true"
                        aria-labelledby="titulo-eliminar"
                        onClick={(event) => event.stopPropagation()}
                    >
                        <h2 id="titulo-eliminar">Eliminar empleado</h2>
                        <p>
                            Esta acción no se puede deshacer. Para confirmar, escribe el RUT de{" "}
                            <strong>{empleadoAEliminar.nombre}</strong>:
                        </p>
                        <p className="empleados-modal-rut">{empleadoAEliminar.rut}</p>
                        <form onSubmit={confirmarEliminar}>
                            <label>
                                Confirmar RUT
                                <input
                                    type="text"
                                    value={rutConfirmado}
                                    onChange={(event) => setRutConfirmado(event.target.value)}
                                    placeholder={empleadoAEliminar.rut}
                                    autoFocus
                                />
                            </label>
                            {errorEliminar ? <p className="empleados-error" role="alert">{errorEliminar}</p> : null}
                            <div className="empleados-acciones">
                                <button type="submit" className="peligro" disabled={eliminando}>
                                    {eliminando ? "Eliminando..." : "Eliminar definitivamente"}
                                </button>
                                <button type="button" className="secundario" onClick={cerrarEliminar} disabled={eliminando}>
                                    Cancelar
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            ) : null}
        </div>
    );
}

export default Empleados;
