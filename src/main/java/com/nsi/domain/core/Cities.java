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
@Table(name="cities")
public class Cities extends BaseDomain {

	private Long id;
	private String cityCode;
	private String cityName;
	private States states;
	private String atCityId;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cities_generator")
	@SequenceGenerator(name="cities_generator", sequenceName = "cities_city_id_seq", allocationSize=1)
	@Column(name="city_id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="city_code", length=30)
	public String getCityCode() {
		return cityCode;
	}
	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}
	
	@Column(name="city_name", length=50)
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	
	@ManyToOne
	@JoinColumn(name="states_id")
	public States getStates() {
		return states;
	}
	public void setStates(States states) {
		this.states = states;
	}
	
	@Column(name="at_city_id")
	public String getAtCityId() {
		return atCityId;
	}
	public void setAtCityId(String atCityId) {
		this.atCityId = atCityId;
	}
	
	
}
