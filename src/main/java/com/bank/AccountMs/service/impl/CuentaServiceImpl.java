package com.bank.AccountMs.service.impl;

import com.bank.AccountMs.exception.CuentaException;
import com.bank.AccountMs.exception.SaldoInsuficienteException;
import com.bank.AccountMs.model.Cuenta;
import com.bank.AccountMs.model.TipoCuenta;
import com.bank.AccountMs.repository.CuentaRepository;
import com.bank.AccountMs.client.ClienteClient;
import com.bank.AccountMs.service.CuentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CuentaServiceImpl implements CuentaService {
    private final CuentaRepository cuentaRepository;
    private final ClienteClient clienteClient;

    @Override
    @Transactional
    public Cuenta crearCuenta(Cuenta cuenta) {
        if (!clienteClient.existeCliente(cuenta.getClienteId())) {
            throw new CuentaException("El cliente no existe");
        }
        cuenta.setNumeroCuenta(generarNumeroCuenta());
        cuenta.setSaldo(0.0);
        return cuentaRepository.save(cuenta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cuenta> listarCuentas() {
        return cuentaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cuenta> listarCuentasPorClienteId(Integer clienteId) {
        return cuentaRepository.findByClienteId(clienteId);
    }

    @Override
    @Transactional(readOnly = true)
    public Cuenta obtenerCuentaPorId(Integer id) {
        return cuentaRepository.findById(id)
                .orElseThrow(() -> new CuentaException("Cuenta no encontrada"));
    }

    @Override
    @Transactional
    public Cuenta depositarDinero(Integer cuentaId, Double monto) {
        if (monto <= 0) {
            throw new CuentaException("El monto debe ser mayor a 0");
        }

        Cuenta cuenta = obtenerCuentaPorId(cuentaId);
        cuenta.setSaldo(cuenta.getSaldo() + monto);
        return cuentaRepository.save(cuenta);
    }

    @Override
    @Transactional
    public Cuenta retirarDinero(Integer cuentaId, Double monto) {
        if (monto <= 0) {
            throw new CuentaException("El monto debe ser mayor a 0");
        }

        Cuenta cuenta = obtenerCuentaPorId(cuentaId);
        double nuevoSaldo = cuenta.getSaldo() - monto;

        if (cuenta.getTipoCuenta() == TipoCuenta.AHORROS && nuevoSaldo < 0) {
            throw new SaldoInsuficienteException(
                    "No se puede realizar el retiro. La cuenta de ahorros no permite saldo negativo. " +
                            "Saldo actual: " + cuenta.getSaldo()
            );
        }

        if (cuenta.getTipoCuenta() == TipoCuenta.CORRIENTE && nuevoSaldo < -500) {
            throw new SaldoInsuficienteException(
                    "No se puede realizar el retiro. La cuenta corriente no permite un sobregiro mayor a 500. " +
                            "Saldo actual: " + cuenta.getSaldo() +
                            ", Sobregiro máximo permitido: 500"
            );
        }

        cuenta.setSaldo(nuevoSaldo);
        return cuentaRepository.save(cuenta);
    }

    @Override
    @Transactional
    public void eliminarCuenta(Integer id) {
        Cuenta cuenta = obtenerCuentaPorId(id);
        cuentaRepository.delete(cuenta);
    }

    private String generarNumeroCuenta() {
        return String.format("%010d",
                Long.parseLong(UUID.randomUUID().toString().replace("-", "").substring(0, 14), 16) % 10000000000L);

    }

}