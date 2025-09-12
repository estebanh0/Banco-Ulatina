/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ulatina.banco.controller;

import com.ulatina.banco.model.Cuenta;
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
 * @since 12/9/25
 *
 */
@Named
@ViewScoped
public class CuentaController implements Serializable {

    private List<Cuenta> cuentas;
    private CuentaService cuentaService = new CuentaService();
    private int clienteId = 2;

    public void cargarCuentas() {
        try {
            cuentas = cuentaService.obtenerCuentasPorClienteId(clienteId);

            if (cuentas.isEmpty()) {
                mostrarMensaje(FacesMessage.SEVERITY_INFO,
                        "Sin cuentas", "Este cliente no tiene cuentas registradas.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje(FacesMessage.SEVERITY_ERROR,
                    "Error del sistema", "No se pudieron cargar las cuentas.");
        }
    }

    // Método de ayuda para mostrar los mensajes correspondientes
    private void mostrarMensaje(FacesMessage.Severity severity, String error, String detallado) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(severity, error, detallado));
    }

    public List<Cuenta> getCuentas() {
        if (cuentas == null) {
            cargarCuentas();
        }
        return cuentas;
    }

    public void setCuentas(List<Cuenta> cuentas) {
        this.cuentas = cuentas;
    }

    public int getClienteId() {
        return clienteId;
    }

    public void setClienteId(int clienteId) {
        this.clienteId = clienteId;
    }

}
