package com.nsi.domain.core;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "allowed_packages_agent")
public class AllowedPackagesAgent implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "allowed_packages_agent_gen")
  @SequenceGenerator(name = "allowed_packages_agent_gen", sequenceName = "allowed_packages_agent_seq", allocationSize = 1)
  @Column(columnDefinition = "serial")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "agent")
  private Agent agent;

  @ManyToOne
  @JoinColumn(name = "fund_package")
  private FundPackages packages;

  @Column(name="created_by")
  private String createdBy;

  @Column(name="created_date")
  private Date createdDate;

  @Column(name="updated_by")
  private String updatedBy;

  @Column(name="updated_date")
  private Date updatedDate;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Agent getAgent() {
    return agent;
  }

  public void setAgent(Agent agent) {
    this.agent = agent;
  }

  public FundPackages getPackages() {
    return packages;
  }

  public void setPackages(FundPackages packages) {
    this.packages = packages;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public Date getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  public String getUpdatedBy() {
    return updatedBy;
  }

  public void setUpdatedBy(String updatedBy) {
    this.updatedBy = updatedBy;
  }

  public Date getUpdatedDate() {
    return updatedDate;
  }

  public void setUpdatedDate(Date updatedDate) {
    this.updatedDate = updatedDate;
  }
}
