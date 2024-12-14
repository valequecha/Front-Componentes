package com.ulatina.controller;

import Entity.Usuario;
import Servicio.ServicioUsuario;
import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

@ManagedBean
@SessionScoped
public class LoginController implements Serializable{

    private String email;
    private String contrasena;
    private String nombre;
    private String rol;
    private Usuario usuarioActual;
    private boolean autenticado;

    private ServicioUsuario servicioUsuario;

    // Inicialización del EntityManager y del ServicioUsuario
    public LoginController() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("ProyectoComponentesBackEnd");
        EntityManager em = emf.createEntityManager();
        servicioUsuario = new ServicioUsuario(em);
        autenticado = false;
    }

    // Método para manejar el inicio de sesión
    public String iniciarSesion() {
        if (servicioUsuario.validarCredenciales(email, contrasena)) {
            usuarioActual = servicioUsuario.buscarUsuarioPorEmail(email);
            System.out.println("Usuario actual: " + usuarioActual.getNombre() + ", Rol: " + usuarioActual.getRol());
            autenticado = true;
            limpiarCampos(); // Limpia los campos después del inicio de sesión
            return "home.xhtml?faces-redirect=true"; // Redirige a la página de inicio
        } else {
            autenticado = false;
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Credenciales incorrectas"));
            return null; // Permanece en la misma página si las credenciales son incorrectas
        }
    }

// Método para registrar un nuevo usuario
    public String registrarUsuario() {
        try {
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setEmail(email);
            nuevoUsuario.setContrasena(contrasena);
            nuevoUsuario.setNombre(nombre);
            nuevoUsuario.setRol(rol);

            servicioUsuario.registrarUsuario(nuevoUsuario);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Usuario registrado correctamente"));
            limpiarCampos(); // Limpia los campos después del registro
            return "login.xhtml?faces-redirect=true"; // Redirige a la página de login después del registro
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo registrar el usuario: " + e.getMessage()));
            return null; // Permanece en la misma página si hay un error
        }
    }

    // Método para cerrar sesión
    public String cerrarSesion() {
        usuarioActual = null;
        autenticado = false;
        email = null;
        contrasena = null;
        return "login.xhtml?faces-redirect=true"; // Redirige a la página de login
    }

    // Método para limpiar los campos del formulario
    public void limpiarCampos() {
        email = null;
        contrasena = null;
        nombre = null;
        rol = null;
    }

    public boolean esAdmin() {
        if (usuarioActual != null && usuarioActual.getRol() != null) {
            return usuarioActual.getRol().equalsIgnoreCase("admin");
        }
        return false;
    }

    // Método para preparar el formulario al cargar la página
    public void prepararFormulario() {
        limpiarCampos();
    }

    // Getters y Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public ServicioUsuario getServicioUsuario() {
        return servicioUsuario;
    }

    public void setServicioUsuario(ServicioUsuario servicioUsuario) {
        this.servicioUsuario = servicioUsuario;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public void setUsuarioActual(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
    }

    public boolean isAutenticado() {
        return autenticado;
    }

    public void setAutenticado(boolean autenticado) {
        this.autenticado = autenticado;
    }
}