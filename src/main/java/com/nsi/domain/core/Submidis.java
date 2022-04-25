package com.nsi.domain.core;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "submidis")
public class Submidis {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "submidis_id_generator")
    @SequenceGenerator(name="submidis_id_generator", sequenceName = "submidis_id_seq", allocationSize=1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sbn_packages_id")
    private SbnPackages sbnPackages;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_date")
    private Date updatedDate;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "submidis_name")
    private String submidisName;

    @Column(name = "submidis_picture")
    private String submidisPicture;

    @Column(name = "submidis_price")
    private Double submidisPrice;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SbnPackages getSbnPackages() {
        return sbnPackages;
    }

    public void setSbnPackages(SbnPackages sbnPackages) {
        this.sbnPackages = sbnPackages;
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

    public String getSubmidisName() {
        return submidisName;
    }

    public void setSubmidisName(String submidisName) {
        this.submidisName = submidisName;
    }

    public String getSubmidisPicture() {
        return submidisPicture;
    }

    public void setSubmidisPicture(String submidisPicture) {
        this.submidisPicture = submidisPicture;
    }

    public Double getSubmidisPrice() {
        return submidisPrice;
    }

    public void setSubmidisPrice(Double submidisPrice) {
        this.submidisPrice = submidisPrice;
    }
}
