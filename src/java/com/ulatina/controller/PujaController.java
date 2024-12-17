package com.ulatina.controller;

import Entity.Puja;
import Entity.Subasta;
import Entity.Usuario;
import Servicio.ServicioPuja;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


@ManagedBean(name = "pujaController")
@ViewScoped
public class PujaController implements Serializable {

    EntityManagerFactory emf = Persistence.createEntityManagerFactory("ProyectoComponentesBackEnd");
    EntityManager em = emf.createEntityManager();
    
    private Puja puja = new Puja();
    private final ServicioPuja servicioPuja = new ServicioPuja(em);

    @ManagedProperty("#{loginController}")
    private LoginController loginController;
    
    
    @ManagedProperty("#{usuarioController}")
    private UsuarioController usuarioController;

    @PostConstruct
    public void init() {
        if (loginController == null) {
            System.err.println("UsuarioController no inyectado.");
        }
    }

    public Puja getPuja() {
        return puja;
    }

    public void setPuja(Puja puja) {
        this.puja = puja;
    }

    public LoginController getLoginController() {
        return loginController;
    }

    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }
    
    public void prepararNuevaPuja(Subasta subasta) {
    this.puja = new Puja(); // Crear una nueva instancia para limpiar valores
    this.puja.setSubasta(subasta); // Asociar la subasta seleccionada
}

    public UsuarioController getUsuarioController() {
        return usuarioController;
    }

    public void setUsuarioController(UsuarioController usuarioController) {
        this.usuarioController = usuarioController;
    }

    public void registrarPuja() {
    FacesContext context = FacesContext.getCurrentInstance();
    try {
        // Asignar usuario autenticado a la puja
        Usuario usuarioActual = loginController.getUsuarioActual();
        puja.setUsuario(usuarioActual);

        // Verificar que haya un usuario y una subasta seleccionada
        if (puja.getSubasta() == null || puja.getUsuario() == null) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Subasta o usuario no seleccionados."));
            return;
        }

        // Obtener la puja más alta actual, si existe
        Puja pujaMasAlta = servicioPuja.obtenerPujaMasAlta(puja.getSubasta().getId());
        if (pujaMasAlta != null && puja.getMonto() > pujaMasAlta.getMonto()) {
            
        }

        // Registrar la nueva puja
        if (servicioPuja.registrarPuja(puja)) {
            context.addMessage(null, new FacesMessage("Puja realizada exitosamente."));
            // Recargar las pujas para el usuario actual
            usuarioController.recargarPujas();
        } else {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo registrar la puja."));
        }
    } catch (Exception e) {
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error grave", e.getMessage()));
        e.printStackTrace();
    } finally {
        // Resetear la puja después de procesar
        this.puja = new Puja();
    }
}

}
