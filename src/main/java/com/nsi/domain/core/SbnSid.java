package com.nsi.domain.core;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "sbn_sid")
public class SbnSid {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sbn_sid_id_generator")
    @SequenceGenerator(name="sbn_sid_id_generator", sequenceName = "sbn_sid_id_seq", allocationSize=1)
    private Long id;

    @Column(name="created_date")
    private Date createdDate;

    @Column(name="created_by")
    private String createdBy;

    @Column(name="updated_date")
    private Date updatedDate;

    @Column(name="updated_by")
    private String updatedBy;

    @Column(name="sid")
    private String sid;

    @Column(name="sid_name")
    private String sidName;

    @Column(name="id_partisipan_subregistry")
    private String idPartisipanSubregistry;

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "kyc_id")
    private Kyc kyc;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getSidName() {
        return sidName;
    }

    public void setSidName(String sidName) {
        this.sidName = sidName;
    }

    public String getIdPartisipanSubregistry() {
        return idPartisipanSubregistry;
    }

    public void setIdPartisipanSubregistry(String idPartisipanSubregistry) {
        this.idPartisipanSubregistry = idPartisipanSubregistry;
    }

    public Kyc getKyc() {
        return kyc;
    }

    public void setKyc(Kyc kyc) {
        this.kyc = kyc;
    }
}
