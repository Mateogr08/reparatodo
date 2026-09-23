import { useEffect, useState } from "react";
import {
    obtenerUsuarios,
    crearUsuario,
} from "../services/usuarioService";

function Usuarios() {
    const [usuarios, setUsuarios] = useState([]);

    const [nombre, setNombre] = useState("");
    const [email, setEmail] = useState("");

    useEffect(() => {
        cargarUsuarios();
    }, []);

    async function cargarUsuarios() {
        try {
            const datos = await obtenerUsuarios();
            setUsuarios(datos);
        } catch (error) {
            console.error(error);
        }
    }

    async function handleSubmit(event) {
        event.preventDefault();

        if (!nombre || !email) {
            alert("Completa todos los campos");
            return;
        }

        try {
            await crearUsuario({
                nombre: nombre,
                email: email,
            });

            setNombre("");
            setEmail("");

            await cargarUsuarios();
        } catch (error) {
            console.error(error);
            alert("No se pudo crear el usuario");
        }
    }

    return (
        <div>
            <h1>Usuarios</h1>

            <form onSubmit={handleSubmit}>
                <div>
                    <label>Nombre:</label>
                    <input
                        type="text"
                        value={nombre}
                        onChange={(event) => setNombre(event.target.value)}
                    />
                </div>

                <div>
                    <label>Email:</label>
                    <input
                        type="email"
                        value={email}
                        onChange={(event) => setEmail(event.target.value)}
                    />
                </div>

                <button type="submit">
                    Crear usuario
                </button>
            </form>

            <hr />

            <h2>Lista de usuarios</h2>

            {usuarios.length === 0 ? (
                <p>No hay usuarios registrados.</p>
            ) : (
                <ul>
                    {usuarios.map((usuario) => (
                        <li key={usuario.id}>
                            {usuario.nombre} - {usuario.email}
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
}

export default Usuarios;