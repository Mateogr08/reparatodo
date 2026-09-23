const SUELDO_MINIMO = 400000;
const SUELDO_MAXIMO = 50000000;
const EDAD_MINIMA = 18;
const EDAD_MAXIMA = 75;
const JORNADAS = ["Completa", "Parcial", "Turnos"];

export function fechaMaximaNacimiento() {
    const fecha = new Date();
    fecha.setFullYear(fecha.getFullYear() - EDAD_MINIMA);
    return fecha.toISOString().slice(0, 10);
}

export function fechaMinimaNacimiento() {
    const fecha = new Date();
    fecha.setFullYear(fecha.getFullYear() - EDAD_MAXIMA);
    return fecha.toISOString().slice(0, 10);
}

export function normalizarRut(rut) {
    return (rut || "").replace(/[.\-\s]/g, "").toUpperCase();
}

export function formatearRut(rut) {
    const limpio = normalizarRut(rut);
    if (limpio.length < 2) {
        return rut || "";
    }
    const cuerpo = limpio.slice(0, -1);
    const dv = limpio.slice(-1);
    let conPuntos = "";
    let contador = 0;
    for (let i = cuerpo.length - 1; i >= 0; i -= 1) {
        if (contador === 3) {
            conPuntos = `.${conPuntos}`;
            contador = 0;
        }
        conPuntos = cuerpo[i] + conPuntos;
        contador += 1;
    }
    return `${conPuntos}-${dv}`;
}

export function esRutValido(rut) {
    const limpio = normalizarRut(rut);
    if (!/^\d{7,8}[0-9K]$/.test(limpio)) {
        return false;
    }
    const cuerpo = limpio.slice(0, -1);
    const dv = limpio.slice(-1);
    let suma = 0;
    let multiplicador = 2;
    for (let i = cuerpo.length - 1; i >= 0; i -= 1) {
        suma += Number(cuerpo[i]) * multiplicador;
        multiplicador = multiplicador === 7 ? 2 : multiplicador + 1;
    }
    const resto = 11 - (suma % 11);
    const dvCalculado = resto === 11 ? "0" : resto === 10 ? "K" : String(resto);
    return dv === dvCalculado;
}

function edadEnAnios(fechaIso) {
    const nacimiento = new Date(`${fechaIso}T00:00:00`);
    const hoy = new Date();
    let edad = hoy.getFullYear() - nacimiento.getFullYear();
    const mes = hoy.getMonth() - nacimiento.getMonth();
    if (mes < 0 || (mes === 0 && hoy.getDate() < nacimiento.getDate())) {
        edad -= 1;
    }
    return edad;
}

export function validarCampo(nombre, valor, formulario = {}) {
    const texto = typeof valor === "string" ? valor.trim() : valor;

    switch (nombre) {
        case "nombre":
            if (!texto) return "El nombre completo es obligatorio";
            if (!/^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ' ]{5,80}$/.test(texto)) {
                return "Solo letras, espacios y tildes (5 a 80 caracteres)";
            }
            if (texto.split(/\s+/).length < 2) return "Ingresa nombre y apellido";
            return "";
        case "rut":
            if (!texto) return "El RUT es obligatorio";
            if (!esRutValido(texto)) return "RUT inválido: revisa el número y el dígito verificador";
            return "";
        case "fechaNacimiento":
            if (!texto) return "La fecha de nacimiento es obligatoria";
            {
                const edad = edadEnAnios(texto);
                if (edad < EDAD_MINIMA) return `Debe ser mayor de ${EDAD_MINIMA} años`;
                if (edad > EDAD_MAXIMA) return `La edad no puede superar los ${EDAD_MAXIMA} años`;
            }
            return "";
        case "direccion":
            if (!texto) return "La dirección es obligatoria";
            if (texto.length < 8) return "La dirección es demasiado corta";
            return "";
        case "telefono":
            if (!texto) return "El teléfono es obligatorio";
            if (!/^(\+?56)?\s?0?9\s?\d{4}\s?\d{4}$/.test(texto)) {
                return "Usa un celular chileno, por ejemplo +56 9 1234 5678";
            }
            return "";
        case "correo":
            if (!texto) return "El correo es obligatorio";
            if (!/^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/.test(texto)) {
                return "El correo no tiene un formato válido";
            }
            return "";
        case "cargo":
            if (!texto) return "El cargo o profesión es obligatorio";
            if (texto.length < 3) return "El cargo es demasiado corto";
            return "";
        case "jornada":
            if (!texto) return "La jornada es obligatoria";
            if (!JORNADAS.includes(texto)) return "Selecciona Completa, Parcial o Turnos";
            return "";
        case "sueldoBruto":
            if (texto === "" || texto === null || texto === undefined) return "El sueldo bruto es obligatorio";
            {
                const sueldo = Number(texto);
                if (!Number.isInteger(sueldo)) return "El sueldo debe ser un monto entero";
                if (sueldo < SUELDO_MINIMO) return "El sueldo bruto mínimo es $400.000";
                if (sueldo > SUELDO_MAXIMO) return "El sueldo bruto supera el máximo permitido";
            }
            return "";
        case "descuentos": {
            const descuentos = texto === "" || texto === null || texto === undefined ? 0 : Number(texto);
            if (!Number.isInteger(descuentos)) return "Los descuentos deben ser un monto entero";
            if (descuentos < 0) return "Los descuentos no pueden ser negativos";
            const sueldo = Number(formulario.sueldoBruto);
            if (Number.isFinite(sueldo) && descuentos > sueldo) {
                return "Los descuentos no pueden superar el sueldo bruto";
            }
            if (Number.isFinite(sueldo) && descuentos > sueldo * 0.4) {
                return "Los descuentos no pueden superar el 40% del sueldo bruto";
            }
            return "";
        }
        default:
            return "";
    }
}

export function validarFormulario(formulario) {
    const errores = {};
    Object.keys(formulario).forEach((campo) => {
        const mensaje = validarCampo(campo, formulario[campo], formulario);
        if (mensaje) {
            errores[campo] = mensaje;
        }
    });
    return errores;
}
