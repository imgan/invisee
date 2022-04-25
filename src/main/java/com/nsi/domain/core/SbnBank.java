package com.nsi.domain.core;

import org.hibernate.annotations.Type;
import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="sbn_bank")
public class SbnBank {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sbn_bank_id_generator")
    @SequenceGenerator(name="sbn_bank_id_generator", sequenceName = "sbn_bank_id_seq", allocationSize=1)
    private Long id;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "petunjuk")
    private String petunjuk;

    @Column(name = "method")
    private String method;

    @Column(name = "bank_name", nullable = false)
    private String bankName;

    @Column(name = "image_key", nullable = false)
    private String imageKey;

    @Column(name = "created_date", nullable = false)
    private Date createdDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPetunjuk() {
        return petunjuk;
    }

    public void setPetunjuk(String petunjuk) {
        this.petunjuk = petunjuk;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getImageKey() {
        return imageKey;
    }

    public void setImageKey(String imageKey) {
        this.imageKey = imageKey;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}