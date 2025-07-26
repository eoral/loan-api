package com.eoral.loanapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "loan_installment")
@Getter
@Setter
public class LoanInstallment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "loan_ins_seq_gen")
    @SequenceGenerator(name = "loan_ins_seq_gen", sequenceName = "loan_ins_seq", allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "loan_id", referencedColumnName = "id")
    private Loan loan;

    @Column
    private BigDecimal amountWithoutInterest;

    @Column
    private BigDecimal amount;

    @Column
    private BigDecimal paidAmount;

    @Column
    private LocalDate dueDate;

    @Column
    private LocalDate paymentDate;

    @Column
    private Boolean isPaid;
}
