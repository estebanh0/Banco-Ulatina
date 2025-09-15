/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ulatina.banco.controller;

import com.ulatina.banco.model.Cliente;
import com.ulatina.banco.service.ClienteService;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;

/**
 *
 * @author esteban
 * @since 11/9/25
 * - Valida el usuario con nombre y cedula
 * - Valida el embargo del usuario
 * - Control de errores
 */

@Named
@ViewScoped

public class ClienteController implements Serializable{
    private String cedula;
    private String nombre;
    private ClienteService clienteService = new ClienteService();
   
    public String validar() {
        try {
            
            // Buscar cliente en la base de datos
            Cliente cliente = clienteService.buscarClientePorCedulaYNombre(cedula, nombre);
            
            if (cliente == null) {
                mostrarMensaje(FacesMessage.SEVERITY_WARN, 
                    "Cliente no encontrado", "Verifique cédula y nombre.");
                return null;
            }
            
            // Validar embargo
            if (cliente.isEmbargo()) {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR, 
                    "Acceso denegado", "El cliente tiene embargo.");
                return null;
            }
            
            //admin
            if (cliente.getNombre().equals("admin") && cliente.getCedula().equals("123")){
                clienteService.redireccionar("/admin.xhtml");
            }
            
            // Cliente válido - redirigir a cuentas
            clienteService.redireccionar("/cuenta.xhtml");
            return null;
            
        } catch (NumberFormatException e) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, 
                "Cédula inválida", "Formato de cédula incorrecto.");
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje(FacesMessage.SEVERITY_FATAL, 
                "Error del sistema", "Error interno. Contacte al administrador.");
            return null;
        }
    }
    
    

    // Método de ayuda para mostrar los mensajes correspondientes
    private void mostrarMensaje(FacesMessage.Severity severity, String error, String detallado) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(severity, error, detallado));
    }
    

    public String getCedula() {return cedula;}
    public void setCedula(String cedula) {this.cedula = cedula;}
    
    public String getNombre() {return nombre;}
    public void setNombre(String nombre) {this.nombre = nombre;}
    
    public ClienteService getClienteService() {return clienteService;}
    public void setClienteService(ClienteService clienteService) {this.clienteService = clienteService;}
    
}
