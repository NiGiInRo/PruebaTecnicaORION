/**
 * Capa de aplicación: casos de uso (orquestación de reglas de dominio) y
 * puertos de persistencia (interfaces de repositorio) que la infraestructura
 * implementa. Por pragmatismo de tiempo (ver ADR-8 en SCENARIO.md), los
 * puertos extienden Spring Data JpaRepository en vez de una interfaz 100%
 * agnóstica — siguen siendo mockeables en tests de casos de uso.
 */
package com.orion.maintenance.application;
