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

    //SINPE
    private int cuentaSinpeOrigenId;
    private String numeroDestinoSinpe;
    private BigDecimal montoSinpe;
    private String descripcionSinpe;

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

            if (cuenta.getMoneda() != cuenta2.getMoneda()) {
                mostrarMensaje(FacesMessage.SEVERITY_WARN,
                        "Advertencia", "La cuenta destino contiene otro tipo de moneda");
                return;
            }

            if (saldo == null || saldo.compareTo(BigDecimal.ZERO) <= 0) {
                mostrarMensaje(FacesMessage.SEVERITY_WARN,
                        "Advertencia", "Saldo insuficiente");
                return;
            }

            if (montoDeposito.compareTo(saldo) > 0) {
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

    public void realizarSinpe() {
        try {

            // Validaciones básicas
            if (cuentaSinpeOrigenId == 0) {
                mostrarMensaje(FacesMessage.SEVERITY_WARN, "Advertencia", "Debe seleccionar una cuenta origen");
                return;}

            if (numeroDestinoSinpe == null || numeroDestinoSinpe.trim().isEmpty()) {
                mostrarMensaje(FacesMessage.SEVERITY_WARN, "Advertencia", "Debe ingresar el número de teléfono destino");
                return;}

            if (montoSinpe == null || montoSinpe.compareTo(BigDecimal.ZERO) <= 0) {
                mostrarMensaje(FacesMessage.SEVERITY_WARN, "Advertencia", "El monto debe ser mayor a cero");
                return;}

            if (descripcionSinpe == null || descripcionSinpe.trim().isEmpty()) {
                mostrarMensaje(FacesMessage.SEVERITY_WARN, "Advertencia", "Debe ingresar una descripción");
                return;}

            // Validar cuenta origen
            Cuenta cuentaSinpe = cuentaService.buscarCuentaPorId(cuentaSinpeOrigenId);
            BigDecimal saldo = cuentaSinpe.getSaldo();

            if (cuentaSinpe.getEstado() != Cuenta.EstadoCuenta.ACTIVA) {
                mostrarMensaje(FacesMessage.SEVERITY_WARN, "Advertencia", "La cuenta debe estar activa para realizar transferencias");
                return;}

            // Validar saldo suficiente
            if (saldo == null || saldo.compareTo(BigDecimal.ZERO) <= 0) {
                mostrarMensaje(FacesMessage.SEVERITY_WARN,
                        "Advertencia", "Saldo insuficiente");
                return;}
            
            if (montoSinpe.compareTo(saldo) > 0) {
                mostrarMensaje(FacesMessage.SEVERITY_WARN,
                        "Advertencia", "Fondos insufucientes");
                return;
            }

            // Para no permitir transferencia a cuentas dolares
            if (cuentaSinpe.getMoneda() != Cuenta.Moneda.CRC) {
                mostrarMensaje(FacesMessage.SEVERITY_WARN,
                        "Advertencia", "SINPE móvil solo permite transferencias en colones");
                return;
            }

            // Realizar la transferencia SINPE
            boolean transferencia = cuentaService.realizarTransferenciaSinpe(cuentaSinpeOrigenId, montoSinpe);

            if (transferencia) {
                mostrarMensaje(FacesMessage.SEVERITY_INFO, "SINPE exitoso", "Transferencia realizada exitosamente."
                        + " Numero destino: "+ numeroDestinoSinpe
                        + " Monto: " + montoSinpe
                        + " Descripción: " + descripcionSinpe);

                // Refrescar saldos y limpiar formulario
                cargarCuentas();
                limpiarFormularioSinpe();

            } else {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR,
                        "Error", "No se pudo realizar la transferencia SINPE");
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Error del sistema", "Error al realizar SINPE: " + e.getMessage());
        }
    }

    private void limpiarFormularioSinpe() {
        cuentaSinpeOrigenId = 0;
        numeroDestinoSinpe = null;
        montoSinpe = null;
        descripcionSinpe = null;
    }

    // Método de ayuda para mostrar los mensajes correspondientes
    private void mostrarMensaje(FacesMessage.Severity severity, String error, String detallado) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(severity, error, detallado));
    }

    public BigDecimal getMontoDeposito() {
        return montoDeposito;
    }

    public void setMontoDeposito(BigDecimal montoDeposito) {
        this.montoDeposito = montoDeposito;
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

    public int getCuentaSinpeOrigenId() {
        return cuentaSinpeOrigenId;
    }

    public void setCuentaSinpeOrigenId(int cuentaSinpeOrigenId) {
        this.cuentaSinpeOrigenId = cuentaSinpeOrigenId;
    }

    public String getNumeroDestinoSinpe() {
        return numeroDestinoSinpe;
    }

    public void setNumeroDestinoSinpe(String numeroDestinoSinpe) {
        this.numeroDestinoSinpe = numeroDestinoSinpe;
    }

    public BigDecimal getMontoSinpe() {
        return montoSinpe;
    }

    public void setMontoSinpe(BigDecimal montoSinpe) {
        this.montoSinpe = montoSinpe;
    }

    public String getDescripcionSinpe() {
        return descripcionSinpe;
    }

    public void setDescripcionSinpe(String descripcionSinpe) {
        this.descripcionSinpe = descripcionSinpe;
    }

}
