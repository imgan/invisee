package com.nsi.domain.core;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "lookup_line")
public class LookupLine extends BaseDomain {

    private Long id;
    private LookupHeader category;
    private String code;
    private String value;
    private String description;
    private String atLookupId;
    private Integer sequenceLookup;
    private Boolean publishStatus = false;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lookup_line_generator")
    @SequenceGenerator(name = "lookup_line_generator", sequenceName = "lookup_line_lookup_id_seq", allocationSize = 1)
    @Column(name = "lookup_id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "category_id")
    public LookupHeader getCategory() {
        return category;
    }

    public void setCategory(LookupHeader category) {
        this.category = category;
    }

    @Column(name = "code")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "value")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "at_lookup_id")
    public String getAtLookupId() {
        return atLookupId;
    }

    public void setAtLookupId(String atLookupId) {
        this.atLookupId = atLookupId;
    }

    @Column(name = "publish_status")
    public Boolean getPublishStatus() {
        return publishStatus;
    }

    public void setPublishStatus(Boolean publishStatus) {
        this.publishStatus = publishStatus;
    }

    @Column(name = "sequence_lookup")
    public Integer getSequenceLookup() {
        return sequenceLookup;
    }

    public void setSequenceLookup(Integer sequenceLookup) {
        this.sequenceLookup = sequenceLookup;
    }

}
