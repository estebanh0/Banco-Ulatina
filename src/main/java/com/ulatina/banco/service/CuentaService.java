/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ulatina.banco.service;

import com.ulatina.banco.model.Cuenta;
import com.ulatina.banco.model.Cuenta.Moneda;
import com.ulatina.banco.model.Cuenta.EstadoCuenta;
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
 * 
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

}
