import { useState } from "react";
import { iniciarSesion } from "../services/AuthService";
import "./Empleados.css";

function Login({ onSesion }) {
    const [email, setEmail] = useState("admin@reparatodo.com");
    const [password, setPassword] = useState("Admin123");
    const [error, setError] = useState("");
    const [cargando, setCargando] = useState(false);

    async function handleSubmit(event) {
        event.preventDefault();
        setError("");
        setCargando(true);
        try {
            const sesion = await iniciarSesion(email, password);
            localStorage.setItem("token", sesion.token);
            localStorage.setItem("usuario", JSON.stringify(sesion));
            onSesion(sesion);
        } catch (err) {
            setError(err.message || "No se pudo iniciar sesión");
        } finally {
            setCargando(false);
        }
    }

    return (
        <div className="empleados-page">
            <header className="empleados-header">
                <p className="empleados-marca">Reparatodo</p>
                <h1>Ingreso al sistema</h1>
                <p className="empleados-subtitulo">Usa tu usuario y rol asignado para acceder a los módulos.</p>
            </header>
            <section className="empleados-card">
                {error ? <p className="empleados-error" role="alert">{error}</p> : null}
                <form className="empleados-form" onSubmit={handleSubmit}>
                    <fieldset>
                        <legend>Credenciales</legend>
                        <label>
                            Correo
                            <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
                        </label>
                        <label>
                            Contraseña
                            <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
                        </label>
                    </fieldset>
                    <div className="empleados-acciones">
                        <button type="submit" disabled={cargando}>{cargando ? "Ingresando..." : "Ingresar"}</button>
                    </div>
                </form>
                <p className="empleados-vacio">Usuario inicial: admin@reparatodo.com / Admin123</p>
            </section>
        </div>
    );
}

export default Login;
