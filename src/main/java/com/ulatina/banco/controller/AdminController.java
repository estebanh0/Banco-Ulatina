/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ulatina.banco.controller;

import com.ulatina.banco.model.Reversa;
import com.ulatina.banco.service.CuentaService;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author esteban
 * @since 14/9/25
 *
 * - Aprobar o rechazar reversas
 */
@Named
@ViewScoped
public class AdminController implements Serializable {

    private List<Reversa> reversasPendientes;
    private CuentaService cuentaService = new CuentaService();

    public void cargarReversasPendientes() {
        try {
            //Llamada al metodo para cargar las reversas en estado pendiente en la lista creada.
            reversasPendientes = cuentaService.obtenerReversasPendientes();

            if (reversasPendientes.isEmpty()) {
                mostrarMensaje(FacesMessage.SEVERITY_INFO,
                        "Sin solicitudes", "No hay solicitudes de reversa en estado pendiente.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje(FacesMessage.SEVERITY_ERROR,
                    "Error del sistema", "No se pudieron cargar las solicitudes de reversa.");
        }
    }

    public void aprobarReversa(int reversaId) {
        try {
            boolean exito = cuentaService.aprobarReversa(reversaId);

            if (exito) {
                mostrarMensaje(FacesMessage.SEVERITY_INFO,
                        "Reversa aprobada", "La solicitud de reversa ha sido aprobada.");
                cargarReversasPendientes();
            } else {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR,
                        "Error", "No se pudo aprobar la reversa.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje(FacesMessage.SEVERITY_ERROR,
                    "Error del sistema", "Error al aprobar la reversa: " + e.getMessage());
        }
    }

    public void rechazarReversa(int reversaId) {
        try {
            boolean exito = cuentaService.rechazarReversa(reversaId);

            if (exito) {
                mostrarMensaje(FacesMessage.SEVERITY_INFO,
                        "Reversa rechazada", "La solicitud de reversa ha sido rechazada.");
                cargarReversasPendientes();
            } else {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR,
                        "Error", "No se pudo rechazar la reversa.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje(FacesMessage.SEVERITY_ERROR,
                    "Error del sistema", "Error al rechazar la reversa: " + e.getMessage());
        }
    }

    private void mostrarMensaje(FacesMessage.Severity severity, String error, String detallado) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(severity, error, detallado));
    }

    public List<Reversa> getReversasPendientes() {
        if (reversasPendientes == null){
            cargarReversasPendientes();
        }return reversasPendientes;}

    public void setReversasPendientes(List<Reversa> reversasPendientes) {this.reversasPendientes = reversasPendientes;}

}
