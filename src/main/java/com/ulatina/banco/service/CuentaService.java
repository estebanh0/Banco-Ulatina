/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ulatina.banco.service;

import com.ulatina.banco.controller.SugefController;
import com.ulatina.banco.model.Cuenta;
import com.ulatina.banco.model.Cuenta.Moneda;
import com.ulatina.banco.model.Cuenta.EstadoCuenta;
import com.ulatina.banco.model.Reversa;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author esteban
 * @since 12/9/25
 *
 * - Consultas SQL con sus excepsiones
 *
 * - Obtenemos todas las cuentas que tenga el cliente mediante su id 
 * - "BuscarCuentaPorId" es un método que de momento no se utilizará pero para versiones posteriores si 
 * - Metodos correspondientes para transferencias tanto sinpe como entre cuentas bancarias 
 * - Funcionalidad de reversas
 */
public class CuentaService extends Service {
    
    public List<Cuenta> obtenerCuentasPorClienteId(int clienteId) 
            throws SQLException, ClassNotFoundException {

        List<Cuenta> cuentas = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            conectarBD();
            String sql = "SELECT id, numero_cuenta, cliente_id, moneda, estado, saldo "
                    + "FROM cuenta WHERE cliente_id = ?";
            ps = conexion.prepareStatement(sql);
            ps.setInt(1, clienteId);
            rs = ps.executeQuery();

            while (rs.next()) {
                Cuenta cuenta = new Cuenta(
                        rs.getInt("id"),
                        rs.getString("numero_cuenta"),
                        rs.getInt("cliente_id"),
                        Moneda.valueOf(rs.getString("moneda")),
                        EstadoCuenta.valueOf(rs.getString("estado")),
                        rs.getBigDecimal("saldo")
                );
                cuentas.add(cuenta);
            }

        } finally {
            cerrarResultSet(rs);
            cerrarPreparedStatement(ps);
            cerrarConexion();
        }

        return cuentas;
    }

    public Cuenta buscarCuentaPorId(int id) 
            throws SQLException, ClassNotFoundException {

        PreparedStatement ps = null;
        ResultSet rs = null;
        Cuenta cuenta = null;

        try {
            conectarBD();
            String sql = "SELECT id, numero_cuenta, cliente_id, moneda, estado, saldo "
                    + "FROM cuenta WHERE id = ?";
            ps = conexion.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                cuenta = new Cuenta(
                        rs.getInt("id"),
                        rs.getString("numero_cuenta"),
                        rs.getInt("cliente_id"),
                        Moneda.valueOf(rs.getString("moneda")),
                        EstadoCuenta.valueOf(rs.getString("estado")),
                        rs.getBigDecimal("saldo")
                );
            }

        } finally {
            cerrarResultSet(rs);
            cerrarPreparedStatement(ps);
            cerrarConexion();
        }

        return cuenta;
    }

    public boolean realizarTransferencia(int cuentaOrigenId, int cuentaDestinoId, BigDecimal monto) 
            throws SQLException, ClassNotFoundException {

        PreparedStatement ps = null;

        try {
            conectarBD();

            // Restar de cuenta origen
            String sqlOrigen = "UPDATE cuenta SET saldo = saldo - ? WHERE id = ?";
            ps = conexion.prepareStatement(sqlOrigen);
            ps.setBigDecimal(1, monto);
            ps.setInt(2, cuentaOrigenId);
            ps.executeUpdate();
            cerrarPreparedStatement(ps);

            // Sumar a cuenta destino
            String sqlDestino = "UPDATE cuenta SET saldo = saldo + ? WHERE id = ?";
            ps = conexion.prepareStatement(sqlDestino);
            ps.setBigDecimal(1, monto);
            ps.setInt(2, cuentaDestinoId);
            int resultado = ps.executeUpdate();

            return resultado > 0;

        } finally {
            cerrarPreparedStatement(ps);
            cerrarConexion();
        }
    }

    public boolean realizarTransferenciaSinpe(int cuentaOrigenId, BigDecimal monto) 
            throws SQLException, ClassNotFoundException {

        PreparedStatement ps = null;

        try {
            conectarBD();

            // Solo restar de cuenta origen (transferencia externa)
            String sqlOrigen = "UPDATE cuenta SET saldo = saldo - ? WHERE id = ?";
            ps = conexion.prepareStatement(sqlOrigen);
            ps.setBigDecimal(1, monto);
            ps.setInt(2, cuentaOrigenId);
            int resultado = ps.executeUpdate();

            return resultado > 0;

        } finally {
            cerrarPreparedStatement(ps);
            cerrarConexion();
        }
    }

    public boolean crearSolicitudReversa(int clienteId, int cuentaId, String descripcion, BigDecimal monto) 
            throws SQLException, ClassNotFoundException {

        PreparedStatement ps = null;

        try {
            conectarBD();
            String sql = "INSERT INTO reversa (cliente_id, cuenta_id, descripcion, monto, estado) VALUES (?, ?, ?, ?, 'PENDIENTE')";
            ps = conexion.prepareStatement(sql);
            ps.setInt(1, clienteId);
            ps.setInt(2, cuentaId);
            ps.setString(3, descripcion);
            ps.setBigDecimal(4, monto);
            int resultado = ps.executeUpdate();

            return resultado > 0;

        } finally {
            cerrarPreparedStatement(ps);
            cerrarConexion();
        }
    }

    public List<Reversa> obtenerReversasPendientes()
            throws SQLException, ClassNotFoundException {

        List<Reversa> reversas = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conectarBD();
            String sql = "SELECT id, cliente_id, cuenta_id, descripcion, monto, estado FROM reversa WHERE estado = 'PENDIENTE'";
            ps = conexion.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Reversa reversa = new Reversa(
                        rs.getInt("cliente_id"),
                        rs.getInt("cuenta_id"),
                        rs.getString("descripcion"),
                        rs.getBigDecimal("monto"),
                        rs.getString("estado")
                );
                reversa.setId(rs.getInt("id"));
                reversas.add(reversa);
            }

        } finally {
            cerrarResultSet(rs);
            cerrarPreparedStatement(ps);
            cerrarConexion();
        }

        return reversas;
    }

    public boolean aprobarReversa(int reversaId)
            throws SQLException, ClassNotFoundException {

        PreparedStatement ps = null;

        try {
            conectarBD();
            String sql = "UPDATE reversa SET estado = 'APROBADA' WHERE id = ?";
            ps = conexion.prepareStatement(sql);
            ps.setInt(1, reversaId);
            int resultado = ps.executeUpdate();

            return resultado > 0;

        } finally {
            cerrarPreparedStatement(ps);
            cerrarConexion();
        }
    }

    public boolean rechazarReversa(int reversaId)
            throws SQLException, ClassNotFoundException {

        PreparedStatement ps = null;

        try {
            conectarBD();
            String sql = "UPDATE reversa SET estado = 'RECHAZADA' WHERE id = ?";
            ps = conexion.prepareStatement(sql);
            ps.setInt(1, reversaId);
            int resultado = ps.executeUpdate();

            return resultado > 0;

        } finally {
            cerrarPreparedStatement(ps);
            cerrarConexion();
        }
    }
    
    public boolean realizarTransferenciaInternacional(int cuentaOrigenId, BigDecimal monto, BigDecimal tarifa,
                String codigoSwift, String beneficiario, String paisDestino)
            throws SQLException, ClassNotFoundException {

        PreparedStatement ps = null;

        try {
            conectarBD();

            // Restar monto + tarifa de cuenta origen
            BigDecimal montoTotal = monto.add(tarifa);
            String sqlOrigen = "UPDATE cuenta SET saldo = saldo - ? WHERE id = ?";
            ps = conexion.prepareStatement(sqlOrigen);
            ps.setBigDecimal(1, montoTotal);
            ps.setInt(2, cuentaOrigenId);
            int resultado = ps.executeUpdate();
            cerrarPreparedStatement(ps);

            // Registrar la transferencia internacional para tracking
            if (resultado > 0) {
                String sqlRegistro = "INSERT INTO transferencia_internacional (cuenta_id, monto, tarifa, "
                        + "codigo_swift, beneficiario, pais_destino, estado) VALUES (?, ?, ?, ?, ?, ?, 'ENVIADO')";
                ps = conexion.prepareStatement(sqlRegistro);
                ps.setInt(1, cuentaOrigenId);
                ps.setBigDecimal(2, monto);
                ps.setBigDecimal(3, tarifa);
                ps.setString(4, codigoSwift);
                ps.setString(5, beneficiario);
                ps.setString(6, paisDestino);
                ps.executeUpdate();
            }

            return resultado > 0;

        } finally {
            cerrarPreparedStatement(ps);
            cerrarConexion();
        }
    }
    

}
