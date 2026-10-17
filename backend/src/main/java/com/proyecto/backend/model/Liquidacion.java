package com.proyecto.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "liquidaciones", uniqueConstraints = @UniqueConstraint(columnNames = {"empleado_id", "periodo"}))
public class Liquidacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Empleado empleado;

    @Column(nullable = false)
    private String periodo;

    private int diasTrabajados;
    private BigDecimal sueldoBruto;
    private BigDecimal bonificaciones;
    private BigDecimal afp;
    private BigDecimal isapre;
    private BigDecimal impuestos;
    private BigDecimal otrosDescuentos;
    private BigDecimal totalDescuentos;
    private BigDecimal sueldoLiquido;
    private LocalDateTime fechaCalculo;
    private String estado = "CALCULADA";

    @JsonIgnore
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] documentoPdf;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public int getDiasTrabajados() {
        return diasTrabajados;
    }

    public void setDiasTrabajados(int diasTrabajados) {
        this.diasTrabajados = diasTrabajados;
    }

    public BigDecimal getSueldoBruto() {
        return sueldoBruto;
    }

    public void setSueldoBruto(BigDecimal sueldoBruto) {
        this.sueldoBruto = sueldoBruto;
    }

    public BigDecimal getBonificaciones() {
        return bonificaciones;
    }

    public void setBonificaciones(BigDecimal bonificaciones) {
        this.bonificaciones = bonificaciones;
    }

    public BigDecimal getAfp() {
        return afp;
    }

    public void setAfp(BigDecimal afp) {
        this.afp = afp;
    }

    public BigDecimal getIsapre() {
        return isapre;
    }

    public void setIsapre(BigDecimal isapre) {
        this.isapre = isapre;
    }

    public BigDecimal getImpuestos() {
        return impuestos;
    }

    public void setImpuestos(BigDecimal impuestos) {
        this.impuestos = impuestos;
    }

    public BigDecimal getOtrosDescuentos() {
        return otrosDescuentos;
    }

    public void setOtrosDescuentos(BigDecimal otrosDescuentos) {
        this.otrosDescuentos = otrosDescuentos;
    }

    public BigDecimal getTotalDescuentos() {
        return totalDescuentos;
    }

    public void setTotalDescuentos(BigDecimal totalDescuentos) {
        this.totalDescuentos = totalDescuentos;
    }

    public BigDecimal getSueldoLiquido() {
        return sueldoLiquido;
    }

    public void setSueldoLiquido(BigDecimal sueldoLiquido) {
        this.sueldoLiquido = sueldoLiquido;
    }

    public LocalDateTime getFechaCalculo() {
        return fechaCalculo;
    }

    public void setFechaCalculo(LocalDateTime fechaCalculo) {
        this.fechaCalculo = fechaCalculo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public byte[] getDocumentoPdf() {
        return documentoPdf;
    }

    public void setDocumentoPdf(byte[] documentoPdf) {
        this.documentoPdf = documentoPdf;
    }
}
