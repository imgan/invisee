package com.nsi.domain.core;

import java.util.Date;

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
@Table(name="log_trx")
public class LogTrx extends BaseDomain {

	private Long id;
	private Integer version;
	private Date logDate = new Date();
	private User userId;
	private String logSource;
	private String logMessage;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "log_trx_generator")
	@SequenceGenerator(name="log_trx_generator", sequenceName = "log_trx_id_log_seq", allocationSize=1)
	@Column(name="id_log")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="version")
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	
	@Column(name="log_date")
	public Date getLogDate() {
		return logDate;
	}
	public void setLogDate(Date logDate) {
		this.logDate = logDate;
	}
	
	@ManyToOne
	@JoinColumn(name="user_id_id")
	public User getUserId() {
		return userId;
	}
	public void setUserId(User userId) {
		this.userId = userId;
	}
	
	@Column(name="log_source")
	public String getLogSource() {
		return logSource;
	}
	public void setLogSource(String logSource) {
		this.logSource = logSource;
	}
	
	@Column(name="log_message")
	public String getLogMessage() {
		return logMessage;
	}
	public void setLogMessage(String logMessage) {
		this.logMessage = logMessage;
	}
	
	
}
