/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vista;

/**
 *
 * @author samue
 */

import com.mycompany.servicios.ServicioEstudiante;
import com.mycompany.servicios.ServicioMatricula;
import com.mycompany.servicios.ServicioUsuario;
import com.mycompany.modelo.Estudiante;
import com.mycompany.modelo.Curso;
import com.mycompany.modelo.Matricula;
import com.mycompany.modelo.PeriodoAcademico;
import com.mycompany.servicios.ServicioArchivo;
import com.mycompany.util.AuditLog;
import java.util.List;
import java.util.Scanner;
import java.util.InputMismatchException;

public class MenuEstudiante {
    private final ServicioEstudiante servicioEstudiante;
    private final ServicioMatricula servicioMatricula;
    private final ServicioUsuario servicioUsuario;
    private final ServicioArchivo servicioArchivo;
    private final Scanner scanner;

    public MenuEstudiante() {
        this.servicioEstudiante = new ServicioEstudiante();
        this.servicioMatricula = new ServicioMatricula();
        this.servicioUsuario = new ServicioUsuario();
        this.servicioArchivo = new ServicioArchivo();
        this.scanner = new Scanner(System.in);
    }

    public void mostrarMenu(Estudiante estudiante) {
        System.out.println("Bienvenido, " + estudiante.getNombre());

        while (true) {
            System.out.println("\n=== Menú Estudiante ===");
            System.out.println("1. Consultar cursos matriculados");
            System.out.println("2. Consultar promedio acumulado");
            System.out.println("3. Matricular curso");
            System.out.println("4. Volver al menú principal");
            System.out.print("Seleccione una opción (1-4): ");

            try {
                int opcion = scanner.nextInt();
                scanner.nextLine();

                switch (opcion) {
                    case 1:
                        List<String> cursos = servicioEstudiante.consultarCursosMatriculados(estudiante, estudiante.getId());
                        if (cursos.isEmpty()) {
                            System.out.println("No hay cursos matriculados en este momento.");
                        } else {
                            System.out.println("Cursos matriculados:");
                            for (String curso : cursos) {
                                System.out.println("- " + curso);
                            }
                        }
                        break;
                    case 2:
                        double promedio = servicioEstudiante.consultarPromedioAcumulado(estudiante, estudiante.getId());
                        if (promedio == 0 && estudiante.getCursosMatriculados().isEmpty()) {
                            System.out.println("No hay calificaciones registradas para calcular el promedio.");
                        } else {
                            System.out.println("Promedio acumulado: " + promedio);
                        }
                        break;
                    case 3:
                        System.out.print("Código del curso: ");
                        String codigoCurso = scanner.nextLine();
                        Curso curso = servicioUsuario.buscarCursoPorCodigo(codigoCurso);
                        while (curso == null) {
                            System.out.println("Curso no encontrado. Por favor ingrese nuevamente: ");
                            codigoCurso = scanner.nextLine();
                            curso = servicioUsuario.buscarCursoPorCodigo(codigoCurso);
                        }
                        System.out.println("Curso encontrado: " + curso.getAsignatura().getNombre());
                        PeriodoAcademico periodo = seleccionarOCrearPeriodo();
                        if (periodo == null) {
                            System.out.println("Error: No se pudo crear o seleccionar un período académico.");
                            break;
                        }
                        Matricula matricula = new Matricula(estudiante, periodo);
                        try {
                            servicioMatricula.matricularCurso(matricula, curso);
                            servicioUsuario.registrarEstudiante(estudiante);
                            System.out.println("Curso matriculado con éxito.");
                            servicioArchivo.guardarAuditLog(new AuditLog(estudiante.getId(), "MATRICULAR_CURSO", "Curso: " + codigoCurso), "audit.log");
                        } catch (IllegalArgumentException e) {
                            System.out.println("Error: " + e.getMessage());
                        }
                        break;
                    case 4:
                        System.out.println("Volviendo al menú principal...");
                        return;
                    default:
                        System.out.println("Opción inválida. Por favor, seleccione un número entre 1 y 4.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Error: Por favor, ingrese un número válido.");
                scanner.nextLine();
            }
        }
    }

    private PeriodoAcademico seleccionarOCrearPeriodo() {
        List<PeriodoAcademico> periodos = servicioUsuario.getPeriodosAcademicos();
        if (periodos.isEmpty()) {
            System.out.println("No hay períodos académicos registrados. Cree uno nuevo.");
        } else {
            System.out.println("Períodos académicos disponibles:");
            for (int i = 0; i < periodos.size(); i++) {
                System.out.println((i + 1) + ". " + periodos.get(i).getNombre() + " (Año: " + periodos.get(i).getAnio() + ", Semestre: " + periodos.get(i).getSemestre() + ")");
            }
            System.out.print("Seleccione un período (1-" + periodos.size() + ") o 0 para crear uno nuevo: ");
            try {
                int opcion = scanner.nextInt();
                scanner.nextLine();
                if (opcion >= 1 && opcion <= periodos.size()) {
                    return periodos.get(opcion - 1);
                }
            } catch (InputMismatchException e) {
                System.out.println("Error: Por favor, ingrese un número válido.");
                scanner.nextLine();
            }
        }
        System.out.print("Nombre del período (ej. 2025-I): ");
        String nombre = scanner.nextLine();
        System.out.print("Año del período: ");
        int anio;
        try {
            anio = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Error: Por favor, ingrese un número válido para el año.");
            scanner.nextLine();
            return null;
        }
        System.out.print("Semestre del período (1 o 2): ");
        int semestre;
        try {
            semestre = scanner.nextInt();
            scanner.nextLine();
            if (semestre != 1 && semestre != 2) {
                System.out.println("Error: El semestre debe ser 1 o 2.");
                return null;
            }
        } catch (InputMismatchException e) {
            System.out.println("Error: Por favor, ingrese un número válido para el semestre.");
            scanner.nextLine();
            return null;
        }
        PeriodoAcademico periodo = new PeriodoAcademico(nombre, anio, semestre);
        servicioUsuario.registrarPeriodoAcademico(periodo);
        return periodo;
    }
}