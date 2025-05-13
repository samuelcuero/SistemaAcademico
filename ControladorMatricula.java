/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.controlador;

/**
 *
 * @author samue
 */

import com.mycompany.servicios.ServicioMatricula;
import com.mycompany.modelo.Estudiante;
import com.mycompany.modelo.Curso;
import com.mycompany.modelo.Matricula;
import com.mycompany.modelo.PeriodoAcademico;
import com.mycompany.util.AuditLog;
import com.mycompany.servicios.ServicioArchivo;

import java.util.ArrayList;

public class ControladorMatricula {
    private final ServicioMatricula servicioMatricula;
    private final ServicioArchivo servicioArchivo;

    public ControladorMatricula() {
        this.servicioMatricula = new ServicioMatricula();
        this.servicioArchivo = new ServicioArchivo();
    }

    public Matricula crearMatricula(Estudiante estudiante, PeriodoAcademico periodo, String usuarioId) {
        if (estudiante == null) {
            throw new IllegalArgumentException("El estudiante no puede ser nulo.");
        }
        if (periodo == null || !periodo.isMatriculaAbierta()) {
            throw new IllegalArgumentException("El período de matrícula no está abierto.");
        }
        Matricula matricula = servicioMatricula.crearMatricula(estudiante, periodo);
        AuditLog log = new AuditLog(usuarioId, "CREAR_MATRICULA",
                "Estudiante: " + estudiante.getId() + ", Período: " + periodo.getNombre());
        servicioArchivo.guardarAuditLog(log, "audit.log");
        return matricula;
    }

    public void matricularCurso(Matricula matricula, Curso curso, String usuarioId) {
        if (matricula == null || curso == null) {
            throw new IllegalArgumentException("Matrícula o curso inválido.");
        }
        servicioMatricula.matricularCurso(matricula, curso);
        AuditLog log = new AuditLog(usuarioId, "MATRICULAR_CURSO",
                "Estudiante: " + matricula.getEstudiante().getId() + ", Curso: " + curso.getCodigo());
        servicioArchivo.guardarAuditLog(log, "audit.log");
    }

    public ArrayList<Curso> listarCursosDisponibles() {
        return servicioMatricula.listarCursosDisponibles();
    }
}