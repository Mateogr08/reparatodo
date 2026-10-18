import { useMemo, useState } from "react";
import Empleados from "/src/assets/pages/Empleados.jsx";
import Login from "/src/assets/pages/Login.jsx";
import Asistencia from "/src/assets/pages/Asistencia.jsx";
import Liquidaciones from "/src/assets/pages/Liquidaciones.jsx";
import Reportes from "/src/assets/pages/Reportes.jsx";
import Backups from "/src/assets/pages/Backups.jsx";
import Usuarios from "/src/assets/pages/Usuarios.jsx";
import { cerrarSesion } from "/src/assets/services/AuthService.js";
import "./App.css";

function leerSesion() {
    try {
        return JSON.parse(localStorage.getItem("usuario") || "null");
    } catch {
        return null;
    }
}

const MODULOS = [
    { id: "empleados", etiqueta: "Empleados", roles: ["ADMINISTRADOR", "ENCARGADO_PERSONAL", "EMPLEADO", "USUARIO"] },
    { id: "asistencia", etiqueta: "Asistencia", roles: ["ADMINISTRADOR", "ENCARGADO_PERSONAL"] },
    { id: "liquidaciones", etiqueta: "Liquidaciones", roles: ["ADMINISTRADOR", "ENCARGADO_PERSONAL", "EMPLEADO"] },
    { id: "reportes", etiqueta: "Reportes", roles: ["ADMINISTRADOR", "ENCARGADO_PERSONAL"] },
    { id: "backups", etiqueta: "Copias de seguridad", roles: ["ADMINISTRADOR"] },
    { id: "usuarios", etiqueta: "Usuarios y roles", roles: ["ADMINISTRADOR"] },
];

function App() {
    const [sesion, setSesion] = useState(leerSesion);
    const [vista, setVista] = useState("empleados");

    const modulosVisibles = useMemo(
        () => MODULOS.filter((modulo) => sesion && modulo.roles.includes(sesion.rol)),
        [sesion]
    );

    async function salir() {
        await cerrarSesion();
        localStorage.removeItem("token");
        localStorage.removeItem("usuario");
        setSesion(null);
        setVista("empleados");
    }

    if (!sesion || !localStorage.getItem("token")) {
        return <Login onSesion={setSesion} />;
    }

    const puedeEditarEmpleados = ["ADMINISTRADOR", "ENCARGADO_PERSONAL"].includes(sesion.rol);

    return (
        <div>
            <nav className="app-nav">
                <strong>Reparatodo</strong>
                {modulosVisibles.map((modulo) => (
                    <button
                        key={modulo.id}
                        type="button"
                        className={vista === modulo.id ? "activo" : ""}
                        onClick={() => setVista(modulo.id)}
                    >
                        {modulo.id === "empleados" && sesion.rol === "EMPLEADO"
                            ? "Mi ficha"
                            : modulo.etiqueta}
                    </button>
                ))}
                <span className="app-nav-usuario">{sesion.nombre} · {sesion.rol}</span>
                <button type="button" className="salir" onClick={salir}>Salir</button>
            </nav>
            {vista === "empleados" ? <Empleados puedeEditar={puedeEditarEmpleados} /> : null}
            {vista === "asistencia" ? <Asistencia /> : null}
            {vista === "liquidaciones" ? <Liquidaciones puedePagar={puedeEditarEmpleados} /> : null}
            {vista === "reportes" ? <Reportes /> : null}
            {vista === "backups" ? <Backups /> : null}
            {vista === "usuarios" ? <Usuarios /> : null}
        </div>
    );
}

export default App;
