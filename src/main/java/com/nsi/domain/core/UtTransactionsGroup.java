package com.nsi.domain.core;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "ut_transactions_group")
public class UtTransactionsGroup implements Serializable {

    private Long id;
    private Long version;
    private String orderNo;
    private String channel;
    private Double orderAmount;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ut_transactions_group_generator")
    @SequenceGenerator(name = "ut_transactions_group_generator", sequenceName = "ut_transactions_group_id_seq", allocationSize = 1)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "version")
    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Column(name = "order_no")
    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    @Column(name = "channel")
    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    @Column(name = "order_amount")
    public Double getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(Double orderAmount) {
        this.orderAmount = orderAmount;
    }

    @Override
    public String toString() {
        return "UtTransactionsGroup{" + "id=" + id + ", version=" + version + ", orderNo=" + orderNo + ", channel=" + channel + ", orderAmount=" + orderAmount + '}';
    }
}
