package com.ulatina.controller;

import Entity.Puja;
import Entity.Usuario;
import Servicio.ServicioPuja;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.context.ExternalContext;
import java.io.Serializable;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author User
 */
@ManagedBean(name = "perfilUsuarioController")
@SessionScoped
public class UsuarioController implements Serializable {

    private static final long serialVersionUID = 1L;
    private Usuario usuario;
    private Usuario usuarioActual;
    private Puja selectedPuja;
    private List<Puja> selectedPujas;
    private List<Puja> pujas;

    EntityManagerFactory emf = Persistence.createEntityManagerFactory("ProyectoComponentesBackEnd");
    EntityManager em = emf.createEntityManager();

    private final ServicioPuja servicioPuja = new ServicioPuja(em);

    @PostConstruct
    public void init() {
        try {
            // Obtener el usuario logueado
            usuarioActual = getUsuarioActual();

            if (usuarioActual != null) {
                // Recargar las pujas y cargar el historial de puntuaciones
                recargarPujas();
                pujas = servicioPuja.obtenerPujasPorUsuario(usuarioActual.getId());
            } else {
                System.err.println("No se encontró un usuario logueado en la sesión.");
            }

            // Inicializar la puja seleccionada
            selectedPuja = new Puja();
            selectedPuja.setMonto(0.0);
        } catch (Exception ex) {
            Logger.getLogger(UsuarioController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Usuario getUsuarioActual() {
        return usuario;
    }

    public List<Puja> getPujas() {
        return pujas;
    }

    public void setPujas(List<Puja> pujas) {
        this.pujas = pujas;
    }

    public Puja getSelectedPuja() {
        return selectedPuja;
    }

    public void setSelectedPuja(Puja selectedPuja) {
        this.selectedPuja = selectedPuja;
    }

    public List<Puja> getSelectedPujas() {
        return selectedPujas;
    }

    public void setSelectedPujas(List<Puja> selectedPujas) {
        this.selectedPujas = selectedPujas;
    }

    

    public Usuario getUsuario() {
        if (usuario == null) {
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            usuario = (Usuario) externalContext.getSessionMap().get("usuarioActual");

            if (usuario == null) {
                System.out.println("El usuario no está en la sesión.");
                usuario = new Usuario(); // Evita errores en la vista si el usuario sigue siendo nulo
            } else {
                System.out.println("Usuario recuperado de la sesión:");
                System.out.println("Nombre: " + usuario.getNombre());
                System.out.println("Correo: " + usuario.getEmail());
            }
        }
        return usuario;
    }

    // Método para cerrar sesión
    public void cerrarSesion() {
        // Eliminar el usuario de la sesión
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        externalContext.getSessionMap().remove("usuarioActual");

        // Redirigir a la página de login
        try {
            externalContext.redirect("Login.xhtml"); // Asegúrate de que Login.xhtml esté en el mismo nivel de directorio
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void mostrarDatosUsuario() {
        System.out.println("Nombre: " + usuario.getNombre());
        System.out.println("Correo: " + usuario.getEmail());
    }

    public void editarPuja(Puja puja) {
        if (puja != null) {
            this.selectedPuja = puja;
            System.out.println("Puja seleccionada para edición: ID " + puja.getId());
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo cargar la puja seleccionada."));
            // Inicializa selectedPuja para evitar valores nulos
            this.selectedPuja.setMonto(0.0); // Establece un valor predeterminado para evitar errores
        }
    }

    public void savePuja() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if (selectedPuja == null) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No hay puja seleccionada."));
                return;
            }

            // Verificar si el ID es inválido
            if (selectedPuja.getId() <= 0) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El ID de la puja no puede ser menor o igual a cero."));
                return;
            }

            // Editar puja existente
            if (servicioPuja.editarPuja(selectedPuja)) {
                context.addMessage(null, new FacesMessage("Puja actualizada exitosamente"));
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo actualizar la puja"));
            }

            // Actualizar la lista de pujas y limpiar selección
            pujas = servicioPuja.obtenerPujasPorUsuario(usuarioActual.getId());
            selectedPuja = null;

        } catch (Exception e) {
            // Manejo de errores inesperados
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error grave", "Ocurrió un error inesperado: " + e.getMessage()));
            e.printStackTrace();
        }
    }

    public void eliminarPuja(Puja puja) {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if (puja == null) {
                context.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "No se ha seleccionado ninguna puja para eliminar."));
                return;
            }
            // Intentar eliminar la puja
            if (servicioPuja.eliminarPuja(puja.getId())) {
                context.addMessage(null,
                        new FacesMessage("Puja eliminada exitosamente: ID " + puja.getId()));
                pujas.remove(puja); // Eliminar de la lista
            } else {
                context.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo eliminar la puja: ID " + puja.getId()));
            }
        } catch (Exception e) {
            context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error grave", "Ocurrió un error inesperado al eliminar la puja: " + e.getMessage()));
            e.printStackTrace();
        }
    }

    public void recargarPujas() {
        try {
            if (usuarioActual != null) {
                pujas = servicioPuja.obtenerPujasPorUsuario(usuarioActual.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
