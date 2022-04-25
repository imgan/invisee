package com.nsi.domain.core;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="goal_category")
public class GoalCategory extends BaseDomain {

	private String id= UUID.randomUUID().toString();
	private Integer version;
	private String categoryName;
	private String categoryImagePath;
	private Boolean activeStatus;    
	private int seq;
	
	@Id
	@Column(name="goal_category_id")
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	@Column(name="version")
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	
	@Column(name="category_name", length=30)
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	
	@Column(name="category_image_path", length=45)
	public String getCategoryImagePath() {
		return categoryImagePath;
	}
	public void setCategoryImagePath(String categoryImagePath) {
		this.categoryImagePath = categoryImagePath;
	}
	
	@Column(name="active_status")
	public Boolean getActiveStatus() {
		return activeStatus;
	}
	public void setActiveStatus(Boolean activeStatus) {
		this.activeStatus = activeStatus;
	}
	
	@Column(name="seq")
	public int getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
    
    
}
