package com.nsi.domain.core;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;

@Entity
@Table(name = "mcw_email_scheduller")
public class McwEmailScheduller implements Serializable {

    private Long id;
    private String apiEmail;
    private String value;
    private Integer status;
    private String response;
    private Date dateCreated;
    private Date dateExecute;
    private String createdBy;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mcw_email_scheduller_generator")
    @SequenceGenerator(name = "mcw_email_scheduller_generator", sequenceName = "mcw_email_scheduller_id_seq", allocationSize = 1)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name="api_email")
    public String getApiEmail() {
        return apiEmail;
    }

    public void setApiEmail(String apiEmail) {
        this.apiEmail = apiEmail;
    }

    @Column(name="value")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Column(name="status")
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Column(name="response")
    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @Column(name="date_created")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Column(name="date_execute")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getDateExecute() {
        return dateExecute;
    }

    public void setDateExecute(Date dateExecute) {
        this.dateExecute = dateExecute;
    }

    @Column(name="create_by")
    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

}
