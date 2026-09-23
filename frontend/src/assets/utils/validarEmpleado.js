const SUELDO_MINIMO = 1750905;
const SUELDO_MAXIMO = 50000000;
const EDAD_MINIMA = 18;
const EDAD_MAXIMA = 75;
const JORNADAS = ["Completa", "Parcial", "Turnos"];
const PESOS_DIAN = [3, 7, 13, 17, 19, 23, 29, 37, 41, 43, 47, 53, 59, 67, 71];

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
    return (rut || "").replace(/\D/g, "");
}

function calcularDvDian(numero) {
    let suma = 0;
    for (let i = 0; i < numero.length; i += 1) {
        suma += Number(numero[numero.length - 1 - i]) * PESOS_DIAN[i];
    }
    const residuo = suma % 11;
    return residuo > 1 ? 11 - residuo : residuo;
}

export function formatearRut(rut) {
    const limpio = normalizarRut(rut);
    if (limpio.length < 2) {
        return rut || "";
    }
    const numero = limpio.slice(0, -1);
    const dv = limpio.slice(-1);
    let conPuntos = "";
    let contador = 0;
    for (let i = numero.length - 1; i >= 0; i -= 1) {
        if (contador === 3) {
            conPuntos = `.${conPuntos}`;
            contador = 0;
        }
        conPuntos = numero[i] + conPuntos;
        contador += 1;
    }
    return `${conPuntos}-${dv}`;
}

export function esRutValido(rut) {
    const limpio = normalizarRut(rut);
    if (limpio.length < 7 || limpio.length > 11) {
        return false;
    }
    const numero = limpio.slice(0, -1);
    const dv = Number(limpio.slice(-1));
    if (!/^\d{6,10}$/.test(numero) || Number.isNaN(dv)) {
        return false;
    }
    return dv === calcularDvDian(numero);
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
            if (!esRutValido(texto)) {
                return "RUT colombiano inválido: revisa el NIT y el dígito de verificación";
            }
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
            if (!/^(\+?57)?\s?3\d{2}\s?\d{3}\s?\d{4}$/.test(texto)) {
                return "Usa un celular colombiano, por ejemplo 300 123 4567";
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
                if (sueldo < SUELDO_MINIMO) return "El sueldo bruto mínimo es $1.750.905 (SMMLV 2026)";
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
