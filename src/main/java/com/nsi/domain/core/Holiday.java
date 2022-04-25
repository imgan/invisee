package com.nsi.domain.core;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="holiday")
public class Holiday extends BaseDomain {

	private Long id;
	private Long yearValue;
	private Date holidayDate;
	private String holidayDescription;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "holiday_generator")
	@SequenceGenerator(name="holiday_generator", sequenceName = "holiday_holiday_id_seq", allocationSize=1)
	@Column(name="holiday_id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="year_value")
	public Long getYearValue() {
		return yearValue;
	}
	public void setYearValue(Long yearValue) {
		this.yearValue = yearValue;
	}
	
	@Column(name="holiday_date")
	public Date getHolidayDate() {
		return holidayDate;
	}
	public void setHolidayDate(Date holidayDate) {
		this.holidayDate = holidayDate;
	}
	
	@Column(name="holiday_description", length=100)
	public String getHolidayDescription() {
		return holidayDescription;
	}
	public void setHolidayDescription(String holidayDescription) {
		this.holidayDescription = holidayDescription;
	}
	
}
