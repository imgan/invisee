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
@Table(name="password_histories")
public class PasswordHistories extends BaseDomain {

	private Long id;
	private String loginId;
	private String pass;
	private String stat;
	private User userAccounts;
	private String dateFormatted;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "password_histories_generator")
	@SequenceGenerator(name="password_histories_generator", sequenceName = "password_histories_pass_history_id_seq", allocationSize=1)
	@Column(name="pass_history_id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="login_id", length=50)
	public String getLoginId() {
		return loginId;
	}
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	
	@Column(name="pass", length=250)
	public String getPass() {
		return pass;
	}
	public void setPass(String pass) {
		this.pass = pass;
	}
	
	@Column(name="stat", length=1)
	public String getStat() {
		return stat;
	}
	public void setStat(String stat) {
		this.stat = stat;
	}
	
	@ManyToOne
	@JoinColumn(name="user_accounts_id")
	public User getUserAccounts() {
		return userAccounts;
	}
	public void setUserAccounts(User userAccounts) {
		this.userAccounts = userAccounts;
	}
	
	@Column(name="date_formatted")
	public String getDateFormatted() {
		return dateFormatted;
	}
	public void setDateFormatted(String dateFormatted) {
		this.dateFormatted = dateFormatted;
	}
	
	
}
