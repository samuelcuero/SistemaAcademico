/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.controlador;

/**
 *
 * @author samue
 */

import com.mycompany.servicios.ServicioNotas;
import com.mycompany.modelo.Estudiante;
import com.mycompany.modelo.Curso;
import com.mycompany.modelo.Evaluacion;
import com.mycompany.modelo.Calificacion;
import com.mycompany.util.AuditLog;
import com.mycompany.util.ValidadorEntrada;
import com.mycompany.servicios.ServicioArchivo;

import java.util.HashMap;
import java.util.Map;

public class ControladorNotas {
    private final ServicioNotas servicioNotas;
    private final ServicioArchivo servicioArchivo;

    public ControladorNotas() {
        this.servicioNotas = new ServicioNotas();
        this.servicioArchivo = new ServicioArchivo();
    }

    public void asignarCalificacion(Estudiante estudiante, Curso curso, Evaluacion evaluacion, double nota, String observacion, String usuarioId) {
        if (!ValidadorEntrada.validarNota(nota)) {
            throw new IllegalArgumentException("La nota debe estar entre 0 y 100.");
        }
        servicioNotas.asignarCalificacion(estudiante, curso, evaluacion, nota, observacion);
        AuditLog log = new AuditLog(usuarioId, "ASIGNAR_CALIFICACION",
                "Estudiante: " + estudiante.getId() + ", Curso: " + curso.getCodigo() + ", Evaluación: " + evaluacion.getTipo());
        servicioArchivo.guardarAuditLog(log, "audit.log");
    }

    public void actualizarNota(Estudiante estudiante, Curso curso, Evaluacion evaluacion, double nuevaNota, String usuarioId) {
        if (!ValidadorEntrada.validarNota(nuevaNota)) {
            throw new IllegalArgumentException("La nota debe estar entre 0 y 100.");
        }
        servicioNotas.actualizarNota(estudiante, curso, evaluacion, nuevaNota);
        AuditLog log = new AuditLog(usuarioId, "ACTUALIZAR_NOTA",
                "Estudiante: " + estudiante.getId() + ", Curso: " + curso.getCodigo() + ", Evaluación: " + evaluacion.getTipo());
        servicioArchivo.guardarAuditLog(log, "audit.log");
    }

    public double obtenerPromedio(Estudiante estudiante, Curso curso) {
        return servicioNotas.obtenerPromedio(estudiante, curso);
    }

    public double calcularNotaFinal(Estudiante estudiante, Curso curso, Map<Evaluacion, Double> ponderaciones) {
        double sumaPonderaciones = ponderaciones.values().stream().mapToDouble(Double::doubleValue).sum();
        if (Math.abs(sumaPonderaciones - 100.0) > 0.01) {
            throw new IllegalArgumentException("La suma de ponderaciones debe ser 100%.");
        }
        return servicioNotas.calcularNotaFinal(estudiante, curso, ponderaciones);
    }

    public Map<Evaluacion, Calificacion> listarCalificaciones(Estudiante estudiante, Curso curso) {
        Map<Evaluacion, Calificacion> calificaciones = new HashMap<>();
        for (Evaluacion eval : curso.getEvaluaciones()) {
            Calificacion cal = eval.consultarCalificacion(estudiante);
            if (cal != null) {
                calificaciones.put(eval, cal);
            }
        }
        return calificaciones;
    }
}