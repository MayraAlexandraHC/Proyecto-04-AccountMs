package com.bank.AccountMs.service;

import com.bank.AccountMs.model.Cuenta;
import java.util.List;

public interface CuentaService {
    Cuenta crearCuenta(Cuenta cuenta);
    List<Cuenta> listarCuentas();
    List<Cuenta> listarCuentasPorClienteId(Integer clienteId);
    Cuenta obtenerCuentaPorId(Integer id);
    Cuenta depositarDinero(Integer cuentaId, Double monto);
    Cuenta retirarDinero(Integer cuentaId, Double monto);
    void eliminarCuenta(Integer id);
}