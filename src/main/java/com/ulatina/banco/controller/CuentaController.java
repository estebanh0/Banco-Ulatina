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
import java.math.BigDecimal;
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
    private BigDecimal montoDeposito;
    private int cuentaOrigenId = 3;
    private int cuentaDepositoId = 1;

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

    
    public void realizarDeposito() {

        try {
            
            Cuenta cuenta = cuentaService.buscarCuentaPorId(cuentaOrigenId);
            Cuenta cuenta2 = cuentaService.buscarCuentaPorId(cuentaDepositoId);
            
            BigDecimal saldo = cuenta.getSaldo();
            
            if (montoDeposito == null || montoDeposito.compareTo(BigDecimal.ZERO) <= 0) {
                mostrarMensaje(FacesMessage.SEVERITY_WARN,
                        "Advertencia", "El monto debe ser mayor a cero");
                return;
            }
            

            if (cuenta.getEstado() != Cuenta.EstadoCuenta.ACTIVA) {
                mostrarMensaje(FacesMessage.SEVERITY_WARN,
                        "Advertencia", "La cuenta debe estar activa para realizar depósitos");
                return;
            }
            
            
            if (cuenta.getMoneda() != cuenta2.getMoneda()){
                mostrarMensaje(FacesMessage.SEVERITY_WARN,
                        "Advertencia", "La cuenta destino contiene otro tipo de moneda");
                return;
            }
            
            if (saldo == null || saldo.compareTo(BigDecimal.ZERO) <= 0){
                mostrarMensaje(FacesMessage.SEVERITY_WARN,
                        "Advertencia", "Saldo insuficiente");
                return;
            }
            
            if (montoDeposito.compareTo(saldo) > 0){
                mostrarMensaje(FacesMessage.SEVERITY_WARN,
                        "Advertencia", "Sobrepasa sus fondos actuales");
                return;
            }

            // Realizar el depósito
            boolean exito = cuentaService.realizarTransferencia(cuentaOrigenId, cuentaDepositoId, montoDeposito);
            

            if (exito) {
                mostrarMensaje(FacesMessage.SEVERITY_INFO,
                        "Depósito exitoso", "Se depositaron " + montoDeposito + " con éxito");

                // Refrescar saldos y limpiar el monto
                cargarCuentas();
                montoDeposito = null;
                
            } else {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR,
                        "Error", "No se pudo realizar el depósito");
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje(FacesMessage.SEVERITY_ERROR,
                    "Error del sistema", "Error al realizar el depósito: " + e.getMessage());
        }
    }

    // Método de ayuda para mostrar los mensajes correspondientes
    private void mostrarMensaje(FacesMessage.Severity severity, String error, String detallado) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(severity, error, detallado));
    }

    
    
    public BigDecimal getMontoDeposito() {return montoDeposito;}

    public void setMontoDeposito(BigDecimal montoDeposito) {this.montoDeposito = montoDeposito;}

    
    public List<Cuenta> getCuentas() {
        if (cuentas == null) {
            cargarCuentas();
        }
        return cuentas;
    }

    public void setCuentas(List<Cuenta> cuentas) {
        this.cuentas = cuentas;
    }

    public int getClienteId() {return clienteId;}

    public void setClienteId(int clienteId) {this.clienteId = clienteId;}

}
