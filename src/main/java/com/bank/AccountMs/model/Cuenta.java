package com.bank.AccountMs.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cuentas")
public class Cuenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false, length = 10)
    private String numeroCuenta;

    @Column(nullable = false)
    private Double saldo;

    @NotNull(message = "El tipo de cuenta es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoCuenta tipoCuenta;

    @NotNull(message = "El ID del cliente es obligatorio")
    @Column(name = "cliente_id", nullable = false)
    private Integer clienteId;
}