package com.ulatina.controller;

import Entity.Subasta;
import Entity.Usuario;
import Servicio.ServicioPuja;
import Servicio.ServicioSubasta;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@ManagedBean
@RequestScoped
public class SubastaController implements Serializable {

    private String producto;
    private String categoria;
    private Double precioInicial;
    private Double montoFinal;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private boolean cerrada;

    @ManagedProperty(value = "#{loginController}")
    private LoginController loginController;

    private ServicioSubasta servicioSubasta;
    private ServicioPuja servicioPuja;
    private List<Subasta> subastas;

    public SubastaController() {
        servicioSubasta = new ServicioSubasta( 
                javax.persistence.Persistence.createEntityManagerFactory("ProyectoComponentesBackEnd").createEntityManager()
        );
        // Configuración de fechas iniciales por defecto
        fechaInicio = LocalDateTime.now();
        fechaFin = fechaInicio.plusDays(7); // Duración predeterminada de 7 días
        cargarSubastas();
    }

    public String crearSubasta() {
        try {
            if (!loginController.isAutenticado()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Error", "Debes iniciar sesión para crear una subasta"));
                return null;
            }

            Usuario creador = loginController.getUsuarioActual();
            Subasta nuevaSubasta = servicioSubasta.crearSubasta(
                    producto, categoria, creador, precioInicial, fechaInicio, fechaFin
            );

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito",
                            "Subasta creada exitosamente: " + nuevaSubasta.getProducto()));
            limpiarFormulario(); // Limpia los campos después de crear la subasta
            return "misSubastas.xhtml?faces-redirect=true"; // Redirige a la página de gestión de subastas
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo crear la subasta: " + e.getMessage()));
            return null;
        }
    }
    
    public void cargarSubastas() {
        subastas = servicioSubasta.listarSubastas();
    }

    public List<Subasta> getSubastas() {
        return subastas;
    }

    public void limpiarFormulario() {
        producto = null;
        categoria = null;
        precioInicial = null;
        fechaInicio = LocalDateTime.now();
        fechaFin = fechaInicio.plusDays(7);
    }

    // Getters y Setters
    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Double getPrecioInicial() {
        return precioInicial;
    }

    public void setPrecioInicial(Double precioInicial) {
        this.precioInicial = precioInicial;
    }

    public Double getMontoFinal() {
        return montoFinal;
    }

    public void setMontoFinal(Double montoFinal) {
        this.montoFinal = montoFinal;
    }

    public ServicioSubasta getServicioSubasta() {
        return servicioSubasta;
    }

    public void setServicioSubasta(ServicioSubasta servicioSubasta) {
        this.servicioSubasta = servicioSubasta;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public boolean isCerrada() {
        return cerrada;
    }

    public void setCerrada(boolean cerrada) {
        this.cerrada = cerrada;
    }

    public LoginController getLoginController() {
        return loginController;
    }

    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }

    public ServicioPuja getServicioPuja() {
        return servicioPuja;
    }

    public void setServicioPuja(ServicioPuja servicioPuja) {
        this.servicioPuja = servicioPuja;
    }

    private static class servicioPuja {

        public servicioPuja() {
        }
    }
    
    
    
}