package com.nsi.domain.core;

import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "kyc")
public class Kyc {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "KYC_SEQ")
  @SequenceGenerator(sequenceName = "kyc_kyt_id_seq", allocationSize = 1, name = "KYC_SEQ")
  @Column(name = "customer_id")
  private Long id;

  @Column(name = "salutation")
  private String salutation;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "middle_name")
  private String middleName;

  @Column(name = "last_name")
  private String lastName;

  @Column(name = "birth_date")
  private Date birthDate;

  @Column(name = "birth_place")
  private String birthPlace;

  @Column(name = "gender")
  private String gender;

  @Column(name = "citizenship")
  private String citizenship;

  @Column(name = "nationality")
  private String nationality;

  @Column(name = "marital_status")
  private String maritalStatus;

  @Column(name = "spouse_name")
  private String spouseName;

  @Column(name = "mother_maiden_name")
  private String motherMaidenName;

  @Column(name = "education_background")
  private String educationBackground;

  @Column(name = "religion")
  private String religion;

  @Column(name = "investment_purpose")
  private String investmentPurpose;

  @Column(name = "source_of_income")
  private String sourceOfIncome;

  @Column(name = "total_income_pa")
  private String totalIncomePa;

  @Column(name = "referral")
  private String referral;

  @Column(name = "referral_name")
  private String referralName;

  @Column(name = "occupation")
  private String occupation;

  @Column(name = "employer_name")
  private String employerName;

  @Column(name = "nature_of_business")
  private String natureOfBusiness;

  @Column(name = "preferred_mailing_address")
  private String preferredMailingAddress;

  @Column(name = "id_number")
  private String idNumber;

  @Column(name = "id_type")
  private String idType;

  @Column(name = "id_expiration_date")
  private Date idExpirationDate;

  @Column(name = "tax_id")
  private String taxId;

  @Column(name = "branch_name")
  private String branchName;

  @Column(name = "home_address")
  private String homeAddress;

  @Column(name = "home_city")
  private String homeCity;

  @Column(name = "home_province")
  private String homeProvince;

  @Column(name = "home_country")
  private String homeCountry;

  @Column(name = "home_postal_code")
  private String homePostalCode;

  @Column(name = "home_phone_number")
  private String homePhoneNumber;

  @Column(name = "mobile_number")
  private String mobileNumber;

  @Column(name = "home_fax_number")
  private String homeFaxNumber;

  @Column(name = "legal_address")
  private String legalAddress;

  @Column(name = "legal_city")
  private String legalCity;

  @Column(name = "legal_province")
  private String legalProvince;

  @Column(name = "legal_country")
  private String legalCountry;

  @Column(name = "legal_postal_code")
  private String legalPostalCode;

  @Column(name = "legal_phone_number")
  private String legalPhoneNumber;

  @Column(name = "legal_fax_number")
  private String legalFaxNumber;

  @Column(name = "office_address")
  private String officeAddress;

  @Column(name = "office_city")
  private String officeCity;

  @Column(name = "office_province")
  private String officeProvince;

  @Column(name = "office_country")
  private String officeCountry;

  @Column(name = "office_postal_code")
  private String officePostalCode;

  @Column(name = "office_phone_number")
  private String officePhoneNumber;

  @Column(name = "office_fax_number")
  private String officeFaxNumber;

  @Column(name = "email")
  private String email;

  @Column(name = "job_title")
  private String jobTitle;

  @Column(name = "employement_status")
  private String employementStatus;

  @Column(name = "employment_date")
  private Date employmentDate;

  @Column(name = "profession")
  private String profession;

  @ManyToOne
  @JoinColumn(name = "risk_profile_id")
  private Score riskProfile;

  @Column(name = "spouse_date_of_birth")
  private Date spouseDateOfBirth;

  @Column(name = "status")
  private String status;

  @Column(name = "tax_no")
  private String taxNo;

  @Column(name = "user_accounts")
  private String userAccounts;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "account_id")
  private User account;

  @ManyToOne
  @JoinColumn(name = "question_id")
  private Question question;

  @Column(name = "portalcif")
  private String portalcif;

  @Column(name = "beneficiary_name")
  private String beneficiaryName;

  @Column(name = "beneficiary_relationship")
  private String beneficiaryRelationship;

  @Column(name = "at_customer_id")
  private String atCustomerId;

  @Lob
  @Type(type = "org.hibernate.type.TextType")
  @Column(name = "old_value_kyc")
  private String oldValueKyc;

  @Lob
  @Type(type = "org.hibernate.type.TextType")
  @Column(name = "old_value_fatca")
  private String oldValueFatca;

  @Lob
  @Type(type = "org.hibernate.type.TextType")
  @Column(name = "old_value_risk_profile")
  private String oldValueRiskProfile;

  @Column(name = "tax_id_regis_date")
  private Date taxIdRegisDate;

  @Column(name = "investment_experience")
  private String investmentExperience; //code in LookupLine

  @Column(name = "other_investment_experience")
  private String otherInvestmentExperience;

  @Column(name = "total_asset")
  private String totalAsset; //code in LookupLine

  @Column(name = "pep_name")
  private String pepName;

  @Column(name = "pep_position")
  private String pepPosition;

  @Column(name = "pep_public_function")
  private String pepPublicFunction;

  @ManyToOne
  @JoinColumn(name = "pep_country_id")
  private Countries pepCountry;

  @Column(name = "pep_year_of_service")
  private String pepYearOfService;

  @Column(name = "pep_relationship")
  private String pepRelationship; //code in LookupLine

  @Column(name = "pep_other")
  private String pepOther;

  @ManyToOne
  @JoinColumn(name = "sales_id")
  private Sales sales;

  @Column(name = "flag_email")
  private boolean flagEmail = false;

  @Column(name = "flag_phone_number")
  private boolean flagPhoneNumber = false;

  @Column(name = "referral_code")
  private String referralCode;

  @ManyToOne
  @JoinColumn(name = "referral_cus_id")
  private Kyc referralCus;

  @Column(name = "is_need_help")
  private boolean isNeedHelp = false;

  @Column(name = "request_fill_date")
  private Date requestFillDate;

  @Column(name = "request_fill_time")
  private Date requestFillTime;
  
  @Column(name = "sid")
  private String sid;

  @Column(name="created_date")
  private Date createdDate;

  @Column(name="created_by", length=50)
  private String createdBy;

  @Column(name="updated_date")
  private Date updatedDate;

  @Column(name="updated_by", length=50)
  private String updatedBy;

  @Column(name="no_kmiln")
  private String noKmiln;

  @Column(name="issue_date_kmiln")
  private Date issueDateKmiln;

  public void setId(Long id) {
    this.id = id;
  }

  public Long getId() {
    return id;
  }

  public String getSalutation() {
    return salutation;
  }

  public void setSalutation(String salutation) {
    this.salutation = salutation;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getMiddleName() {
    return middleName;
  }

  public void setMiddleName(String middleName) {
    this.middleName = middleName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public Date getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(Date birthDate) {
    this.birthDate = birthDate;
  }

  public String getBirthPlace() {
    return birthPlace;
  }

  public void setBirthPlace(String birthPlace) {
    this.birthPlace = birthPlace;
  }

  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public String getCitizenship() {
    return citizenship;
  }

  public void setCitizenship(String citizenship) {
    this.citizenship = citizenship;
  }

  public String getNationality() {
    return nationality;
  }

  public void setNationality(String nationality) {
    this.nationality = nationality;
  }

  public String getMaritalStatus() {
    return maritalStatus;
  }

  public void setMaritalStatus(String maritalStatus) {
    this.maritalStatus = maritalStatus;
  }

  public String getSpouseName() {
    return spouseName;
  }

  public void setSpouseName(String spouseName) {
    this.spouseName = spouseName;
  }

  public String getMotherMaidenName() {
    return motherMaidenName;
  }

  public void setMotherMaidenName(String motherMaidenName) {
    this.motherMaidenName = motherMaidenName;
  }

  public String getEducationBackground() {
    return educationBackground;
  }

  public void setEducationBackground(String educationBackground) {
    this.educationBackground = educationBackground;
  }

  public String getReligion() {
    return religion;
  }

  public void setReligion(String religion) {
    this.religion = religion;
  }

  public String getInvestmentPurpose() {
    return investmentPurpose;
  }

  public void setInvestmentPurpose(String investmentPurpose) {
    this.investmentPurpose = investmentPurpose;
  }

  public String getSourceOfIncome() {
    return sourceOfIncome;
  }

  public void setSourceOfIncome(String sourceOfIncome) {
    this.sourceOfIncome = sourceOfIncome;
  }

  public String getTotalIncomePa() {
    return totalIncomePa;
  }

  public void setTotalIncomePa(String totalIncomePa) {
    this.totalIncomePa = totalIncomePa;
  }

  public String getReferral() {
    return referral;
  }

  public void setReferral(String referral) {
    this.referral = referral;
  }

  public String getReferralName() {
    return referralName;
  }

  public void setReferralName(String referralName) {
    this.referralName = referralName;
  }

  public String getOccupation() {
    return occupation;
  }

  public void setOccupation(String occupation) {
    this.occupation = occupation;
  }

  public String getEmployerName() {
    return employerName;
  }

  public void setEmployerName(String employerName) {
    this.employerName = employerName;
  }

  public String getNatureOfBusiness() {
    return natureOfBusiness;
  }

  public void setNatureOfBusiness(String natureOfBusiness) {
    this.natureOfBusiness = natureOfBusiness;
  }

  public String getPreferredMailingAddress() {
    return preferredMailingAddress;
  }

  public void setPreferredMailingAddress(String preferredMailingAddress) {
    this.preferredMailingAddress = preferredMailingAddress;
  }

  public String getIdNumber() {
    return idNumber;
  }

  public void setIdNumber(String idNumber) {
    this.idNumber = idNumber;
  }

  public String getIdType() {
    return idType;
  }

  public void setIdType(String idType) {
    this.idType = idType;
  }

  public Date getIdExpirationDate() {
    return idExpirationDate;
  }

  public void setIdExpirationDate(Date idExpirationDate) {
    this.idExpirationDate = idExpirationDate;
  }

  public String getTaxId() {
    return taxId;
  }

  public void setTaxId(String taxId) {
    this.taxId = taxId;
  }

  public String getBranchName() {
    return branchName;
  }

  public void setBranchName(String branchName) {
    this.branchName = branchName;
  }

  public String getHomeAddress() {
    return homeAddress;
  }

  public void setHomeAddress(String homeAddress) {
    this.homeAddress = homeAddress;
  }

  public String getHomeCity() {
    return homeCity;
  }

  public void setHomeCity(String homeCity) {
    this.homeCity = homeCity;
  }

  public String getHomeProvince() {
    return homeProvince;
  }

  public void setHomeProvince(String homeProvince) {
    this.homeProvince = homeProvince;
  }

  public String getHomeCountry() {
    return homeCountry;
  }

  public void setHomeCountry(String homeCountry) {
    this.homeCountry = homeCountry;
  }

  public String getHomePostalCode() {
    return homePostalCode;
  }

  public void setHomePostalCode(String homePostalCode) {
    this.homePostalCode = homePostalCode;
  }

  public String getHomePhoneNumber() {
    return homePhoneNumber;
  }

  public void setHomePhoneNumber(String homePhoneNumber) {
    this.homePhoneNumber = homePhoneNumber;
  }

  public String getMobileNumber() {
    return mobileNumber;
  }

  public void setMobileNumber(String mobileNumber) {
    this.mobileNumber = mobileNumber;
  }

  public String getHomeFaxNumber() {
    return homeFaxNumber;
  }

  public void setHomeFaxNumber(String homeFaxNumber) {
    this.homeFaxNumber = homeFaxNumber;
  }

  public String getLegalAddress() {
    return legalAddress;
  }

  public void setLegalAddress(String legalAddress) {
    this.legalAddress = legalAddress;
  }

  public String getLegalCity() {
    return legalCity;
  }

  public void setLegalCity(String legalCity) {
    this.legalCity = legalCity;
  }

  public String getLegalProvince() {
    return legalProvince;
  }

  public void setLegalProvince(String legalProvince) {
    this.legalProvince = legalProvince;
  }

  public String getLegalCountry() {
    return legalCountry;
  }

  public void setLegalCountry(String legalCountry) {
    this.legalCountry = legalCountry;
  }

  public String getLegalPostalCode() {
    return legalPostalCode;
  }

  public void setLegalPostalCode(String legalPostalCode) {
    this.legalPostalCode = legalPostalCode;
  }

  public String getLegalPhoneNumber() {
    return legalPhoneNumber;
  }

  public void setLegalPhoneNumber(String legalPhoneNumber) {
    this.legalPhoneNumber = legalPhoneNumber;
  }

  public String getLegalFaxNumber() {
    return legalFaxNumber;
  }

  public void setLegalFaxNumber(String legalFaxNumber) {
    this.legalFaxNumber = legalFaxNumber;
  }

  public String getOfficeAddress() {
    return officeAddress;
  }

  public void setOfficeAddress(String officeAddress) {
    this.officeAddress = officeAddress;
  }

  public String getOfficeCity() {
    return officeCity;
  }

  public void setOfficeCity(String officeCity) {
    this.officeCity = officeCity;
  }

  public String getOfficeProvince() {
    return officeProvince;
  }

  public void setOfficeProvince(String officeProvince) {
    this.officeProvince = officeProvince;
  }

  public String getOfficeCountry() {
    return officeCountry;
  }

  public void setOfficeCountry(String officeCountry) {
    this.officeCountry = officeCountry;
  }

  public String getOfficePostalCode() {
    return officePostalCode;
  }

  public void setOfficePostalCode(String officePostalCode) {
    this.officePostalCode = officePostalCode;
  }

  public String getOfficePhoneNumber() {
    return officePhoneNumber;
  }

  public void setOfficePhoneNumber(String officePhoneNumber) {
    this.officePhoneNumber = officePhoneNumber;
  }

  public String getOfficeFaxNumber() {
    return officeFaxNumber;
  }

  public void setOfficeFaxNumber(String officeFaxNumber) {
    this.officeFaxNumber = officeFaxNumber;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getJobTitle() {
    return jobTitle;
  }

  public void setJobTitle(String jobTitle) {
    this.jobTitle = jobTitle;
  }

  public String getEmployementStatus() {
    return employementStatus;
  }

  public void setEmployementStatus(String employementStatus) {
    this.employementStatus = employementStatus;
  }

  public Date getEmploymentDate() {
    return employmentDate;
  }

  public void setEmploymentDate(Date employmentDate) {
    this.employmentDate = employmentDate;
  }

  public String getProfession() {
    return profession;
  }

  public void setProfession(String profession) {
    this.profession = profession;
  }

  public Score getRiskProfile() {
    return riskProfile;
  }

  public void setRiskProfile(Score riskProfile) {
    this.riskProfile = riskProfile;
  }

  public Date getSpouseDateOfBirth() {
    return spouseDateOfBirth;
  }

  public void setSpouseDateOfBirth(Date spouseDateOfBirth) {
    this.spouseDateOfBirth = spouseDateOfBirth;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getTaxNo() {
    return taxNo;
  }

  public void setTaxNo(String taxNo) {
    this.taxNo = taxNo;
  }

  public String getUserAccounts() {
    return userAccounts;
  }

  public void setUserAccounts(String userAccounts) {
    this.userAccounts = userAccounts;
  }

  public User getAccount() {
    return account;
  }

  public void setAccount(User account) {
    this.account = account;
  }

  public Question getQuestion() {
    return question;
  }

  public void setQuestion(Question question) {
    this.question = question;
  }

  public String getPortalcif() {
    return portalcif;
  }

  public void setPortalcif(String portalcif) {
    this.portalcif = portalcif;
  }

  public String getBeneficiaryName() {
    return beneficiaryName;
  }

  public void setBeneficiaryName(String beneficiaryName) {
    this.beneficiaryName = beneficiaryName;
  }

  public String getBeneficiaryRelationship() {
    return beneficiaryRelationship;
  }

  public void setBeneficiaryRelationship(String beneficiaryRelationship) {
    this.beneficiaryRelationship = beneficiaryRelationship;
  }

  public String getAtCustomerId() {
    return atCustomerId;
  }

  public void setAtCustomerId(String atCustomerId) {
    this.atCustomerId = atCustomerId;
  }

  public String getOldValueKyc() {
    return oldValueKyc;
  }

  public void setOldValueKyc(String oldValueKyc) {
    this.oldValueKyc = oldValueKyc;
  }

  public String getOldValueFatca() {
    return oldValueFatca;
  }

  public void setOldValueFatca(String oldValueFatca) {
    this.oldValueFatca = oldValueFatca;
  }

  public String getOldValueRiskProfile() {
    return oldValueRiskProfile;
  }

  public void setOldValueRiskProfile(String oldValueRiskProfile) {
    this.oldValueRiskProfile = oldValueRiskProfile;
  }

  public Date getTaxIdRegisDate() {
    return taxIdRegisDate;
  }

  public void setTaxIdRegisDate(Date taxIdRegisDate) {
    this.taxIdRegisDate = taxIdRegisDate;
  }

  public String getInvestmentExperience() {
    return investmentExperience;
  }

  public void setInvestmentExperience(String investmentExperience) {
    this.investmentExperience = investmentExperience;
  }

  public String getOtherInvestmentExperience() {
    return otherInvestmentExperience;
  }

  public void setOtherInvestmentExperience(String otherInvestmentExperience) {
    this.otherInvestmentExperience = otherInvestmentExperience;
  }

  public String getTotalAsset() {
    return totalAsset;
  }

  public void setTotalAsset(String totalAsset) {
    this.totalAsset = totalAsset;
  }

  public String getPepName() {
    return pepName;
  }

  public void setPepName(String pepName) {
    this.pepName = pepName;
  }

  public String getPepPosition() {
    return pepPosition;
  }

  public void setPepPosition(String pepPosition) {
    this.pepPosition = pepPosition;
  }

  public String getPepPublicFunction() {
    return pepPublicFunction;
  }

  public void setPepPublicFunction(String pepPublicFunction) {
    this.pepPublicFunction = pepPublicFunction;
  }

  public Countries getPepCountry() {
    return pepCountry;
  }

  public void setPepCountry(Countries pepCountry) {
    this.pepCountry = pepCountry;
  }

  public String getPepYearOfService() {
    return pepYearOfService;
  }

  public void setPepYearOfService(String pepYearOfService) {
    this.pepYearOfService = pepYearOfService;
  }

  public String getPepRelationship() {
    return pepRelationship;
  }

  public void setPepRelationship(String pepRelationship) {
    this.pepRelationship = pepRelationship;
  }

  public String getPepOther() {
    return pepOther;
  }

  public void setPepOther(String pepOther) {
    this.pepOther = pepOther;
  }

  public Sales getSales() {
    return sales;
  }

  public void setSales(Sales sales) {
    this.sales = sales;
  }

  public boolean isFlagEmail() {
    return flagEmail;
  }

  public void setFlagEmail(boolean flagEmail) {
    this.flagEmail = flagEmail;
  }

  public boolean isFlagPhoneNumber() {
    return flagPhoneNumber;
  }

  public void setFlagPhoneNumber(boolean flagPhoneNumber) {
    this.flagPhoneNumber = flagPhoneNumber;
  }

  public String getReferralCode() {
    return referralCode;
  }

  public void setReferralCode(String referralCode) {
    this.referralCode = referralCode;
  }

  public Kyc getReferralCus() {
    return referralCus;
  }

  public void setReferralCus(Kyc referralCus) {
    this.referralCus = referralCus;
  }

  public boolean isNeedHelp() {
    return isNeedHelp;
  }

  public void setNeedHelp(boolean needHelp) {
    isNeedHelp = needHelp;
  }

  public Date getRequestFillDate() {
    return requestFillDate;
  }

  public void setRequestFillDate(Date requestFillDate) {
    this.requestFillDate = requestFillDate;
  }

  public Date getRequestFillTime() {
    return requestFillTime;
  }

  public void setRequestFillTime(Date requestFillTime) {
    this.requestFillTime = requestFillTime;
  }
  
  public String getSid() {return sid;}
  public void setSid(String sid) {this.sid = sid;}

  public Date getCreatedDate() {
    return createdDate;
  }
  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  public String getCreatedBy() {
    return createdBy;
  }
  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public Date getUpdatedDate() {
    return updatedDate;
  }
  public void setUpdatedDate(Date updatedDate) {
    this.updatedDate = updatedDate;
  }

  public String getUpdatedBy() {
    return updatedBy;
  }
  public void setUpdatedBy(String updatedBy) {
    this.updatedBy = updatedBy;
  }

  public String getNoKmiln() {
    return noKmiln;
  }

  public void setNoKmiln(String noKmiln) {
    this.noKmiln = noKmiln;
  }

  public Date getIssueDateKmiln() {
    return issueDateKmiln;
  }

  public void setIssueDateKmiln(Date issueDateKmiln) {
    this.issueDateKmiln = issueDateKmiln;
  }

  @Override
  public String toString() {
    return "Kyc{" +
            "id=" + id +
            ", salutation='" + salutation + '\'' +
            ", firstName='" + firstName + '\'' +
            ", middleName='" + middleName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", birthDate=" + birthDate +
            ", birthPlace='" + birthPlace + '\'' +
            ", gender='" + gender + '\'' +
            ", citizenship='" + citizenship + '\'' +
            ", nationality='" + nationality + '\'' +
            ", maritalStatus='" + maritalStatus + '\'' +
            ", spouseName='" + spouseName + '\'' +
            ", motherMaidenName='" + motherMaidenName + '\'' +
            ", educationBackground='" + educationBackground + '\'' +
            ", religion='" + religion + '\'' +
            ", investmentPurpose='" + investmentPurpose + '\'' +
            ", sourceOfIncome='" + sourceOfIncome + '\'' +
            ", totalIncomePa='" + totalIncomePa + '\'' +
            ", referral='" + referral + '\'' +
            ", referralName='" + referralName + '\'' +
            ", occupation='" + occupation + '\'' +
            ", employerName='" + employerName + '\'' +
            ", natureOfBusiness='" + natureOfBusiness + '\'' +
            ", preferredMailingAddress='" + preferredMailingAddress + '\'' +
            ", idNumber='" + idNumber + '\'' +
            ", idType='" + idType + '\'' +
            ", idExpirationDate=" + idExpirationDate +
            ", taxId='" + taxId + '\'' +
            ", branchName='" + branchName + '\'' +
            ", homeAddress='" + homeAddress + '\'' +
            ", homeCity='" + homeCity + '\'' +
            ", homeProvince='" + homeProvince + '\'' +
            ", homeCountry='" + homeCountry + '\'' +
            ", homePostalCode='" + homePostalCode + '\'' +
            ", homePhoneNumber='" + homePhoneNumber + '\'' +
            ", mobileNumber='" + mobileNumber + '\'' +
            ", homeFaxNumber='" + homeFaxNumber + '\'' +
            ", legalAddress='" + legalAddress + '\'' +
            ", legalCity='" + legalCity + '\'' +
            ", legalProvince='" + legalProvince + '\'' +
            ", legalCountry='" + legalCountry + '\'' +
            ", legalPostalCode='" + legalPostalCode + '\'' +
            ", legalPhoneNumber='" + legalPhoneNumber + '\'' +
            ", legalFaxNumber='" + legalFaxNumber + '\'' +
            ", officeAddress='" + officeAddress + '\'' +
            ", officeCity='" + officeCity + '\'' +
            ", officeProvince='" + officeProvince + '\'' +
            ", officeCountry='" + officeCountry + '\'' +
            ", officePostalCode='" + officePostalCode + '\'' +
            ", officePhoneNumber='" + officePhoneNumber + '\'' +
            ", officeFaxNumber='" + officeFaxNumber + '\'' +
            ", email='" + email + '\'' +
            ", jobTitle='" + jobTitle + '\'' +
            ", employementStatus='" + employementStatus + '\'' +
            ", employmentDate=" + employmentDate +
            ", profession='" + profession + '\'' +
            ", riskProfile=" + riskProfile +
            ", spouseDateOfBirth=" + spouseDateOfBirth +
            ", status='" + status + '\'' +
            ", taxNo='" + taxNo + '\'' +
            ", userAccounts='" + userAccounts + '\'' +
            ", account=" + account +
            ", question=" + question +
            ", portalcif='" + portalcif + '\'' +
            ", beneficiaryName='" + beneficiaryName + '\'' +
            ", beneficiaryRelationship='" + beneficiaryRelationship + '\'' +
            ", atCustomerId='" + atCustomerId + '\'' +
            ", oldValueKyc='" + oldValueKyc + '\'' +
            ", oldValueFatca='" + oldValueFatca + '\'' +
            ", oldValueRiskProfile='" + oldValueRiskProfile + '\'' +
            ", taxIdRegisDate=" + taxIdRegisDate +
            ", investmentExperience='" + investmentExperience + '\'' +
            ", otherInvestmentExperience='" + otherInvestmentExperience + '\'' +
            ", totalAsset='" + totalAsset + '\'' +
            ", pepName='" + pepName + '\'' +
            ", pepPosition='" + pepPosition + '\'' +
            ", pepPublicFunction='" + pepPublicFunction + '\'' +
            ", pepCountry=" + pepCountry +
            ", pepYearOfService='" + pepYearOfService + '\'' +
            ", pepRelationship='" + pepRelationship + '\'' +
            ", pepOther='" + pepOther + '\'' +
            ", sales=" + sales +
            ", flagEmail=" + flagEmail +
            ", flagPhoneNumber=" + flagPhoneNumber +
            ", referralCode='" + referralCode + '\'' +
            ", referralCus=" + referralCus +
            ", isNeedHelp=" + isNeedHelp +
            ", requestFillDate=" + requestFillDate +
            ", requestFillTime=" + requestFillTime +
            ", sid='" + sid + '\'' +
            ", createdDate=" + createdDate +
            ", createdBy='" + createdBy + '\'' +
            ", updatedDate=" + updatedDate +
            ", updatedBy='" + updatedBy + '\'' +
            ", noKmiln='" + noKmiln + '\'' +
            ", issueDateKmiln=" + issueDateKmiln +
            '}';
  }


}
