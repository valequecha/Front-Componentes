package com.ulatina.controller;

import Entity.Puja;
import Entity.Subasta;
import Entity.Usuario;
import Servicio.ServicioPuja;
import Servicio.ServicioSubasta;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.time.LocalDateTime;

@ManagedBean
@SessionScoped
public class PujaController {
    
     // Asegúrate de tener un constructor público sin argumentos
    public PujaController() {
        // Inicialización si es necesaria
    }
    
    private ServicioSubasta servicioSubasta;
    private ServicioPuja servicioPuja;
    private Usuario usuarioActual; // Este debe ser el usuario actualmente logueado
    private Long subastaId;
    private Double montoPuja;

    public PujaController(ServicioSubasta servicioSubasta) {
        this.servicioSubasta = servicioSubasta;
    }
    
    public PujaController(ServicioPuja servicioPuja) {
        this.servicioPuja = servicioPuja;
    }

    public void realizarPuja() {
        try {
            Subasta subasta = servicioSubasta.buscarSubastaPorId(subastaId); // Obtiene la subasta por su ID
            if (subasta != null && montoPuja > subasta.getMontoFinal()) {
                Puja puja = new Puja();
                puja.setMonto(montoPuja);
                puja.setFechaPuja(LocalDateTime.now());
                puja.setSubasta(subasta);
                puja.setUsuario(usuarioActual); // Asigna el usuario que está logueado

                servicioPuja.realizarPuja(usuarioActual, subasta, montoPuja);
                System.out.println("Puja realizada exitosamente.");
            } else {
                System.out.println("Monto de puja inválido o subasta no encontrada.");
            }
        } catch (Exception e) {
            System.err.println("Error al realizar la puja: " + e.getMessage());
        }
    }

    // Getters y setters
    public Long getSubastaId() {
        return subastaId;
    }

    public void setSubastaId(Long subastaId) {
        this.subastaId = subastaId;
    }

    public Double getMontoPuja() {
        return montoPuja;
    }

    public void setMontoPuja(Double montoPuja) {
        this.montoPuja = montoPuja;
    }

    public ServicioSubasta getServicioSubasta() {
        return servicioSubasta;
    }

    public void setServicioSubasta(ServicioSubasta servicioSubasta) {
        this.servicioSubasta = servicioSubasta;
    }

    public ServicioPuja getServicioPuja() {
        return servicioPuja;
    }

    public void setServicioPuja(ServicioPuja servicioPuja) {
        this.servicioPuja = servicioPuja;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public void setUsuarioActual(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
    }
 
}
