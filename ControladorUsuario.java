/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.controlador;

/**
 *
 * @author samue
 */

import com.mycompany.servicios.ServicioUsuario;
import com.mycompany.servicios.ServicioArchivo;
import com.mycompany.modelo.Carrera;
import com.mycompany.modelo.Usuario;
import com.mycompany.modelo.Estudiante;
import com.mycompany.modelo.Docente;
import com.mycompany.modelo.Administrativo;
import com.mycompany.util.AuditLog;

public class ControladorUsuario {
    private final ServicioUsuario servicioUsuario;
    private final ServicioArchivo servicioArchivo;

    public ControladorUsuario() {
        this.servicioUsuario = new ServicioUsuario();
        this.servicioArchivo = new ServicioArchivo();
    }

    public void consultarPerfil(Usuario usuario) {
        if (usuario != null) {
            usuario.consultarPerfil();
        } else {
            throw new IllegalArgumentException("Usuario no puede ser nulo.");
        }
    }

    public Estudiante crearEstudiante(String id, String nombre, String correo, String telefono, String cedula, String codigoEstudiante, Carrera carrera, String password, String usuarioId) {
        Estudiante estudiante = new Estudiante(id, nombre, correo, telefono, cedula, password, codigoEstudiante, carrera);
        servicioUsuario.registrarEstudiante(estudiante);
        AuditLog log = new AuditLog(usuarioId, "CREAR_ESTUDIANTE", "ID: " + id);
        servicioArchivo.guardarAuditLog(log, "audit.log");
        return estudiante;
    }

    public Docente crearDocente(String id, String nombre, String correo, String telefono, String cedula, String codigoDocente, String password, String usuarioId) {
        Docente docente = new Docente(id, nombre, correo, telefono, cedula, password, codigoDocente);
        servicioUsuario.registrarDocente(docente);
        AuditLog log = new AuditLog(usuarioId, "CREAR_DOCENTE", "ID: " + id);
        servicioArchivo.guardarAuditLog(log, "audit.log");
        return docente;
    }

    public Administrativo crearAdministrativo(String id, String nombre, String correo, String telefono, String cedula, String codigoAdministrativo, String password, String usuarioId) {
        Administrativo administrativo = new Administrativo(id, nombre, correo, telefono, cedula, password, codigoAdministrativo);
        servicioUsuario.registrarAdministrativo(administrativo);
        AuditLog log = new AuditLog(usuarioId, "CREAR_ADMINISTRATIVO", "ID: " + id);
        servicioArchivo.guardarAuditLog(log, "audit.log");
        return administrativo;
    }

    public Usuario autenticar(String id, String password) {
        Object usuario = servicioUsuario.autenticarUsuario(id, password);
        if (usuario instanceof Usuario) {
            return (Usuario) usuario;
        }
        return null; // Autenticación fallida
    }
}