package com.bank.AccountMs.repository;

import com.bank.AccountMs.model.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Integer> {
    List<Cuenta> findByClienteId(Integer clienteId);
    boolean existsByClienteId(Integer clienteId);
    boolean existsByNumeroCuenta(String numeroCuenta);
}