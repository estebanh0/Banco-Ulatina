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
 * - Implementación de monitoreo de montos mayores o iguales a 10.000.000
 *
 */
@Named
@ViewScoped
public class CuentaController implements Serializable {
    
    private SugefController sugefController = new SugefController();

    //Cuentas
    private List<Cuenta> cuentas;
    private CuentaService cuentaService = new CuentaService();
    private int clienteId = 2;
    private BigDecimal montoDeposito;
    private int cuentaOrigenId;
    private int cuentaDepositoId;
    
    // NUEVO: Variables para límite de transferencia
    private BigDecimal limiteTransferencia = new BigDecimal("250000"); // Límite por defecto ₡250,000
    private boolean mostrarConfirmacionTransferencia = false;
    private String mensajeConfirmacion = "";
    
    //SINPE
    private int cuentaSinpeOrigenId;
    private String numeroDestinoSinpe;
    private BigDecimal montoSinpe;
    private String descripcionSinpe;
    
    //Reversas
    private int cuentaReversaId;
    private String descripcionReversa;
    private BigDecimal montoReversa;
    
    
    //TRANSFERENCIA INTERNACIONAL
    private int cuentaSwiftOrigenId;
    private String codigoSwift;
    private String beneficiario;
    private String paisDestino;
    private BigDecimal montoSwift;
    
    //Ahorros con intereses
    private BigDecimal montoAhorro;
    
    
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
            
            // Verificar si supera el límite
            if (montoDeposito.compareTo(limiteTransferencia) > 0) {
                // SOLO mostrar confirmación, NO ejecutar transferencia
                mensajeConfirmacion = String.format(
                        "El monto de la transferencia supera el límite establecido "
                        + "(₡%,.2f). ¿Desea continuar con la operación?", limiteTransferencia
                );
                mostrarConfirmacionTransferencia = true;
                return; // IMPORTANTE: Salir aquí para esperar confirmación
            }

            // Si NO supera el límite, ejecutar transferencia normalmente
            boolean exito = cuentaService.realizarTransferencia(cuentaOrigenId, cuentaDepositoId, montoDeposito);

            if (exito) {
                // Monitorear el limite de la transaccion para reportes de SUGEF
                sugefController.monitorearTransaccion(clienteId, cuentaOrigenId, "TRANSFERENCIA_INTERNA", montoDeposito);

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
    
    // Método para confirmar transferencia que supera el límite
    public void confirmarTransferencia() {
        try {
            boolean exito = cuentaService.realizarTransferencia(cuentaOrigenId, cuentaDepositoId, montoDeposito);

            if (exito) {
                String mensaje = String.format("Se transfirieron ₡%,.2f con éxito", montoDeposito);
                mostrarMensaje(FacesMessage.SEVERITY_INFO, "Transferencia exitosa", mensaje);
                cargarCuentas();
                montoDeposito = null;
            } else {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo realizar la transferencia");
            }
            
            limpiarConfirmacion();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Error del sistema", "Error al confirmar la transferencia: " + e.getMessage());
        }
    }
    
    public void rechazarTransferencia() {
        mostrarMensaje(FacesMessage.SEVERITY_INFO, "Transferencia cancelada", "La operación ha sido cancelada por el usuario");
        limpiarConfirmacion();
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
                
                //Monitoreo de transferencia - SUGEF
                sugefController.monitorearTransaccion(clienteId, cuentaSinpeOrigenId, "SINPE", montoSinpe);

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
    
    public void solicitarReversa() {
        try {
            // Validaciones básicas
            if (cuentaReversaId == 0) {
                mostrarMensaje(FacesMessage.SEVERITY_WARN, "Advertencia", "Debe seleccionar una cuenta");
                return;
            }

            if (descripcionReversa == null || descripcionReversa.trim().isEmpty()) {
                mostrarMensaje(FacesMessage.SEVERITY_WARN, "Advertencia", "Debe ingresar la descripción del motivo");
                return;
            }

            if (montoReversa == null || montoReversa.compareTo(BigDecimal.ZERO) <= 0) {
                mostrarMensaje(FacesMessage.SEVERITY_WARN, "Advertencia", "El monto debe ser mayor a cero");
                return;
            }

            // Crear solicitud de reversa
            boolean exito = cuentaService.crearSolicitudReversa(clienteId, cuentaReversaId, descripcionReversa, montoReversa);

            if (exito) {
                mostrarMensaje(FacesMessage.SEVERITY_INFO, "Solicitud enviada", "Su solicitud de reversa ha sido enviada al administrador para revisión");
                limpiarFormularioReversa();
            } else {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo enviar la solicitud de reversa");
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Error del sistema", "Error al solicitar reversa: " + e.getMessage());
        }
    }
    
    public void incrementoSaldoDirecto(int id) {
        try {
            // Buscar la cuenta específica
            for (Cuenta cuenta : cuentas) {
                if (cuenta.getId()== id) {

                    // Verificar que la cuenta esté activa
                    if (cuenta.getEstado() != Cuenta.EstadoCuenta.ACTIVA) {
                        mostrarMensaje(FacesMessage.SEVERITY_WARN,
                                "Advertencia", "Solo se puede incrementar el saldo de cuentas activas");
                        return;
                    }

                    // Obtener el saldo actual
                    BigDecimal saldoActual = cuenta.getSaldo();

                    // Incrementar 1000 al saldo actual
                    BigDecimal nuevoSaldo = saldoActual.add(new BigDecimal("1000"));
                    cuenta.setSaldo(nuevoSaldo);
                    return;
                }
            }
        } catch (Exception e) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR,
                    "Error del sistema", "Error al incrementar el saldo: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void realizarTransferenciaInternacional() {
        try {
            // Validaciones básicas
            if (cuentaSwiftOrigenId == 0) {
                mostrarMensaje(FacesMessage.SEVERITY_WARN, "Advertencia", "Debe seleccionar una cuenta origen");
                return;
            }

            if (codigoSwift == null || codigoSwift.trim().isEmpty()) {
                mostrarMensaje(FacesMessage.SEVERITY_WARN, "Advertencia", "Debe ingresar el código SWIFT del banco destino");
                return;
            }

            if (beneficiario == null || beneficiario.trim().isEmpty()) {
                mostrarMensaje(FacesMessage.SEVERITY_WARN, "Advertencia", "Debe ingresar el nombre del beneficiario");
                return;
            }

            if (paisDestino == null || paisDestino.trim().isEmpty()) {
                mostrarMensaje(FacesMessage.SEVERITY_WARN, "Advertencia", "Debe ingresar el país destino");
                return;
            }

            if (montoSwift == null || montoSwift.compareTo(BigDecimal.ZERO) <= 0) {
                mostrarMensaje(FacesMessage.SEVERITY_WARN, "Advertencia", "El monto debe ser mayor a cero");
                return;
            }
            
            // Validar cuenta origen
            Cuenta cuentaOrigen = cuentaService.buscarCuentaPorId(cuentaSwiftOrigenId);
            
            if (cuentaOrigen.getEstado() != Cuenta.EstadoCuenta.ACTIVA) {
                mostrarMensaje(FacesMessage.SEVERITY_WARN, "Advertencia", "La cuenta debe estar activa para realizar transferencias");
                return;
            }
            
            // Solo permite USD
            if (cuentaOrigen.getMoneda() == Cuenta.Moneda.CRC) {
                mostrarMensaje(FacesMessage.SEVERITY_WARN, "Advertencia", "Las transferencias internacionales solo se permiten en USD");
                return;
            }

            // Calcular tarifa (5% del monto como ejemplo)
            BigDecimal tarifa = montoSwift.multiply(new BigDecimal("0.05"));
            BigDecimal montoTotal = montoSwift.add(tarifa);
            
            if (cuentaOrigen.getSaldo().compareTo(montoTotal) < 0) {
                mostrarMensaje(FacesMessage.SEVERITY_WARN, "Advertencia", "Fondos insuficientes (monto + tarifa: " + tarifa + ")");
                return;
            }

            // Realizar transferencia internacional (rebaja con cuenta destino ficticia)
            boolean exito = cuentaService.realizarTransferenciaInternacional(cuentaSwiftOrigenId, montoSwift, tarifa, codigoSwift, beneficiario, paisDestino);

            if (exito) {
                
                sugefController.monitorearTransaccion(clienteId, cuentaSwiftOrigenId, "TRANSFERENCIA_INTERNACIONAL", montoSwift);
                
                mostrarMensaje(FacesMessage.SEVERITY_INFO, "Transferencia internacional enviada",
                        "Transferencia enviada exitosamente. "
                        + "Beneficiario: " + beneficiario
                        + ", País: " + paisDestino
                        + ", SWIFT: " + codigoSwift
                        + ", Monto: " + montoSwift
                        + ", Tarifa: " + tarifa);

                // Refrescar saldos y limpiar formulario
                cargarCuentas();
                limpiarFormularioSwift();

            } else {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo procesar la transferencia internacional");
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Error del sistema", "Error al realizar transferencia internacional: " + e.getMessage());
        }
    }
  
    public BigDecimal getInteresesGenerados() {
        if (montoAhorro == null || montoAhorro.compareTo(BigDecimal.ZERO) <= 0) {
            mostrarMensaje(FacesMessage.SEVERITY_WARN, "Advertencia", "El monto debe ser mayor a cero");
            return BigDecimal.ZERO;
        }

        // 1.5% de interés mensual
        BigDecimal tasaInteres = new BigDecimal("0.015");
        
        
        //.setScale(2,roundhalf) -> basicamente es un redondea a dos decimales
        return montoAhorro.multiply(tasaInteres).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    // Calcula el impuesto sobre los intereses (15%)
    public BigDecimal getImpuestoRetenido() {
        BigDecimal intereses = getInteresesGenerados();
        if (intereses.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        // 15% de impuesto sobre intereses
        BigDecimal tasaImpuesto = new BigDecimal("0.15");
        //.setScale(2,roundhalf) -> basicamente es un redondea a dos decimales
        return intereses.multiply(tasaImpuesto).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    // Calcula los intereses netos (intereses - impuestos)
    public BigDecimal getInteresesNetos() {
        BigDecimal intereses = getInteresesGenerados();
        BigDecimal impuesto = getImpuestoRetenido();
        //.substract lo utilizamos para restar los intereses el impuesto genero e igual redondeado a dos decimales para obtener el monto neto
        return intereses.subtract(impuesto).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    // Calcula el monto final (monto inicial + intereses netos)
    public BigDecimal getMontoFinal() {
        if (montoAhorro == null || montoAhorro.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal interesesNetos = getInteresesNetos();
        return montoAhorro.add(interesesNetos).setScale(2, BigDecimal.ROUND_HALF_UP);
    }
    
    public void aplicarLimite() {
        try {
            if (limiteTransferencia == null || limiteTransferencia.compareTo(BigDecimal.ZERO) <= 0) {
                mostrarMensaje(FacesMessage.SEVERITY_WARN,
                        "Límite inválido", "El límite debe ser mayor a cero");
                limiteTransferencia = new BigDecimal("250000"); // Resetear al valor por defecto
                return;
            }

            if (limiteTransferencia.compareTo(new BigDecimal("15000000")) > 0) {
                mostrarMensaje(FacesMessage.SEVERITY_WARN,
                        "Límite muy alto", "El límite no puede ser mayor a ₡15,000,000");
                limiteTransferencia = new BigDecimal("15000000"); // Limitar al máximo
                return;
            }

            mostrarMensaje(FacesMessage.SEVERITY_INFO,
                    "Límite actualizado",
                    String.format("El nuevo límite de transferencia es ₡%,.2f", limiteTransferencia));

        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje(FacesMessage.SEVERITY_ERROR,
                    "Error", "No se pudo aplicar el límite: " + e.getMessage());
        }
    }
    
    
    public void bloquearCuentaBancaria(int cuentaId) {
        try {
            // Buscar cuenta especifica
            boolean cuentaValida = false;
            
            for (Cuenta cuenta : cuentas) {
                if (cuenta.getId() == cuentaId && cuenta.getEstado() == Cuenta.EstadoCuenta.ACTIVA) {
                    cuentaValida = true;
                    break;
                }
            }

            if (!cuentaValida) {
                mostrarMensaje(FacesMessage.SEVERITY_WARN,
                        "Advertencia", "Solo se pueden bloquear cuentas activas propias");
                return;
            }

            // Bloquear la cuenta
            boolean exito = cuentaService.bloquearCuenta(cuentaId);

            if (exito) {
                mostrarMensaje(FacesMessage.SEVERITY_INFO,
                        "Cuenta bloqueada", "Su cuenta ha sido bloqueada exitosamente. "
                        + "Para desbloquearla, contacte al banco o visite una sucursal.");

                // Refrescar lista de cuentas para mostrar el nuevo estado
                cargarCuentas();
            } else {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR,
                        "Error", "No se pudo bloquear la cuenta. Intente nuevamente.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje(FacesMessage.SEVERITY_ERROR,
                    "Error del sistema", "Error al bloquear la cuenta: " + e.getMessage());
        }
    }

    private void limpiarFormularioSwift() {
        cuentaSwiftOrigenId = 0;
        codigoSwift = null;
        beneficiario = null;
        paisDestino = null;
        montoSwift = null;
    }
  
    private void limpiarFormularioSinpe() {
        cuentaSinpeOrigenId = 0;
        numeroDestinoSinpe = null;
        montoSinpe = null;
        descripcionSinpe = null;
    }
    
    private void limpiarFormularioReversa() {
        cuentaReversaId = 0;
        descripcionReversa = null;
        montoReversa = null;
    }
    
    private void limpiarConfirmacion() {
        mostrarConfirmacionTransferencia = false;
        mensajeConfirmacion = "";
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
    
    public BigDecimal getLimiteTransferencia() { return limiteTransferencia; }

    public void setLimiteTransferencia(BigDecimal limiteTransferencia) { this.limiteTransferencia = limiteTransferencia; }

    public boolean isMostrarConfirmacionTransferencia() { return mostrarConfirmacionTransferencia; }

    public void setMostrarConfirmacionTransferencia(boolean mostrarConfirmacionTransferencia) { this.mostrarConfirmacionTransferencia = mostrarConfirmacionTransferencia; }

    public String getMensajeConfirmacion() { return mensajeConfirmacion; }

    public void setMensajeConfirmacion(String mensajeConfirmacion) { this.mensajeConfirmacion = mensajeConfirmacion; }

    public BigDecimal getMontoAhorro() {return montoAhorro;}
    public void setMontoAhorro(BigDecimal montoAhorro) {this.montoAhorro = montoAhorro;}
    
    public int getCuentaSwiftOrigenId() {return cuentaSwiftOrigenId;}
    public void setCuentaSwiftOrigenId(int cuentaSwiftOrigenId) {this.cuentaSwiftOrigenId = cuentaSwiftOrigenId;}

    public String getCodigoSwift() {return codigoSwift;}
    public void setCodigoSwift(String codigoSwift) {this.codigoSwift = codigoSwift;}

    public String getBeneficiario() {return beneficiario;}
    public void setBeneficiario(String beneficiario) {this.beneficiario = beneficiario;}

    public String getPaisDestino() {return paisDestino;}
    public void setPaisDestino(String paisDestino) {this.paisDestino = paisDestino;}

    public BigDecimal getMontoSwift() {return montoSwift;}
    public void setMontoSwift(BigDecimal montoSwift) {this.montoSwift = montoSwift;}

    public int getCuentaReversaId() {return cuentaReversaId;}
    public void setCuentaReversaId(int cuentaReversaId) {this.cuentaReversaId = cuentaReversaId;}

    public String getDescripcionReversa() {return descripcionReversa;}
    public void setDescripcionReversa(String descripcionReversa) {this.descripcionReversa = descripcionReversa;}

    public BigDecimal getMontoReversa() {return montoReversa;}
    public void setMontoReversa(BigDecimal montoReversa) {this.montoReversa = montoReversa;}
    
    public BigDecimal getMontoDeposito() {return montoDeposito;}
    public void setMontoDeposito(BigDecimal montoDeposito) {this.montoDeposito = montoDeposito;}
    
    public int getCuentaOrigenId() {return cuentaOrigenId;}
    public void setCuentaOrigenId(int cuentaOrigenId) {this.cuentaOrigenId = cuentaOrigenId;}

    public int getCuentaDepositoId() {return cuentaDepositoId;}
    public void setCuentaDepositoId(int cuentaDepositoId) {this.cuentaDepositoId = cuentaDepositoId;}
    
    public void setCuentas(List<Cuenta> cuentas) {this.cuentas = cuentas;}

    public int getClienteId() {return clienteId;}
    public void setClienteId(int clienteId) {this.clienteId = clienteId;}

    public int getCuentaSinpeOrigenId() {return cuentaSinpeOrigenId;}
    public void setCuentaSinpeOrigenId(int cuentaSinpeOrigenId) {this.cuentaSinpeOrigenId = cuentaSinpeOrigenId;}

    public String getNumeroDestinoSinpe() {return numeroDestinoSinpe;}
    public void setNumeroDestinoSinpe(String numeroDestinoSinpe) {this.numeroDestinoSinpe = numeroDestinoSinpe;}

    public BigDecimal getMontoSinpe() {return montoSinpe;}
    public void setMontoSinpe(BigDecimal montoSinpe) {this.montoSinpe = montoSinpe;}

    public String getDescripcionSinpe() {return descripcionSinpe;}
    public void setDescripcionSinpe(String descripcionSinpe) {this.descripcionSinpe = descripcionSinpe;}

}
