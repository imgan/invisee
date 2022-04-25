package com.nsi.domain.core;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="banner_area")
public class BannerArea extends BaseDomain {

	private Long id;
	private Long image;
	private String description;
	private String bannerTitle;
	private Boolean publishStatus = false;
	private Boolean publishFrontStatus = false;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "banner_area_generator")
	@SequenceGenerator(name="banner_area_generator", sequenceName = "banner_area_banner_id_seq", allocationSize=1)
	@Column(name="banner_id")
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
	@Column(name="description")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Column(name="banner_title")
	public String getBannerTitle() {
		return bannerTitle;
	}
	public void setBannerTitle(String bannerTitle) {
		this.bannerTitle = bannerTitle;
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
