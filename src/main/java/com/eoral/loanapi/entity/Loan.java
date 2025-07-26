package com.eoral.loanapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "loan")
@Getter
@Setter
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "loan_seq_gen")
    @SequenceGenerator(name = "loan_seq_gen", sequenceName = "loan_seq", allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private Customer customer;

    @Column
    private BigDecimal loanAmount;

    @Column
    private Integer numberOfInstallments;

    @Column
    private BigDecimal interestRate;

    @Column
    private LocalDate createDate;

    @Column
    private Boolean isPaid;

    @Column
    @CreatedBy
    private String createdBy;

    @Column
    @CreatedDate
    private Instant createdDate;
}
