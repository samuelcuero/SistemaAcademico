/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vista;

/**
 *
 * @author samue
 */

/**
 *
 * @author samue
 */

import com.mycompany.servicios.ServicioArchivo;
import com.mycompany.servicios.ServicioMatricula;
import com.mycompany.servicios.ServicioUsuario;
import com.mycompany.modelo.*;
import com.mycompany.util.AuditLog;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Scanner;
import java.util.InputMismatchException;

public class MenuAdmin {
    private final ServicioMatricula servicioMatricula;
    private final ServicioUsuario servicioUsuario;
    private final ServicioArchivo servicioArchivo;
    private final Scanner scanner;

    public MenuAdmin() {
        this.servicioMatricula = new ServicioMatricula();
        this.servicioUsuario = new ServicioUsuario();
        this.servicioArchivo = new ServicioArchivo();
        this.scanner = new Scanner(System.in);
    }

    public void mostrarMenu(Administrativo administrativo) {
        System.out.println("Bienvenido, " + administrativo.getNombre());

        while (true) {
            System.out.println("\n=== Menú Administrador ===");
            System.out.println("1. Crear matrícula");
            System.out.println("2. Matricular curso");
            System.out.println("3. Consultar cursos disponibles");
            System.out.println("4. Crear asignatura");
            System.out.println("5. Eliminar docente");
            System.out.println("6. Crear departamento");
            System.out.println("7. Crear curso");
            System.out.println("8. Crear carrera");
            System.out.println("9. Volver al menú principal");
            System.out.print("Seleccione una opción (1-9): ");

            try {
                int opcion = scanner.nextInt();
                scanner.nextLine();

                switch (opcion) {
                    case 1:
                        System.out.print("Código del estudiante: ");
                        String idEstudiante = scanner.nextLine();
                        Estudiante estudiante = servicioUsuario.buscarEstudiantePorCodigo(idEstudiante);
                        while (estudiante == null) {
                            System.out.println("Estudiante no encontrado. Por favor ingrese nuevamente: ");
                            idEstudiante = scanner.nextLine();
                            estudiante = servicioUsuario.buscarEstudiantePorCodigo(idEstudiante);
                        }
                        System.out.println("Estudiante encontrado: " + estudiante.getNombre());
                        PeriodoAcademico periodo = seleccionarOCrearPeriodo();
                        if (periodo == null) {
                            System.out.println("Error: No se pudo crear o seleccionar un período académico.");
                            break;
                        }
                        try {
                            Matricula matricula = servicioMatricula.crearMatricula(estudiante, periodo);
                            System.out.println("Matrícula creada con éxito: " + matricula);
                            servicioArchivo.guardarAuditLog(new AuditLog(administrativo.getId(), "CREAR_MATRICULA", "Estudiante: " + idEstudiante + ", Período: " + periodo.getNombre()), "audit.log");
                        } catch (IllegalArgumentException e) {
                            System.out.println("Error: " + e.getMessage());
                        }
                        break;
                    case 2:
                        System.out.print("Código del estudiante: ");
                        idEstudiante = scanner.nextLine();
                        estudiante = servicioUsuario.buscarEstudiantePorCodigo(idEstudiante);
                        while (estudiante == null) {
                            System.out.println("Estudiante no encontrado. Por favor ingrese nuevamente: ");
                            idEstudiante = scanner.nextLine();
                            estudiante = servicioUsuario.buscarEstudiantePorCodigo(idEstudiante);
                        }
                        System.out.println("Estudiante encontrado: " + estudiante.getNombre());
                        System.out.print("Código del curso: ");
                        String codigoCurso = scanner.nextLine();
                        Curso curso = servicioUsuario.buscarCursoPorCodigo(codigoCurso);
                        while (curso == null) {
                            System.out.println("Curso no encontrado. Por favor ingrese nuevamente: ");
                            codigoCurso = scanner.nextLine();
                            curso = servicioUsuario.buscarCursoPorCodigo(codigoCurso);
                        }
                        System.out.println("Curso encontrado: " + curso.getAsignatura().getNombre());
                        periodo = seleccionarOCrearPeriodo();
                        if (periodo == null) {
                            System.out.println("Error: No se pudo crear o seleccionar un período académico.");
                            break;
                        }
                        Matricula matricula = new Matricula(estudiante, periodo);
                        try {
                            servicioMatricula.matricularCurso(matricula, curso);
                            servicioUsuario.registrarEstudiante(estudiante);
                            System.out.println("Curso matriculado con éxito.");
                            servicioArchivo.guardarAuditLog(new AuditLog(administrativo.getId(), "MATRICULAR_CURSO", "Estudiante: " + idEstudiante + ", Curso: " + codigoCurso), "audit.log");
                        } catch (IllegalArgumentException e) {
                            System.out.println("Error: " + e.getMessage());
                        }
                        break;
                    case 3:
                        List<Curso> cursosDisponibles = servicioMatricula.listarCursosDisponibles();
                        if (cursosDisponibles.isEmpty()) {
                            System.out.println("No hay cursos disponibles para matrícula en este momento.");
                        } else {
                            System.out.println("Cursos disponibles:");
                            for (Curso c : cursosDisponibles) {
                                System.out.println("- " + c.getAsignatura().getNombre() + " (Código: " + c.getCodigo() + ")");
                            }
                        }
                        servicioArchivo.guardarAuditLog(new AuditLog(administrativo.getId(), "CONSULTAR_CURSOS_DISPONIBLES", "Cursos consultados: " + cursosDisponibles.size()), "audit.log");
                        break;
                    case 4:
                        System.out.print("Código de la asignatura: ");
                        String codigoAsignatura = scanner.nextLine();
                        System.out.print("Nombre de la asignatura: ");
                        String nombreAsignatura = scanner.nextLine();
                        System.out.print("Créditos: ");
                        int creditos;
                        try {
                            creditos = scanner.nextInt();
                            scanner.nextLine();
                            if (creditos <= 0) {
                                System.out.println("Error: Los créditos deben ser mayores que 0.");
                                break;
                            }
                        } catch (InputMismatchException e) {
                            System.out.println("Error: Por favor, ingrese un número válido para los créditos.");
                            scanner.nextLine();
                            break;
                        }
                        System.out.print("Código del departamento: ");
                        String codigoDepartamento = scanner.nextLine();
                        Departamento departamento = servicioUsuario.buscarDepartamentoPorId(codigoDepartamento);
                        while (departamento == null) {
                            System.out.println("Departamento no encontrado. Por favor ingrese nuevamente: ");
                            codigoDepartamento = scanner.nextLine();
                            departamento = servicioUsuario.buscarDepartamentoPorId(codigoDepartamento);
                        }
                        Asignatura asignatura = new Asignatura(codigoAsignatura, nombreAsignatura, creditos, departamento);
                        servicioUsuario.registrarAsignatura(asignatura);
                        System.out.println("Asignatura creada con éxito: " + asignatura.getNombre());
                        servicioArchivo.guardarAuditLog(new AuditLog(administrativo.getId(), "CREAR_ASIGNATURA", "Asignatura: " + codigoAsignatura), "audit.log");
                        break;
                    case 5:
                        System.out.print("Código del docente: ");
                        String codigoDocente = scanner.nextLine();
                        Docente docente = servicioUsuario.buscarDocentePorCodigo(codigoDocente);
                        while (docente == null) {
                            System.out.println("Docente no encontrado. Por favor ingrese nuevamente: ");
                            codigoDocente = scanner.nextLine();
                            docente = servicioUsuario.buscarDocentePorCodigo(codigoDocente);
                        }
                        System.out.println("Docente encontrado: " + docente.getNombre());
                        servicioUsuario.getDocentes().remove(docente);
                        servicioArchivo.guardarDocentes(servicioUsuario.getDocentes(), "docentes.txt");
                        System.out.println("Docente eliminado con éxito.");
                        servicioArchivo.guardarAuditLog(new AuditLog(administrativo.getId(), "ELIMINAR_DOCENTE", "Docente: " + codigoDocente), "audit.log");
                        break;
                    case 6:
                        System.out.print("Código del departamento: ");
                        codigoDepartamento = scanner.nextLine();
                        System.out.print("Nombre del departamento: ");
                        String nombreDepartamento = scanner.nextLine();
                        departamento = new Departamento(codigoDepartamento, nombreDepartamento);
                        servicioUsuario.registrarDepartamento(departamento);
                        System.out.println("Departamento creado con éxito: " + departamento.getNombre());
                        servicioArchivo.guardarAuditLog(new AuditLog(administrativo.getId(), "CREAR_DEPARTAMENTO", "Departamento: " + codigoDepartamento), "audit.log");
                        break;
                    case 7:
                        System.out.print("Código del curso: ");
                        codigoCurso = scanner.nextLine();
                        System.out.print("Código de la asignatura: ");
                        codigoAsignatura = scanner.nextLine();
                        asignatura = servicioUsuario.buscarAsignaturaPorCodigo(codigoAsignatura);
                        while (asignatura == null) {
                            System.out.println("Asignatura no encontrada. Por favor ingrese nuevamente: ");
                            codigoAsignatura = scanner.nextLine();
                            asignatura = servicioUsuario.buscarAsignaturaPorCodigo(codigoAsignatura);
                        }
                        System.out.print("Código del docente (opcional, presione Enter para omitir): ");
                        codigoDocente = scanner.nextLine();
                        docente = codigoDocente.isEmpty() ? null : servicioUsuario.buscarDocentePorCodigo(codigoDocente);
                        if (!codigoDocente.isEmpty() && docente == null) {
                            System.out.println("Docente no encontrado. Creando curso sin docente asignado.");
                        }
                        System.out.print("Día de la semana (ej. MONDAY): ");
                        String diaStr = scanner.nextLine();
                        DayOfWeek dia;
                        try {
                            dia = DayOfWeek.valueOf(diaStr.toUpperCase());
                        } catch (IllegalArgumentException e) {
                            System.out.println("Error: Día inválido. Use nombres en inglés (ej. MONDAY).");
                            break;
                        }
                        System.out.print("Hora de inicio (HH:MM, ej. 08:00): ");
                        String horaInicioStr = scanner.nextLine();
                        LocalTime horaInicio;
                        try {
                            horaInicio = LocalTime.parse(horaInicioStr);
                        } catch (Exception e) {
                            System.out.println("Error: Formato de hora inválido. Use HH:MM.");
                            break;
                        }
                        System.out.print("Hora de fin (HH:MM, ej. 10:00): ");
                        String horaFinStr = scanner.nextLine();
                        LocalTime horaFin;
                        try {
                            horaFin = LocalTime.parse(horaFinStr);
                        } catch (Exception e) {
                            System.out.println("Error: Formato de hora inválido. Use HH:MM.");
                            break;
                        }
                        Salon salon = seleccionarOCrearSalon();
                        if (salon == null) {
                            System.out.println("Error: No se pudo crear o seleccionar un salón.");
                            break;
                        }
                        Horario horario = new Horario(dia, horaInicio, horaFin, salon);
                        curso = new Curso(codigoCurso, asignatura, docente, horario);
                        servicioUsuario.registrarCurso(curso);
                        servicioMatricula.agregarCurso(curso);
                        System.out.println("Curso creado con éxito: " + curso.getAsignatura().getNombre());
                        servicioArchivo.guardarAuditLog(new AuditLog(administrativo.getId(), "CREAR_CURSO", "Curso: " + codigoCurso), "audit.log");
                        break;
                    case 8:
                        System.out.print("Código de la carrera: ");
                        String codigoCarrera = scanner.nextLine();
                        System.out.print("Nombre de la carrera: ");
                        String nombreCarrera = scanner.nextLine();
                        Carrera carrera = new Carrera(codigoCarrera, nombreCarrera);
                        try {
                            servicioUsuario.registrarCarrera(carrera);
                            System.out.println("Carrera creada con éxito: " + carrera.getNombre());
                            servicioArchivo.guardarAuditLog(new AuditLog(administrativo.getId(), "CREAR_CARRERA", "Carrera: " + codigoCarrera), "audit.log");
                        } catch (IllegalArgumentException e) {
                            System.out.println("Error: " + e.getMessage());
                        }
                        break;
                    case 9:
                        System.out.println("Volviendo al menú principal...");
                        return;
                    default:
                        System.out.println("Opción inválida. Por favor, seleccione un número entre 1 y 9.");
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

    private Salon seleccionarOCrearSalon() {
        List<Salon> salones = servicioUsuario.getSalones();
        if (salones.isEmpty()) {
            System.out.println("No hay salones registrados. Cree uno nuevo.");
        } else {
            System.out.println("Salones disponibles:");
            for (int i = 0; i < salones.size(); i++) {
                System.out.println((i + 1) + ". " + salones.get(i).getNumero() + " (Ubicación: " + salones.get(i).getUbicacion() + ")");
            }
            System.out.print("Seleccione un salón (1-" + salones.size() + ") o 0 para crear uno nuevo: ");
            try {
                int opcion = scanner.nextInt();
                scanner.nextLine();
                if (opcion >= 1 && opcion <= salones.size()) {
                    return salones.get(opcion - 1);
                }
            } catch (InputMismatchException e) {
                System.out.println("Error: Por favor, ingrese un número válido.");
                scanner.nextLine();
            }
        }
        System.out.print("Número del salón: ");
        String numero = scanner.nextLine();
        System.out.print("Capacidad: ");
        int capacidad;
        try {
            capacidad = scanner.nextInt();
            scanner.nextLine();
            if (capacidad <= 0) {
                System.out.println("Error: La capacidad debe ser mayor que 0.");
                return null;
            }
        } catch (InputMismatchException e) {
            System.out.println("Error: Por favor, ingrese un número válido para la capacidad.");
            scanner.nextLine();
            return null;
        }
        System.out.print("Ubicación (ej. Edificio A): ");
        String ubicacion = scanner.nextLine();
        Salon salon = new Salon(numero, capacidad, ubicacion);
        servicioUsuario.registrarSalon(salon);
        return salon;
    }
}