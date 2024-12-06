package com.bank.AccountMs.service;

import com.bank.AccountMs.client.ClienteClient;
import com.bank.AccountMs.exception.CuentaException;
import com.bank.AccountMs.exception.SaldoInsuficienteException;
import com.bank.AccountMs.model.Cuenta;
import com.bank.AccountMs.model.TipoCuenta;
import com.bank.AccountMs.repository.CuentaRepository;
import com.bank.AccountMs.service.impl.CuentaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CuentaServiceImplTest {

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private ClienteClient clienteClient;

    @InjectMocks
    private CuentaServiceImpl cuentaService;

    private Cuenta cuenta;

    @BeforeEach
    void setUp() {
        cuenta = Cuenta.builder()
                .id(1)
                .numeroCuenta("12345678901234")
                .saldo(1000.0)
                .tipoCuenta(TipoCuenta.AHORROS)
                .clienteId(1)
                .build();
    }

    @Test
    void crearCuenta_DatosValidos_RetornaCuentaCreada() {
        // Arrange
        Cuenta nuevaCuenta = Cuenta.builder()
                .tipoCuenta(TipoCuenta.AHORROS)
                .clienteId(1)
                .build();

        Cuenta cuentaGuardada = Cuenta.builder()
                .id(1)
                .numeroCuenta("1234567890")
                .saldo(0.0)  // El saldo inicial siempre es 0
                .tipoCuenta(TipoCuenta.AHORROS)
                .clienteId(1)
                .build();

        when(clienteClient.existeCliente(1)).thenReturn(true);
        when(cuentaRepository.save(any())).thenReturn(cuentaGuardada);

        // Act
        Cuenta resultado = cuentaService.crearCuenta(nuevaCuenta);

        // Assert
        assertNotNull(resultado);
        assertEquals(0.0, resultado.getSaldo());
        assertNotNull(resultado.getNumeroCuenta());
        verify(clienteClient).existeCliente(1);
        verify(cuentaRepository).save(any());
    }

    @Test
    void crearCuenta_ClienteNoExiste_LanzaExcepcion() {
        when(clienteClient.existeCliente(1)).thenReturn(false);

        Cuenta nuevaCuenta = Cuenta.builder()
                .tipoCuenta(TipoCuenta.AHORROS)
                .clienteId(1)
                .build();

        assertThrows(CuentaException.class, () -> cuentaService.crearCuenta(nuevaCuenta));
    }

    @Test
    void depositarDinero_MontoValido_ActualizaSaldo() {
        when(cuentaRepository.findById(1)).thenReturn(Optional.of(cuenta));
        when(cuentaRepository.save(any())).thenReturn(cuenta);

        Cuenta resultado = cuentaService.depositarDinero(1, 500.0);

        assertEquals(1500.0, resultado.getSaldo());
    }

    @Test
    void depositarDinero_MontoNegativo_LanzaExcepcion() {
        assertThrows(CuentaException.class, () -> cuentaService.depositarDinero(1, -100.0));
    }

    @Test
    void retirarDinero_CuentaAhorros_SaldoSuficiente_RealizaRetiro() {
        when(cuentaRepository.findById(1)).thenReturn(Optional.of(cuenta));
        when(cuentaRepository.save(any())).thenReturn(cuenta);

        Cuenta resultado = cuentaService.retirarDinero(1, 500.0);

        assertEquals(500.0, resultado.getSaldo());
    }

    @Test
    void retirarDinero_CuentaAhorros_SaldoInsuficiente_LanzaExcepcion() {
        when(cuentaRepository.findById(1)).thenReturn(Optional.of(cuenta));

        assertThrows(SaldoInsuficienteException.class,
                () -> cuentaService.retirarDinero(1, 2000.0));
    }

    @Test
    void retirarDinero_CuentaCorriente_PermiteSobregiro() {
        cuenta.setTipoCuenta(TipoCuenta.CORRIENTE);
        when(cuentaRepository.findById(1)).thenReturn(Optional.of(cuenta));
        when(cuentaRepository.save(any())).thenReturn(cuenta);

        Cuenta resultado = cuentaService.retirarDinero(1, 1400.0);

        assertEquals(-400.0, resultado.getSaldo());
    }

    @Test
    void retirarDinero_CuentaCorriente_ExcedeSobregiro_LanzaExcepcion() {
        cuenta.setTipoCuenta(TipoCuenta.CORRIENTE);
        when(cuentaRepository.findById(1)).thenReturn(Optional.of(cuenta));

        assertThrows(SaldoInsuficienteException.class,
                () -> cuentaService.retirarDinero(1, 2000.0));
    }

    @Test
    void listarCuentasPorClienteId_ExistenCuentas_RetornaLista() {
        // Arrange
        List<Cuenta> cuentasEsperadas = Arrays.asList(cuenta);
        when(cuentaRepository.findByClienteId(1)).thenReturn(cuentasEsperadas);

        // Act
        List<Cuenta> resultado = cuentaService.listarCuentasPorClienteId(1);

        // Assert
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals(cuenta.getId(), resultado.get(0).getId());
        verify(cuentaRepository).findByClienteId(1);
    }
}