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
@Table(name="content")
public class Content extends BaseDomain {

	private Long  id;
	private Long image;
	private String description;
	private String highlightTitle;
	private Boolean publishStatus = false;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "content_generator")
	@SequenceGenerator(name="content_generator", sequenceName = "content_highlight_id_seq", allocationSize=1)
	@Column(name="highlight_id")
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
	
	@Column(name="highlight_title")
	public String getHighlightTitle() {
		return highlightTitle;
	}
	public void setHighlightTitle(String highlightTitle) {
		this.highlightTitle = highlightTitle;
	}
	
	@Column(name="publish_status")
	public Boolean getPublishStatus() {
		return publishStatus;
	}
	public void setPublishStatus(Boolean publishStatus) {
		this.publishStatus = publishStatus;
	}
	
	
}
