import { useEffect, useState } from "react";
import { actualizarUsuario, crearUsuario, eliminarUsuario, obtenerUsuarios } from "../services/UsuarioService";
import "./Empleados.css";

const VACIO = { nombre: "", email: "", password: "", rol: "USUARIO" };

function Usuarios() {
    const [usuarios, setUsuarios] = useState([]);
    const [formulario, setFormulario] = useState(VACIO);
    const [idEditando, setIdEditando] = useState(null);
    const [error, setError] = useState("");

    useEffect(() => {
        cargar();
    }, []);

    async function cargar() {
        try {
            setUsuarios(await obtenerUsuarios());
        } catch (err) {
            setError(err.message);
        }
    }

    async function handleSubmit(event) {
        event.preventDefault();
        setError("");
        try {
            if (idEditando) {
                await actualizarUsuario(idEditando, formulario);
            } else {
                await crearUsuario(formulario);
            }
            setFormulario(VACIO);
            setIdEditando(null);
            await cargar();
        } catch (err) {
            setError(err.message);
        }
    }

    return (
        <div className="empleados-page">
            <header className="empleados-header">
                <h1>Usuarios y roles</h1>
                <p className="empleados-subtitulo">Crea cuentas y asigna rol: Administrador, Encargado de personal, Empleado o Usuario.</p>
            </header>
            <section className="empleados-card">
                {error ? <p className="empleados-error">{error}</p> : null}
                <form className="empleados-form" onSubmit={handleSubmit}>
                    <fieldset>
                        <legend>{idEditando ? "Editar usuario" : "Nuevo usuario"}</legend>
                        <label>
                            Nombre
                            <input value={formulario.nombre} onChange={(e) => setFormulario({ ...formulario, nombre: e.target.value })} required />
                        </label>
                        <label>
                            Correo
                            <input type="email" value={formulario.email} onChange={(e) => setFormulario({ ...formulario, email: e.target.value })} required />
                        </label>
                        <label>
                            Contraseña
                            <input type="password" value={formulario.password} onChange={(e) => setFormulario({ ...formulario, password: e.target.value })} placeholder={idEditando ? "Dejar vacío para no cambiar" : ""} />
                        </label>
                        <label>
                            Rol
                            <select value={formulario.rol} onChange={(e) => setFormulario({ ...formulario, rol: e.target.value })}>
                                <option value="ADMINISTRADOR">Administrador</option>
                                <option value="ENCARGADO_PERSONAL">Encargado de personal</option>
                                <option value="EMPLEADO">Empleado</option>
                                <option value="USUARIO">Usuario</option>
                            </select>
                        </label>
                    </fieldset>
                    <div className="empleados-acciones">
                        <button type="submit">{idEditando ? "Guardar cambios" : "Crear usuario"}</button>
                        {idEditando ? (
                            <button type="button" className="secundario" onClick={() => { setIdEditando(null); setFormulario(VACIO); }}>
                                Cancelar
                            </button>
                        ) : null}
                    </div>
                </form>
            </section>
            <section className="empleados-card">
                <h2>Cuentas</h2>
                <div className="empleados-tabla-wrap">
                    <table className="empleados-tabla">
                        <thead>
                            <tr>
                                <th>Nombre</th>
                                <th>Correo</th>
                                <th>Rol</th>
                                <th></th>
                            </tr>
                        </thead>
                        <tbody>
                            {usuarios.map((usuario) => (
                                <tr key={usuario.id}>
                                    <td>{usuario.nombre}</td>
                                    <td>{usuario.email}</td>
                                    <td>{usuario.rol}</td>
                                    <td className="empleados-fila-acciones">
                                        <button type="button" className="secundario" onClick={() => { setIdEditando(usuario.id); setFormulario({ ...VACIO, ...usuario, password: "" }); }}>
                                            Editar
                                        </button>
                                        <button type="button" className="peligro" onClick={() => eliminarUsuario(usuario.id).then(cargar).catch((err) => setError(err.message))}>
                                            Eliminar
                                        </button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </section>
        </div>
    );
}

export default Usuarios;
