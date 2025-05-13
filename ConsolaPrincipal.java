/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vista;

/**
 *
 * @author samue
 */

import com.mycompany.controlador.ControladorUsuario;
import com.mycompany.servicios.ServicioUsuario;
import com.mycompany.servicios.ServicioArchivo;
import com.mycompany.modelo.Carrera;
import com.mycompany.modelo.Usuario;
import com.mycompany.modelo.Estudiante;
import com.mycompany.modelo.Docente;
import com.mycompany.modelo.Administrativo;

import com.mycompany.util.ValidadorEntrada;
import java.util.List;
import java.util.Scanner;

import java.util.InputMismatchException;


public class ConsolaPrincipal {
    private final ServicioUsuario servicioUsuario;
    private final ServicioArchivo servicioArchivo;
    private final ControladorUsuario controladorUsuario;
    private final Scanner scanner;

    public ConsolaPrincipal() {
        this.servicioUsuario = new ServicioUsuario();
        this.servicioArchivo = new ServicioArchivo();
        this.controladorUsuario = new ControladorUsuario();
        this.scanner = new Scanner(System.in);
    }

    public void iniciar() {
        while (true) {
            System.out.println("\n=== Sistema de Gestión Estudiantil ===");
            System.out.println("1. Iniciar sesión");
            System.out.println("2. Registrar persona");
            System.out.println("3. Salir");
            System.out.print("Seleccione una opción (1-3): ");

            try {
                int opcion = scanner.nextInt();
                scanner.nextLine();

                switch (opcion) {
                    case 1:
                        autenticarUsuario();
                        break;
                    case 2:
                        registrarPersona();
                        break;
                    case 3:
                        System.out.println("Saliendo del sistema...");
                        scanner.close();
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

    private void autenticarUsuario() {
        System.out.print("ID: ");
        String id = scanner.nextLine();
        System.out.print("Contraseña: ");
        String password = scanner.nextLine();

        Usuario usuario = controladorUsuario.autenticar(id, password);
        if (usuario == null) {
            System.out.println("Error: ID o contraseña incorrectos.");
            return;
        }

        // Redirigir al menú correspondiente según el tipo de usuario
        if (usuario instanceof Estudiante) {
            MenuEstudiante menuEstudiante = new MenuEstudiante();
            menuEstudiante.mostrarMenu((Estudiante) usuario);
        } else if (usuario instanceof Docente) {
            MenuProfesor menuProfesor = new MenuProfesor();
            menuProfesor.mostrarMenu(); // Nota: Este método pide el código de docente, ajustar si es necesario
        } else if (usuario instanceof Administrativo) {
            MenuAdmin menuAdmin = new MenuAdmin();
            menuAdmin.mostrarMenu((Administrativo) usuario);
        } else {
            System.out.println("Error: Tipo de usuario no reconocido.");
        }
    }

    private void registrarPersona() {
        System.out.println("\n=== Registrar Persona ===");
        System.out.println("1. Estudiante");
        System.out.println("2. Docente");
        System.out.println("3. Administrativo");
        System.out.print("Seleccione el tipo de persona (1-3): ");

        try {
            int tipo = scanner.nextInt();
            scanner.nextLine();

            if (tipo < 1 || tipo > 3) {
                System.out.println("Tipo inválido. Por favor, seleccione un número entre 1 y 3.");
                return;
            }

            System.out.print("ID: ");
            String id = scanner.nextLine();
            // Verificar si el ID ya existe
            if (servicioUsuario.buscarEstudiantePorCodigo(id) != null ||
                servicioUsuario.buscarDocentePorCodigo(id) != null ||
                servicioUsuario.buscarAdministrativoPorCodigo(id) != null) {
                System.out.println("Error: El ID ya está registrado.");
                return;
            }
            System.out.print("Nombre: ");
            String nombre = scanner.nextLine();

            String correo;
            do {
                System.out.print("Correo: ");
                correo = scanner.nextLine();
                if (!ValidadorEntrada.validarCorreo(correo)) {
                    System.out.println("Correo inválido. Por favor, ingrese un correo válido (ej. usuario@dominio.com).");
                }
            } while (!ValidadorEntrada.validarCorreo(correo));

            String telefono;
            do {
                System.out.print("Teléfono (9 dígitos): ");
                telefono = scanner.nextLine();
                if (!ValidadorEntrada.validarTelefono(telefono)) {
                    System.out.println("Teléfono inválido. Por favor, ingrese un número de 9 dígitos.");
                }
            } while (!ValidadorEntrada.validarTelefono(telefono));

            String cedula;
            do {
                System.out.print("Cédula (8 dígitos): ");
                cedula = scanner.nextLine();
                if (!ValidadorEntrada.validarCedula(cedula)) {
                    System.out.println("Cédula inválida. Por favor, ingrese un número de 8 dígitos.");
                }
            } while (!ValidadorEntrada.validarCedula(cedula));

            System.out.print("Contraseña: ");
            String password = scanner.nextLine();
            if (password.trim().isEmpty()) {
                System.out.println("Error: La contraseña no puede estar vacía.");
                return;
            }

            try {
                switch (tipo) {
                    case 1:
                        System.out.print("Código de estudiante: ");
                        String codigoEstudiante = scanner.nextLine();
                        List<Carrera> carreras = servicioUsuario.getCarreras();
                        if (carreras.isEmpty()) {
                            System.out.println("Error: No hay carreras disponibles. Registre una carrera primero.");
                            return;
                        }
                        System.out.println("Carreras disponibles:");
                        for (int i = 0; i < carreras.size(); i++) {
                            System.out.println((i + 1) + ". " + carreras.get(i).getNombre() + " (Código: " + carreras.get(i).getId() + ")");
                        }
                        System.out.print("Seleccione una carrera (1-" + carreras.size() + "): ");
                        int carreraIndex = scanner.nextInt() - 1;
                        scanner.nextLine();
                        if (carreraIndex < 0 || carreraIndex >= carreras.size()) {
                            System.out.println("Error: Selección inválida.");
                            return;
                        }
                        Carrera carrera = carreras.get(carreraIndex);
                        controladorUsuario.crearEstudiante(id, nombre, correo, telefono, cedula, codigoEstudiante, carrera, password, id);
                        System.out.println("Estudiante registrado con éxito.");
                        break;
                    case 2:
                        System.out.print("Código de docente: ");
                        String codigoDocente = scanner.nextLine();
                        controladorUsuario.crearDocente(id, nombre, correo, telefono, cedula, codigoDocente, password, id);
                        System.out.println("Docente registrado con éxito.");
                        break;
                    case 3:
                        System.out.print("Código de administrativo: ");
                        String codigoAdministrativo = scanner.nextLine();
                        controladorUsuario.crearAdministrativo(id, nombre, correo, telefono, cedula, codigoAdministrativo, password, id);
                        System.out.println("Administrativo registrado con éxito.");
                        break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Error al registrar: " + e.getMessage());
            }
        } catch (InputMismatchException e) {
            System.out.println("Error: Por favor, ingrese un número válido.");
            scanner.nextLine();
        }
    }
}