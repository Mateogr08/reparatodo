# Entrega — Taller refactorización (IS II)

## Qué subir al profesor

1. **Repositorio:** https://github.com/Mateogr08/reparatodo.git  
2. **Informe obligatorio:** `docs/refactorizacion/DIAGNOSTICO_REFACTORIZACION.md`  
3. **Commits del taller:** tres commits (diagnóstico → refactor → verificación documentada). Ver sección 8 del informe con hashes.

## Checklist antes de entregar

- [ ] `docs/refactorizacion/DIAGNOSTICO_REFACTORIZACION.md` completo (secciones 1–8)
- [ ] Código: `CalculadoraLiquidacion`, `LiquidacionService` refactorizado, tests
- [ ] En local: `cd backend` → `.\mvnw.cmd test` → BUILD SUCCESS
- [ ] `git push origin main` (o la rama que use el curso)

## Comando push (tú)

```powershell
cd C:\Users\mateo\OneDrive\Escritorio\Proyectos\reparatodo
git push origin main
```

## Sustentación (30 segundos)

- Funcionalidad: **calcular liquidación** (F-03 / F-04).
- Hallazgo aplicado: **H-02** — cálculo mezclado en el servicio (SRP).
- Refactor: clase **`CalculadoraLiquidacion`** + test unitario.
- Verificación: **`mvn test`**; mismo flujo en pantalla Liquidaciones.

## Pregunta de cierre (informe sección 7)

Revisión de código con checklist de capas, tests unitarios de dominio al implementar F-03, y definition of done que separe cálculo de persistencia.
