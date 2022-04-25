package com.nsi.domain.core;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "package_payment")
public class PackagePayment implements Serializable {

    private Long id;
    private FundPackages fundPackages;
    private PaymentMethod paymentMethod;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "package_payment_generator")
    @SequenceGenerator(name = "package_payment_generator", sequenceName = "package_payment_id_seq", allocationSize = 1)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "package")
    public FundPackages getFundPackages() {
        return fundPackages;
    }

    public void setFundPackages(FundPackages fundPackages) {
        this.fundPackages = fundPackages;
    }

    @ManyToOne
    @JoinColumn(name = "payment_method")
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
