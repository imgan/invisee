package com.nsi.domain.core;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

@Entity
@Table(name="info_area")
public class InfoArea extends BaseDomain {

	private Long id;
	private Long image;
	private String description;
	private String infoAreaTitle;
	private Boolean publishStatus = false;
	private Boolean publishFrontStatus = false;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "info_area_generator")
	@SequenceGenerator(name="info_area_generator", sequenceName = "info_area_info_area_id_seq", allocationSize=1)
	@Column(name="info_area_id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="image")
	public Long getImage() {
		return image;
	}
	public void setImage(Long image) {
		this.image = image;
	}
	
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	@Column(name="description")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Column(name="info_area_title")
	public String getInfoAreaTitle() {
		return infoAreaTitle;
	}
	public void setInfoAreaTitle(String infoAreaTitle) {
		this.infoAreaTitle = infoAreaTitle;
	}
	
	@Column(name="publish_status")
	public Boolean getPublishStatus() {
		return publishStatus;
	}
	public void setPublishStatus(Boolean publishStatus) {
		this.publishStatus = publishStatus;
	}
	
	@Column(name="publish_front_status")
	public Boolean getPublishFrontStatus() {
		return publishFrontStatus;
	}
	public void setPublishFrontStatus(Boolean publishFrontStatus) {
		this.publishFrontStatus = publishFrontStatus;
	}
	
	
}
