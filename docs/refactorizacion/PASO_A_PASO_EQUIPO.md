# Qué hacer tú (equipo) — orden simple

Sigue estos pasos en orden. El contenido técnico ya está en el repo; tu trabajo es **revisar**, **probar** y **commitear**.

## Parte A — Revisar (15 min)

1. Abre `docs/refactorizacion/DIAGNOSTICO_REFACTORIZACION.md`.
2. En la sección **1. Contexto**, escribe nombres del equipo y URL del repositorio.
3. Lee los **3 hallazgos** (H-01, H-02, H-03) y abre los archivos citados en el IDE para confirmar que la evidencia existe.
4. Si algo no coincide, corrige el `.md` (eso es parte de la actividad: validación manual).

## Parte B — Probar el código (10 min)

1. Terminal en `backend`:
   ```powershell
   .\mvnw.cmd test
   ```
2. Debe pasar `CalculadoraLiquidacionTest` y `BackendApplicationTests`.
3. Arranca backend + frontend y calcula una liquidación en la web (mismo caso de siempre).
4. En el `.md`, sección **6. Verificación**, marca OK y anota montos o adjunta captura en `evidencias/`.

## Parte C — Git (3 commits)

Desde la raíz del proyecto:

```powershell
git add docs/refactorizacion/DIAGNOSTICO_REFACTORIZACION.md docs/refactorizacion/PASO_A_PASO_EQUIPO.md
git commit -m "docs: registrar diagnostico tecnico"
```

```powershell
git add backend/src/main/java/com/proyecto/backend/domain/CalculadoraLiquidacion.java
git add backend/src/main/java/com/proyecto/backend/service/LiquidacionService.java
git add backend/src/test/java/com/proyecto/backend/domain/CalculadoraLiquidacionTest.java
git commit -m "refactor: extraer calculo de liquidacion a dominio"
```

Completa hashes en sección 8 del diagnóstico, luego:

```powershell
git add docs/refactorizacion/DIAGNOSTICO_REFACTORIZACION.md
git commit -m "docs: documentar verificacion de refactorizacion"
```

## Parte D — Entregar al profesor

- Enlace al repositorio.
- Ruta: `docs/refactorizacion/DIAGNOSTICO_REFACTORIZACION.md`.
- Los 3 commits (o hashes).
- En la sustentación: funcionalidad elegida, un hallazgo, qué refactor hicieron, cómo verificaron.

## Parte E — Sustentación (2 min por persona)

Guion:

1. "Auditamos **calcular liquidación** (F-03/F-04)."
2. "Encontramos que el **servicio mezclaba** cálculo y persistencia (H-02)."
3. "Separamos **`CalculadoraLiquidacion`** y agregamos **test unitario**."
4. "Comportamiento igual: `mvn test` + prueba manual en Liquidaciones."
