package com.nsi.domain.core;

import java.util.Date;

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
@Table(name="news")
public class News {

	private Long id;
	private String imageLocation;
	private String newsContent;
	private String newsTitle;
	private String newsAuthor  ;  
	private Boolean publishStatus;
	private Boolean activeStatus ;
    private Date publishedDate ;
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "news_generator")
	@SequenceGenerator(name="news_generator", sequenceName = "news_news_id_seq", allocationSize=1)
    @Column(name="news_id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="image_location")
	public String getImageLocation() {
		return imageLocation;
	}
	public void setImageLocation(String imageLocation) {
		this.imageLocation = imageLocation;
	}
	
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	@Column(name="news_content")
	public String getNewsContent() {
		return newsContent;
	}
	public void setNewsContent(String newsContent) {
		this.newsContent = newsContent;
	}
	
	@Column(name="news_title")
	public String getNewsTitle() {
		return newsTitle;
	}
	public void setNewsTitle(String newsTitle) {
		this.newsTitle = newsTitle;
	}
	
	@Column(name="news_author")
	public String getNewsAuthor() {
		return newsAuthor;
	}
	public void setNewsAuthor(String newsAuthor) {
		this.newsAuthor = newsAuthor;
	}
	
	@Column(name="publish_status")
	public Boolean getPublishStatus() {
		return publishStatus;
	}
	public void setPublishStatus(Boolean publishStatus) {
		this.publishStatus = publishStatus;
	}
	
	@Column(name="active_status")
	public Boolean getActiveStatus() {
		return activeStatus;
	}
	public void setActiveStatus(Boolean activeStatus) {
		this.activeStatus = activeStatus;
	}
	
	@Column(name="published_date")
	public Date getPublishedDate() {
		return publishedDate;
	}
	public void setPublishedDate(Date publishedDate) {
		this.publishedDate = publishedDate;
	}
    
    
}
