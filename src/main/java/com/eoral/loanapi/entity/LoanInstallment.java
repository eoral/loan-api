package com.eoral.loanapi.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "loan_installment")
public class LoanInstallment { // todo: consider using a base entity and timestamps

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Loan getLoan() {
        return loan;
    }

    public void setLoan(Loan loan) {
        this.loan = loan;
    }

    public BigDecimal getAmountWithoutInterest() {
        return amountWithoutInterest;
    }

    public void setAmountWithoutInterest(BigDecimal amountWithoutInterest) {
        this.amountWithoutInterest = amountWithoutInterest;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Boolean getPaid() {
        return isPaid;
    }

    public void setPaid(Boolean paid) {
        isPaid = paid;
    }
}
