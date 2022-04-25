package com.nsi.domain.core;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "sbn_subregistry")
public class SbnSubregistry {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sbn_subregistry_id_generator")
    @SequenceGenerator(name="sbn_subregistry_id_generator", sequenceName = "sbn_subregistry_id_seq", allocationSize=1)
    private Long id;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "subreg_id", length = 50, unique = true)
    private String subregId;

    @Column(name = "subreg_name", length = 150)
    private String subregName;

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

    public String getSubregId() {
        return subregId;
    }

    public void setSubregId(String subregId) {
        this.subregId = subregId;
    }

    public String getSubregName() {
        return subregName;
    }

    public void setSubregName(String subregName) {
        this.subregName = subregName;
    }
}