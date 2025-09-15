/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ulatina.banco.service;

import com.ulatina.banco.model.Prestamo;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author santiago
 * Genera la tabla de amortización completa en memoria
 * 
 */

public class PrestamoService {

    public List<Prestamo> calcularTablaAmortizacion(BigDecimal monto, BigDecimal tasaAnual, int anos) {

        List<Prestamo> tabla = new ArrayList<>();

        // Calcula cuota fija mensual
        BigDecimal cuotaFija = calcularCuotaFija(monto, tasaAnual, anos);

        BigDecimal tasaMensual = tasaAnual.divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);
        BigDecimal capitalPendiente = monto;
        int totalCuotas = anos * 12;

        // Genera cada período de la tabla
        for (int periodo = 1; periodo <= totalCuotas; periodo++) {

            // Calcula el interés del período
            BigDecimal interes = capitalPendiente.multiply(tasaMensual)
                    .setScale(2, RoundingMode.HALF_UP);

            // Calcula amortización de capital
            BigDecimal amortizacion = cuotaFija.subtract(interes);

            // Ajusta la última cuota para cerrar el préstamo
            if (periodo == totalCuotas) {
                amortizacion = capitalPendiente;
                cuotaFija = interes.add(amortizacion);
            }

            // Calcula nuevo capital pendiente
            BigDecimal nuevoCapitalPendiente = capitalPendiente.subtract(amortizacion);
            if (nuevoCapitalPendiente.compareTo(BigDecimal.ZERO) < 0) {
                nuevoCapitalPendiente = BigDecimal.ZERO;
            }

            // Crea fila de amortización
            Prestamo fila = new Prestamo(periodo, interes, amortizacion,
                    cuotaFija, nuevoCapitalPendiente);
            tabla.add(fila);

            // Actualiza el capital pendiente para siguiente iteración
            capitalPendiente = nuevoCapitalPendiente;
        }

        return tabla;
    }

        //Calcula la cuota fija mensual usando la fórmula de anualidad
    private BigDecimal calcularCuotaFija(BigDecimal monto, BigDecimal tasaAnual, int anos) {

        // Convierte tasa anual a mensual
        BigDecimal tasaMensual = tasaAnual.divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);
        int totalCuotas = anos * 12;

        // Fórmula de anualidad: C = P * [r * (1 + r)^n] / [(1 + r)^n - 1]
        BigDecimal unoSumaTasa = BigDecimal.ONE.add(tasaMensual);
        BigDecimal potencia = unoSumaTasa.pow(totalCuotas);

        BigDecimal numerador = tasaMensual.multiply(potencia);
        BigDecimal denominador = potencia.subtract(BigDecimal.ONE);

        return monto.multiply(numerador.divide(denominador, 2, RoundingMode.HALF_UP));
    }

    public BigDecimal obtenerCuotaMensual(BigDecimal monto, BigDecimal tasaAnual, int anos) {
        return calcularCuotaFija(monto, tasaAnual, anos);
    }
}
