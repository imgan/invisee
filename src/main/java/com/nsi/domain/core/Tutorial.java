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
@Table(name="tutorial")
public class Tutorial extends BaseDomain {

	private Long id;
	private String title;
	private String image;
	private String description;
	private Boolean publishStatus = false;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tutorial_generator")
	@SequenceGenerator(name="tutorial_generator", sequenceName = "tutorial_tutorial_id_seq", allocationSize=1)
	@Column(name="tutorial_id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="title")
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	@Column(name="image", length=36)
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
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
	
	@Column(name="publish_status")
	public Boolean getPublishStatus() {
		return publishStatus;
	}
	public void setPublishStatus(Boolean publishStatus) {
		this.publishStatus = publishStatus;
	}
	
	
}
