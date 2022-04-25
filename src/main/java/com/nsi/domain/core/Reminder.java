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
@Table(name="reminder")
public class Reminder extends BaseDomain {

	private Long id;
	private Integer version;
	private Kyc cust;
	private InvestmentAccounts investmentAccount;
	private String reminderType;
	private Date reminderStartTime;
	private Date durationStartDate;
	private Date durationStopDate;
	private String reminderDesc;
	private Boolean reminderStatus;
	private Date lastTrigger;
	private Double reminderAmount;
	private Integer remindValue;
	private String remindUnit;
	private FundPackages fundPackageRef;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reminder_generator")
	@SequenceGenerator(name="reminder_generator", sequenceName = "reminder_reminder_id_seq", allocationSize=1)
	@Column(name="reminder_id")
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
	
	@ManyToOne
	@JoinColumn(name="cust_id")
	public Kyc getCust() {
		return cust;
	}
	public void setCust(Kyc cust) {
		this.cust = cust;
	}
	
	@ManyToOne
	@JoinColumn(name="investment_account_id")
	public InvestmentAccounts getInvestmentAccount() {
		return investmentAccount;
	}
	public void setInvestmentAccount(InvestmentAccounts investmentAccount) {
		this.investmentAccount = investmentAccount;
	}
	
	@Column(name="reminder_type")
	public String getReminderType() {
		return reminderType;
	}
	public void setReminderType(String reminderType) {
		this.reminderType = reminderType;
	}
	
	@Column(name="reminder_start_time")
	public Date getReminderStartTime() {
		return reminderStartTime;
	}
	public void setReminderStartTime(Date reminderStartTime) {
		this.reminderStartTime = reminderStartTime;
	}
	
	@Column(name="duration_start_date")
	public Date getDurationStartDate() {
		return durationStartDate;
	}
	public void setDurationStartDate(Date durationStartDate) {
		this.durationStartDate = durationStartDate;
	}
	
	@Column(name="duration_stop_date")
	public Date getDurationStopDate() {
		return durationStopDate;
	}
	public void setDurationStopDate(Date durationStopDate) {
		this.durationStopDate = durationStopDate;
	}
	
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	@Column(name="reminder_desc")
	public String getReminderDesc() {
		return reminderDesc;
	}
	public void setReminderDesc(String reminderDesc) {
		this.reminderDesc = reminderDesc;
	}
	
	@Column(name="reminder_status")
	public Boolean getReminderStatus() {
		return reminderStatus;
	}
	public void setReminderStatus(Boolean reminderStatus) {
		this.reminderStatus = reminderStatus;
	}
	
	@Column(name="last_trigger")
	public Date getLastTrigger() {
		return lastTrigger;
	}
	public void setLastTrigger(Date lastTrigger) {
		this.lastTrigger = lastTrigger;
	}
	
	@Column(name="reminder_amount")
	public Double getReminderAmount() {
		return reminderAmount;
	}
	public void setReminderAmount(Double reminderAmount) {
		this.reminderAmount = reminderAmount;
	}
	
	@Column(name="remind_value")
	public Integer getRemindValue() {
		return remindValue;
	}
	public void setRemindValue(Integer remindValue) {
		this.remindValue = remindValue;
	}
	
	@Column(name="remind_unit")
	public String getRemindUnit() {
		return remindUnit;
	}
	public void setRemindUnit(String remindUnit) {
		this.remindUnit = remindUnit;
	}
	
	@ManyToOne
	@JoinColumn(name="fund_package_ref_id")
	public FundPackages getFundPackageRef() {
		return fundPackageRef;
	}
	public void setFundPackageRef(FundPackages fundPackageRef) {
		this.fundPackageRef = fundPackageRef;
	}
	
	
}
