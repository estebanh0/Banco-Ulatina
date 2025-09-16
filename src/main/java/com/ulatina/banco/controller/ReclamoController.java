/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ulatina.banco.controller;

import com.ulatina.banco.model.Reclamo;
import com.ulatina.banco.service.ReclamoService;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author esteban
 * @since 15/9/25
 * 
 */

@Named
@ViewScoped
public class ReclamoController implements Serializable{
    
    private ReclamoService reclamoService = new ReclamoService();
    
    // Datos del formulario
    private String correoElectronico;
    private String descripcion;
    
    // Lista de reclamos del cliente
    private List<Reclamo> reclamosCliente;
    
    private int clienteId = 2;
    
    
    public void crearReclamo() {
        try {
            // Validaciones básicas
            if (correoElectronico == null || correoElectronico.trim().isEmpty()) {
                mostrarMensaje(FacesMessage.SEVERITY_WARN, 
                        "Campo requerido", "Debe ingresar un correo electrónico");
                return;
            }
            
            if (descripcion == null || descripcion.trim().isEmpty()) {
                mostrarMensaje(FacesMessage.SEVERITY_WARN, 
                        "Campo requerido", "Debe describir su reclamo");
                return;
            }           
            
            // Crear el reclamo
            boolean exito = reclamoService.crearReclamo(clienteId, correoElectronico.trim(), descripcion.trim());
            
            if (exito) {
                mostrarMensaje(FacesMessage.SEVERITY_INFO, 
                        "Reclamo registrado", 
                        "Su reclamo ha sido registrado exitosamente. " +
                        "Recibirá una respuesta en el correo proporcionado dentro de las próximas 48 horas.");
                
                limpiarFormulario();
                reclamosCliente = null; // Forzar recarga en el próximo get asi en el get la vera null y cargara el metodo nuevamente
                
            } else {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR, 
                        "Error", "No se pudo registrar el reclamo. Intente nuevamente.");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, 
                    "Error del sistema", 
                    "Error al procesar el reclamo: " + e.getMessage());
        }
    }
    
    private void cargarReclamosCliente() {
        try {
            reclamosCliente = reclamoService.obtenerReclamosPorClienteId(clienteId);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, 
                    "Error al cargar", "No se pudieron cargar los reclamos anteriores.");
        }
    }
    
    private void limpiarFormulario() {
        correoElectronico = null;
        descripcion = null;
    }
    
    
    public List<Reclamo> getReclamosCliente() {
        if (reclamosCliente == null) {
            cargarReclamosCliente();
        }
        return reclamosCliente;
    }
    
    
    
    private void mostrarMensaje(FacesMessage.Severity severity, String error, String detallado) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(severity, error, detallado));
    }
    
    // Getters y Setters
    public String getCorreoElectronico() {return correoElectronico;}   
    public void setCorreoElectronico(String correoElectronico) {this.correoElectronico = correoElectronico;}
    
    public String getDescripcion() {return descripcion;}   
    public void setDescripcion(String descripcion) {this.descripcion = descripcion;}
    
    public void setReclamosCliente(List<Reclamo> reclamosCliente) {this.reclamosCliente = reclamosCliente;}
    
    public int getClienteId() {return clienteId;}   
    public void setClienteId(int clienteId) {this.clienteId = clienteId;}
    
}
