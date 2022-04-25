package com.nsi.domain.core;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

@Entity
@Table(name="log_tracker")
public class LogTracker {

	private Long id;
	private String uri;
	private String url;
	private String queryString;
	private Date timeAccess = new Date();
	private User userAccess;
	private String parametersAccess = "";
	private String parametersRequest = "";
	private String parametersHeader = "";
	private String remoteIp;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "log_tracker_generator")
	@SequenceGenerator(name="log_tracker_generator", sequenceName = "log_tracker_log_tracker_id_seq", allocationSize=1)
	@Column(name="log_tracker_id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	@Column(name="uri")
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	@Column(name="url")
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	@Column(name="query_string")
	public String getQueryString() {
		return queryString;
	}
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}
	
	@Column(name="time_access")
	public Date getTimeAccess() {
		return timeAccess;
	}
	public void setTimeAccess(Date timeAccess) {
		this.timeAccess = timeAccess;
	}
	
	@ManyToOne
	@JoinColumn(name="user_access_id")
	public User getUserAccess() {
		return userAccess;
	}
	public void setUserAccess(User userAccess) {
		this.userAccess = userAccess;
	}
	
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	@Column(name="parameters_access")
	public String getParametersAccess() {
		return parametersAccess;
	}
	public void setParametersAccess(String parametersAccess) {
		this.parametersAccess = parametersAccess;
	}
	
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	@Column(name="parameters_request")
	public String getParametersRequest() {
		return parametersRequest;
	}
	public void setParametersRequest(String parametersRequest) {
		this.parametersRequest = parametersRequest;
	}
	
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	@Column(name="parameters_header")
	public String getParametersHeader() {
		return parametersHeader;
	}
	public void setParametersHeader(String parametersHeader) {
		this.parametersHeader = parametersHeader;
	}
	
	@Column(name="remote_ip")
	public String getRemoteIp() {
		return remoteIp;
	}
	public void setRemoteIp(String remoteIp) {
		this.remoteIp = remoteIp;
	}
    
    
}
