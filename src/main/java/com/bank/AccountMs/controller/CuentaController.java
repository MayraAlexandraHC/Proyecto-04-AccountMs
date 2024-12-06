package com.bank.AccountMs.controller;

import com.bank.AccountMs.model.Cuenta;
import com.bank.AccountMs.model.Transaccion;
import com.bank.AccountMs.service.CuentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cuentas")
@RequiredArgsConstructor
public class CuentaController {
    private final CuentaService cuentaService;

    @PostMapping
    public ResponseEntity<Cuenta> crearCuenta(@Valid @RequestBody Cuenta cuenta) {
        Cuenta cuentaCreada = cuentaService.crearCuenta(cuenta);
        return new ResponseEntity<>(cuentaCreada, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Cuenta>> listarCuentas(
            @RequestParam(required = false) Integer clienteId) {
        if (clienteId != null) {
            List<Cuenta> cuentas = cuentaService.listarCuentasPorClienteId(clienteId);
            return ResponseEntity.ok(cuentas);
        }
        return ResponseEntity.ok(cuentaService.listarCuentas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cuenta> obtenerCuenta(@PathVariable Integer id) {
        Cuenta cuenta = cuentaService.obtenerCuentaPorId(id);
        return ResponseEntity.ok(cuenta);
    }

    @PutMapping("/{cuentaId}/depositar")
    public ResponseEntity<Cuenta> depositarDinero(
            @PathVariable Integer cuentaId,
            @Valid @RequestBody Transaccion transaccion) {
        Cuenta cuenta = cuentaService.depositarDinero(cuentaId, transaccion.getMonto());
        return ResponseEntity.ok(cuenta);
    }

    @PutMapping("/{cuentaId}/retirar")
    public ResponseEntity<Cuenta> retirarDinero(
            @PathVariable Integer cuentaId,
            @Valid @RequestBody Transaccion transaccion) {
        Cuenta cuenta = cuentaService.retirarDinero(cuentaId, transaccion.getMonto());
        return ResponseEntity.ok(cuenta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCuenta(@PathVariable Integer id) {
        cuentaService.eliminarCuenta(id);
        return ResponseEntity.noContent().build();
    }
}