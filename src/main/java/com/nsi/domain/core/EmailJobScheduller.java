package com.nsi.domain.core;

import java.util.Date;
import java.util.UUID;
import javax.persistence.*;

import org.springframework.data.annotation.CreatedDate;

@Entity
@Table(name = "email_job_scheduller")
public class EmailJobScheduller {

  @Id
  private String id = UUID.randomUUID().toString();

  @Column(name="date_created")
  private Date dateCreated = new Date();

  @Column(name="email_type")
  private String emailType;

  @Column(name="execute_date")
  private Date executeDate;

  @Column(name="message")
  private String message;

  @Column(name="order_no")
  private String orderNo;

  @Column(name="status")
  private String status;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Date getDateCreated() {
    return dateCreated;
  }

  public void setDateCreated(Date dateCreated) {
    this.dateCreated = dateCreated;
  }

  public String getEmailType() {
    return emailType;
  }

  public void setEmailType(String emailType) {
    this.emailType = emailType;
  }

  public Date getExecuteDate() {
    return executeDate;
  }

  public void setExecuteDate(Date executeDate) {
    this.executeDate = executeDate;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getOrderNo() {
    return orderNo;
  }

  public void setOrderNo(String orderNo) {
    this.orderNo = orderNo;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
