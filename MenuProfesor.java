/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vista;

/**
 *
 * @author samue
 */


import com.mycompany.servicios.ServicioNotas;
import com.mycompany.servicios.ServicioUsuario;
import com.mycompany.servicios.ServicioMatricula;
import com.mycompany.modelo.Docente;
import com.mycompany.modelo.Estudiante;
import com.mycompany.modelo.Curso;
import com.mycompany.modelo.Evaluacion;
import java.util.Scanner;
import java.util.InputMismatchException;

public class MenuProfesor {
    private final ServicioNotas servicioNotas;
    private final ServicioUsuario servicioUsuario;
    private final Scanner scanner;

    public MenuProfesor() {
        this.servicioNotas = new ServicioNotas();
        this.servicioUsuario = new ServicioUsuario();
        this.scanner = new Scanner(System.in);
    }

    public void mostrarMenu() {
        System.out.print("Ingrese su código de docente: ");
        String codigo = scanner.nextLine();
        Docente docente = servicioUsuario.buscarDocentePorCodigo(codigo);
        while (docente == null) {
            System.out.println("Docente no encontrado. Por favor ingrese nuevamente: ");
            codigo = scanner.nextLine();
            docente = servicioUsuario.buscarDocentePorCodigo(codigo);
        }
        System.out.println("Bienvenido, " + docente.getNombre());

        while (true) {
            System.out.println("\n=== Menú Profesor ===");
            System.out.println("1. Registrar calificación");
            System.out.println("2. Actualizar calificación");
            System.out.println("3. Volver al menú principal");
            System.out.print("Seleccione una opción (1-3): ");

            try {
                int opcion = scanner.nextInt();
                scanner.nextLine();

                switch (opcion) {
                    case 1:
                        System.out.print("Código del curso: ");
                        String codigoCurso = scanner.nextLine();
                        Curso curso = servicioUsuario.buscarCursoPorCodigo(codigoCurso);
                        while (curso == null) {
                            System.out.println("Curso no encontrado. Por favor ingrese nuevamente: ");
                            codigoCurso = scanner.nextLine();
                            curso = servicioUsuario.buscarCursoPorCodigo(codigoCurso);
                        }
                        System.out.println("Curso encontrado: " + curso.getAsignatura().getNombre());
                        if (curso.getEstudiantes().isEmpty()) {
                            System.out.println("No hay estudiantes matriculados en el curso.");
                            break;
                        }
                        System.out.print("Código del estudiante: ");
                        String idEstudiante = scanner.nextLine();
                        Estudiante estudiante = servicioUsuario.buscarEstudiantePorCodigo(idEstudiante);
                        while (estudiante == null) {
                            System.out.println("Estudiante no encontrado. Por favor ingrese nuevamente: ");
                            idEstudiante = scanner.nextLine();
                            estudiante = servicioUsuario.buscarEstudiantePorCodigo(idEstudiante);
                        }
                        System.out.println("Estudiante encontrado: " + estudiante.getNombre());
                        System.out.print("Tipo de evaluación (ej. Parcial 1): ");
                        String tipo = scanner.nextLine();
                        System.out.print("Porcentaje (0-100): ");
                        double porcentaje;
                        try {
                            porcentaje = scanner.nextDouble();
                            if (porcentaje < 0 || porcentaje > 100) {
                                System.out.println("Error: El porcentaje debe estar entre 0 y 100.");
                                scanner.nextLine();
                                break;
                            }
                        } catch (InputMismatchException e) {
                            System.out.println("Error: Por favor, ingrese un número válido para el porcentaje.");
                            scanner.nextLine();
                            break;
                        }
                        System.out.print("Nota (0-100): ");
                        double nota;
                        try {
                            nota = scanner.nextDouble();
                            if (nota < 0 || nota > 100) {
                                System.out.println("Error: La nota debe estar entre 0 y 100.");
                                scanner.nextLine();
                                break;
                            }
                        } catch (InputMismatchException e) {
                            System.out.println("Error: Por favor, ingrese un número válido para la nota.");
                            scanner.nextLine();
                            break;
                        }
                        scanner.nextLine();
                        System.out.print("Observación: ");
                        String observacion = scanner.nextLine();
                        Evaluacion evaluacion = new Evaluacion(tipo, porcentaje);
                        curso.agregarEvaluacion(evaluacion);
                        try {
                            servicioNotas.asignarCalificacion(estudiante, curso, evaluacion, nota, observacion);
                            servicioUsuario.guardarCalificaciones();
                            System.out.println("Calificación registrada con éxito.");
                        } catch (IllegalArgumentException e) {
                            System.out.println("Error: " + e.getMessage());
                        }
                        break;
                    case 2:
                        System.out.print("Código del curso: ");
                        codigoCurso = scanner.nextLine();
                        curso = servicioUsuario.buscarCursoPorCodigo(codigoCurso);
                        while (curso == null) {
                            System.out.println("Curso no encontrado. Por favor ingrese nuevamente: ");
                            codigoCurso = scanner.nextLine();
                            curso = servicioUsuario.buscarCursoPorCodigo(codigoCurso);
                        }
                        System.out.println("Curso encontrado: " + curso.getAsignatura().getNombre());
                        if (curso.getEvaluaciones().isEmpty()) {
                            System.out.println("No hay evaluaciones registradas en el curso.");
                            break;
                        }
                        System.out.print("Código del estudiante: ");
                        idEstudiante = scanner.nextLine();
                        estudiante = servicioUsuario.buscarEstudiantePorCodigo(idEstudiante);
                        while (estudiante == null) {
                            System.out.println("Estudiante no encontrado. Por favor ingrese nuevamente: ");
                            idEstudiante = scanner.nextLine();
                            estudiante = servicioUsuario.buscarEstudiantePorCodigo(idEstudiante);
                        }
                        System.out.println("Estudiante encontrado: " + estudiante.getNombre());
                        System.out.print("Tipo de evaluación (ej. Parcial 1): ");
                        tipo = scanner.nextLine();
                        System.out.print("Nueva nota (0-100): ");
                        try {
                            nota = scanner.nextDouble();
                            if (nota < 0 || nota > 100) {
                                System.out.println("Error: La nota debe estar entre 0 y 100.");
                                scanner.nextLine();
                                break;
                            }
                        } catch (InputMismatchException e) {
                            System.out.println("Error: Por favor, ingrese un número válido para la nota.");
                            scanner.nextLine();
                            break;
                        }
                        scanner.nextLine();
                        evaluacion = new Evaluacion(tipo, 0);
                        try {
                            servicioNotas.actualizarNota(estudiante, curso, evaluacion, nota);
                            servicioUsuario.guardarCalificaciones();
                            System.out.println("Nota actualizada con éxito.");
                        } catch (IllegalArgumentException e) {
                            System.out.println("Error: " + e.getMessage());
                        }
                        break;
                    case 3:
                        System.out.println("Volviendo al menú principal...");
                        return;
                    default:
                        System.out.println("Opción inválida. Por favor, seleccione un número entre 1 y 3.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Error: Por favor, ingrese un número válido.");
                scanner.nextLine();
            }
        }
    }
}