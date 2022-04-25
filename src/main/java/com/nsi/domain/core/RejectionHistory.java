package com.nsi.domain.core;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "rejection_history")
public class RejectionHistory implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "created_by_id")
  private User createdById;

  @Column(name = "created_on")
  private Date createdOn;

  @Column(columnDefinition = "text")
  private String note;

  @ManyToOne
  @JoinColumn(name = "rejected_user_id", nullable = false)
  private User rejectedUserId;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public User getCreatedById() {
    return createdById;
  }

  public void setCreatedById(User createdById) {
    this.createdById = createdById;
  }

  public Date getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(Date createdOn) {
    this.createdOn = createdOn;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public User getRejectedUserId() {
    return rejectedUserId;
  }

  public void setRejectedUserId(User rejectedUserId) {
    this.rejectedUserId = rejectedUserId;
  }
}
