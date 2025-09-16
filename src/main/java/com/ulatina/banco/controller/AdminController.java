/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ulatina.banco.controller;

import com.ulatina.banco.model.Cliente;
import com.ulatina.banco.model.EstadoFinanciero;
import com.ulatina.banco.model.ReporteGenerado;
import com.ulatina.banco.model.Reversa;
import com.ulatina.banco.service.ClienteService;
import com.ulatina.banco.service.CuentaService;
import com.ulatina.banco.service.ReporteService;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author esteban
 * @since 14/9/25
 *
 * - Aprobar o rechazar reversas
 * - Generar estados financieros y reportes para SUGEF
 */
@Named
@ViewScoped
public class AdminController implements Serializable {

    private List<Cliente> clientes;
    private List<Reversa> reversasPendientes;
    private List<ReporteGenerado> reportesGenerados;
    
    private CuentaService cuentaService = new CuentaService();
    private ClienteService clienteService = new ClienteService();
    private ReporteService reporteService = new ReporteService();
    
    private int clienteSeleccionadoId;
    private static final String DIRECTORIO_REPORTES
            = System.getProperty("user.home") + File.separator + "BancoReportes" + File.separator; //se usa de esta manera para tener compatiblidad entre OS

    
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

    // Obtener todos los clientes
    public void cargarClientes() {
        try {
            clientes = clienteService.obtenerTodosLosClientes();
            
            if (clientes.isEmpty()) {
                mostrarMensaje(FacesMessage.SEVERITY_INFO,
                        "Sin clientes", "No hay clientes registrados en el sistema.");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje(FacesMessage.SEVERITY_ERROR,
                    "Error del sistema", "No se pudieron cargar los clientes.");
        }
    }

    // NUEVO: Método para generar estado financiero
    public void generarEstadoFinanciero() {
        try {
            if (clienteSeleccionadoId == 0) {
                mostrarMensaje(FacesMessage.SEVERITY_WARN,
                        "Advertencia", "Debe seleccionar un cliente.");
                return;
            }

            // Crear directorio si no existe
            File directorio = new File(DIRECTORIO_REPORTES);
            if (!directorio.exists()) {
                directorio.mkdirs();
            }

            // Generar estado financiero
            EstadoFinanciero estado = reporteService.generarEstadoFinanciero(clienteSeleccionadoId);
            
            // Crear objeto reporte para guardar en BD
            ReporteGenerado reporte = new ReporteGenerado(
                estado.getCliente().getId(),
                estado.getCliente().getNombre(),
                estado.getCliente().getCedula(),
                "ESTADO_FINANCIERO",
                "GENERADO"
            );

            // Generar nombres únicos para archivos
            String nombreBase = "estado_financiero_" + clienteSeleccionadoId + "_" + System.currentTimeMillis();
            String rutaXML = DIRECTORIO_REPORTES + nombreBase + ".xml";

            // Exportar archivos
            reporteService.exportarXML(estado, rutaXML);

            // Actualizar rutas en el reporte
            reporte.setRutaXML(rutaXML);
            reporte.setTotalActivos(estado.getTotalActivosConsolidado());
            reporte.setPatrimonioNeto(estado.getPatrimonioNetoConsolidado());

            // Guardar en base de datos
            int reporteId = reporteService.guardarReporte(reporte);

            if (reporteId > 0) {
                mostrarMensaje(FacesMessage.SEVERITY_INFO,
                        "Reporte generado exitosamente",
                        String.format("Se ha generado el estado financiero para %s. ID del reporte: %d", 
                                      estado.getCliente().getNombre(), reporteId));
                
                // Recargar lista de reportes generados
                cargarReportesGenerados();
                
                // Limpiar selección
                clienteSeleccionadoId = 0;
            } else {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR,
                        "Error", "No se pudo guardar el reporte en la base de datos.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje(FacesMessage.SEVERITY_ERROR,
                    "Error del sistema", "Error al generar el estado financiero: " + e.getMessage());
        }
    }

    public void cargarReportesGenerados() {
        try {
            reportesGenerados = reporteService.obtenerReportesGenerados();
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje(FacesMessage.SEVERITY_ERROR,
                    "Error del sistema", "No se pudieron cargar los reportes generados.");
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
            cargarReversasPendientes(); }return reversasPendientes;}
    public void setReversasPendientes(List<Reversa> reversasPendientes) {this.reversasPendientes = reversasPendientes;}

    public List<Cliente> getClientes() {
        if (clientes == null) {
            cargarClientes(); }return clientes;}
    public void setClientes(List<Cliente> clientes) { this.clientes = clientes; }

    public int getClienteSeleccionadoId() { return clienteSeleccionadoId; }
    public void setClienteSeleccionadoId(int clienteSeleccionadoId) { this.clienteSeleccionadoId = clienteSeleccionadoId; }

    public List<ReporteGenerado> getReportesGenerados() {
        if (reportesGenerados == null) {
            cargarReportesGenerados(); }return reportesGenerados;}
    public void setReportesGenerados(List<ReporteGenerado> reportesGenerados) { this.reportesGenerados = reportesGenerados; }
}
