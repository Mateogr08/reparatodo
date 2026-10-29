# Diagnóstico técnico y refactorización — Reparatodo

## 1. Contexto

| Campo | Valor |
|--------|--------|
| **Equipo** | Mateo Garrido Rios, Karen Alexandra Lara y Luz Nelly Delgado |
| **Repositorio** | https://github.com/Mateogr08/reparatodo.git |
| **Funcionalidad auditada** | **Calcular liquidación de nómina** (y consulta / PDF asociado) |
| **Requisitos relacionados** | **F-03** (cálculo según asistencia, AFP, EPS, impuestos, bonificaciones, líquido), **F-04** (PDF de liquidación) |
| **Stack** | Spring Boot 4.1 (Java 21), JPA/MySQL, React + Vite |

**Alcance del análisis:** desde `POST /api/liquidaciones` (calcular) hasta persistencia en `liquidaciones` y generación del PDF; incluye listado y descarga `GET /api/liquidaciones/{id}/pdf`. No incluye emisión de pagos ni reportes administrativos.

---

## 2. Arquitectura real

### Componentes

| Capa | Responsabilidad | Archivos |
|------|-----------------|----------|
| Presentación (SPA) | Formulario calcular, tabla, descarga PDF | `frontend/src/assets/pages/Liquidaciones.jsx`, `LiquidacionService.js`, `http.js` |
| Controlador REST | HTTP, parseo mínimo del body | `LiquidacionController.java` |
| Servicio de aplicación | Orquestación: permisos, asistencia, persistencia, PDF | `LiquidacionService.java` |
| Servicio de infraestructura | Generación PDF | `PdfLiquidacionService.java` |
| Dominio (tras refactor) | Cálculo puro de montos | `domain/CalculadoraLiquidacion.java` |
| Persistencia | JPA | `LiquidacionRepository`, `EmpleadoRepository`, `AsistenciaRepository` |
| Modelo | Entidades | `Liquidacion`, `Empleado`, `Asistencia` |
| Seguridad | Roles y acceso por empleado | `SesionActual`, `VinculoEmpleado`, `AuthFilter` |

### Flujo (calcular)

1. Usuario en **Liquidaciones** envía empleado, periodo y bonificaciones.
2. `LiquidacionController.calcular` recibe `Map<String, Object>` y extrae campos.
3. `LiquidacionService.calcular` valida rol, carga empleado, cuenta días presentes en asistencia, **calcula montos**, guarda `Liquidacion`, genera PDF y vuelve a guardar.
4. La UI refresca la lista vía `GET /api/liquidaciones`.

---

## 3. Hallazgos

### H-01 — Parseo del cuerpo HTTP en el controlador

| Campo | Detalle |
|--------|---------|
| **Tipo** | Deuda técnica / riesgo de robustez en capa web |
| **Ubicación** | `backend/src/main/java/com/proyecto/backend/controller/LiquidacionController.java`, método `calcular`, aprox. líneas 30-37 |
| **Evidencia** | Uso de `Map<String, Object>` y `Long.valueOf(cuerpo.get("empleadoId").toString())` sin DTO ni validación explícita de campos nulos. |
| **Observación** | El controlador concentra conversión de tipos y reglas de formato del JSON. Si falta `empleadoId` o el valor no es numérico, la API puede responder 500 en lugar de 400 controlado. |
| **Criterio afectado** | Separación de capas; responsabilidad del controlador (adaptar HTTP, no validar dominio). |
| **Impacto** | Errores poco claros para el cliente; más difícil de probar y mantener el contrato de la API. |
| **Acción propuesta** | Introducir un DTO `CalcularLiquidacionRequest` con validación (`@NotNull`, `@Pattern` en periodo) y delegar parseo a Spring. |
| **Verificación** | Enviar body incompleto y comprobar 400 con mensaje; caso feliz igual que antes. |
| **Estado** | Pendiente (no implementado en esta entrega) |

### H-02 — Mezcla de orquestación y reglas de cálculo en el servicio

| Campo | Detalle |
|--------|---------|
| **Tipo** | Oportunidad de mejora / deuda técnica (cohesión y SRP) |
| **Ubicación** | `LiquidacionService.java`, método `calcular` (antes del refactor), bloque de cálculo aprox. líneas 63-82 en la versión original |
| **Evidencia** | En un mismo método: autorización, consulta asistencia, fórmulas AFP/ISAPRE/impuestos, persistencia y llamada a PDF. |
| **Observación** | La lógica de negocio numérica no era reutilizable ni unit-testeable sin levantar Spring y base de datos. |
| **Criterio afectado** | **SRP** (el servicio tenía varias razones de cambio: tasas legales, persistencia, seguridad). |
| **Impacto** | Cambios en fórmulas obligan a tocar el servicio completo; riesgo de regresiones al evolucionar pagos o PDF. |
| **Acción propuesta** | Extraer cálculo a `CalculadoraLiquidacion` en paquete `domain`. |
| **Verificación** | Test unitario `CalculadoraLiquidacionTest` + misma liquidación manual en UI antes/después. |
| **Estado** | **Implementado y verificado** (ver sección 5) |

### H-03 — Validación de empleado duplicada cliente/servidor

| Campo | Detalle |
|--------|---------|
| **Tipo** | Deuda técnica |
| **Ubicación** | `backend/.../validation/ValidadorEmpleado.java` y `frontend/src/assets/utils/validarEmpleado.js` |
| **Evidencia** | Mismas reglas (RUT DIAN, SMMLV, teléfono, jornadas) implementadas dos veces en lenguajes distintos. |
| **Observación** | No es un defecto funcional si ambas están sincronizadas, pero cualquier cambio legal requiere dos ediciones. |
| **Criterio afectado** | Mantenibilidad, DRY. |
| **Impacto** | Divergencia silenciosa entre lo que el front acepta y lo que el back rechaza. |
| **Acción propuesta** | Documentar reglas en un solo lugar; a medio plazo validar solo en servidor o compartir esquema (OpenAPI + validación). |
| **Verificación** | Matriz de casos borde (RUT inválido, sueldo bajo SMMLV) en front y API. |
| **Estado** | Pendiente |

---

## 4. Priorización

| ID | Impacto (1-3) | Riesgo (1-3) | Esfuerzo (1-3) | Selección |
|----|---------------|--------------|----------------|-----------|
| H-01 | 2 | 2 | 2 | No (siguiente iteración) |
| **H-02** | **3** | **2** | **2** | **Sí — refactor realizado** |
| H-03 | 2 | 2 | 3 | No (alcance mayor) |

**Razón:** H-02 mejora testabilidad y alinea el código con capas (dominio vs aplicación) con esfuerzo acotado y verificación automática clara.

---

## 5. Intervención realizada (H-02)

### Antes

- `LiquidacionService.calcular` contenía constantes de tasas y fórmulas inline.

### Después

- Nueva clase `com.proyecto.backend.domain.CalculadoraLiquidacion` con `Entrada` / `Resultado` y método estático `calcular`.
- `LiquidacionService` orquesta: permisos, asistencia, llama a la calculadora, persiste y genera PDF.

### Archivos tocados

- `backend/src/main/java/com/proyecto/backend/domain/CalculadoraLiquidacion.java` (nuevo)
- `backend/src/main/java/com/proyecto/backend/service/LiquidacionService.java` (modificado)
- `backend/src/test/java/com/proyecto/backend/domain/CalculadoraLiquidacionTest.java` (nuevo)
- `backend/src/test/resources/application-test.properties` (perfil H2 para `mvn test` sin MySQL)
- `backend/src/test/java/com/proyecto/backend/BackendApplicationTests.java` (`@ActiveProfiles("test")`)
- `backend/pom.xml` (dependencia H2 en scope test)

---

## 6. Verificación

| Prueba | Resultado |
|--------|-----------|
| `mvn test` | **OK** — BUILD SUCCESS; 2 tests: `CalculadoraLiquidacionTest`, `BackendApplicationTests.contextLoads` (perfil `test` con H2 en memoria). |
| Compilación `mvn compile` | **OK** (ejecutada junto al ciclo de test). |
| Prueba manual: calcular liquidación en UI | **Recomendada para sustentación:** login admin → Asistencia del periodo → Liquidaciones → Calcular → comprobar montos y estado CALCULADA. |
| Descarga PDF | **Recomendada:** botón PDF en la fila de liquidación; archivo `liquidacion-{id}.pdf` descargado. |

**Comportamiento conservado:** las fórmulas (proporción por días, AFP 4 %, ISAPRE 4 %, impuestos sobre excedente, descuentos proporcionales) se movieron sin alterar la lógica; el servicio sigue persistiendo y generando PDF igual que antes.

**Limitaciones:** no hay prueba de integración automatizada de `LiquidacionService.calcular` contra MySQL; la regresión del cálculo queda cubierta por `CalculadoraLiquidacionTest`.

**Evidencias:** salida de consola de `mvn test` (29-oct-2026); capturas opcionales en `docs/refactorizacion/evidencias/`.

---

## 7. Cierre

**Lección:** extraer reglas de cálculo temprano hubiera permitido pruebas unitarias sin base de datos.

**Deuda pendiente:** H-01 (DTO en controlador), H-03 (duplicación validaciones), tests de integración para liquidación.

**Pregunta de cierre (taller):** ¿Qué práctica lo habría detectado antes? — **Revisión de código con checklist de capas**, **tests unitarios de dominio** al implementar F-03, y **definition of done** que exija no mezclar cálculo con persistencia en el mismo método.

---

## 8. Git

| Commit | Mensaje sugerido | Hash |
|--------|-----------------|------|
| 1 | `docs: registrar diagnostico tecnico` | [pegar hash] |
| 2 | `refactor: extraer calculo de liquidacion a dominio` | [pegar hash] |
| 3 | `docs: documentar verificacion de refactorizacion` | [pegar hash] |

