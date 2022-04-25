package com.nsi.domain.core;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "countries")
public class Countries extends BaseDomain {

    private Long id;
    private String alpha3Code;
    private String numericCode;
    private String countryName;
    private String atCountryCode;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "countries_generator")
    @SequenceGenerator(name = "countries_generator", sequenceName = "countries_country_code_seq", allocationSize = 1)
    @Column(name = "country_code")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "alpha3code", length = 3)
    public String getAlpha3Code() {
        return alpha3Code;
    }

    public void setAlpha3Code(String alpha3Code) {
        this.alpha3Code = alpha3Code;
    }

    @Column(name = "numeric_code", length = 3)
    public String getNumericCode() {
        return numericCode;
    }

    public void setNumericCode(String numericCode) {
        this.numericCode = numericCode;
    }

    @Column(name = "country_name")
    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    @Column(name = "at_country_code")
    public String getAtCountryCode() {
        return atCountryCode;
    }

    public void setAtCountryCode(String atCountryCode) {
        this.atCountryCode = atCountryCode;
    }

}
