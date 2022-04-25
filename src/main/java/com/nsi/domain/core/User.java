package com.nsi.domain.core;

import java.util.Date;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.nsi.util.TokenGenerator;

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
@Table(name = "_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_SEQ")
    @SequenceGenerator(sequenceName = "_user_id_seq", allocationSize = 1, name = "USER_SEQ")
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "password_temp")
    private String passwordTemp;

    @Column(name = "enabled")
    private boolean enabled = true;

    @Column(name = "email")
    private String email;
    
    @Column(name = "security_level")
    private String securityLevel;

    @Column(name = "user_status")
    private String userStatus;

    @Column(name = "user_status_sebelumnya")
    private String userStatusSebelumnya;

    @Column(name = "account_expired")
    private boolean accountExpired;

    @Column(name = "account_locked")
    private boolean accountLocked;

    @Column(name = "password_expired")
    private boolean passwordExpired;

    @Column(name = "token")
    private String token;

    @Column(name = "pass_issued_date")
    private Date passIssuedDate;

    @Column(name = "pass_salt", length = 100)
    private String passSalt;

    @Column(name = "user_type")
    private Boolean userType;

    @Column(name = "role_code", length = 50)
    private String roleCode;

    @Column(name = "current_session_id")
    private String currentSessionId;

    @Column(name = "login_failure_count")
    private Integer loginFailureCount;

    @Column(name = "last_login")
    private Date lastLogin;

    @Column(name = "record_login")
    private Date recordLogin;

    @Column(name = "stat", length = 1)
    private String stat;

    @Column(name = "pin")
    private String pin;

    @Column(name = "reset_code")
    private String resetCode;

    @Column(name = "approval_status")
    private Boolean approvalStatus;

    @Column(name = "user_picture_key")
    private String userPictureKey;

    @Column(name = "officer_name")
    private String officerName;

    @Column(name = "officer_address")
    private String officerAddress;

    @Column(name = "officer_phone")
    private String officerPhone;

    @Column(name = "card_number", length = 50)
    private String cardNumber;

    @Column(name = "password_payment")
    private String passwordPayment;

    @Column(name = "customer_key")
    private String customerKey;

    @ManyToOne
    @JoinColumn(name = "agent_id")
    private Agent agent;

    @Column(name = "channel_customer")
    private String channelCustomer;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_date")
    private Date updatedDate;

    @Column(name = "is_process")
    private Boolean isProcess;

    @Column(name = "is_sbn_customer")
    Boolean isSbnCustomer;

    @Column(name = "is_sbn_customer_process")
    Boolean isSbnCustomerProcess;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        this.password = passwordEncoder.encode(password);
        //this.password = password;
    }

    public String getPasswordTemp() {
        return passwordTemp;
    }

    public void setPasswordTemp(String passwordTemp) {
        this.passwordTemp = passwordTemp;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(String securityLevel) {
        this.securityLevel = securityLevel;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getUserStatusSebelumnya() {
        return userStatusSebelumnya;
    }

    public void setUserStatusSebelumnya(String userStatusSebelumnya) {
        this.userStatusSebelumnya = userStatusSebelumnya;
    }

    public boolean isAccountExpired() {
        return accountExpired;
    }

    public void setAccountExpired(boolean accountExpired) {
        this.accountExpired = accountExpired;
    }

    public boolean isAccountLocked() {
        return accountLocked;
    }

    public void setAccountLocked(boolean accountLocked) {
        this.accountLocked = accountLocked;
    }

    public boolean isPasswordExpired() {
        return passwordExpired;
    }

    public void setPasswordExpired(boolean passwordExpired) {
        this.passwordExpired = passwordExpired;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getPassIssuedDate() {
        return passIssuedDate;
    }

    public void setPassIssuedDate(Date passIssuedDate) {
        this.passIssuedDate = passIssuedDate;
    }

    public String getPassSalt() {
        return passSalt;
    }

    public void setPassSalt(String passSalt) {
        this.passSalt = passSalt;
    }

    public Boolean getUserType() {
        return userType;
    }

    public void setUserType(Boolean userType) {
        this.userType = userType;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getCurrentSessionId() {
        return currentSessionId;
    }

    public void setCurrentSessionId(String currentSessionId) {
        this.currentSessionId = currentSessionId;
    }

    public Integer getLoginFailureCount() {
        return loginFailureCount;
    }

    public void setLoginFailureCount(Integer loginFailureCount) {
        this.loginFailureCount = loginFailureCount;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Date getRecordLogin() {
        return recordLogin;
    }

    public void setRecordLogin(Date recordLogin) {
        this.recordLogin = recordLogin;
    }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getResetCode() {
        return resetCode;
    }

    public void setResetCode(String resetCode) {
        this.resetCode = resetCode;
    }

    public Boolean getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(Boolean approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public String getUserPictureKey() {
        return userPictureKey;
    }

    public void setUserPictureKey(String userPictureKey) {
        this.userPictureKey = userPictureKey;
    }

    public String getOfficerName() {
        return officerName;
    }

    public void setOfficerName(String officerName) {
        this.officerName = officerName;
    }

    public String getOfficerAddress() {
        return officerAddress;
    }

    public void setOfficerAddress(String officerAddress) {
        this.officerAddress = officerAddress;
    }

    public String getOfficerPhone() {
        return officerPhone;
    }

    public void setOfficerPhone(String officerPhone) {
        this.officerPhone = officerPhone;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getPasswordPayment() {
        return passwordPayment;
    }

    public void setPasswordPayment(String passwordPayment) {
        this.passwordPayment = passwordPayment;
    }

    public String getCustomerKey() {
        return customerKey;
    }

    public void setCustomerKey(String customerKey) {
        this.customerKey = customerKey;
    }

    public String getChannelCustomer() {
        return channelCustomer;
    }

    public void setChannelCustomer(String channelCustomer) {
        this.channelCustomer = channelCustomer;
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
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

    public void setIsProcess(Boolean isProcess) {
        this.isProcess = isProcess;
    }

    public Boolean getIsProcess() {
        return isProcess;
    }

    public Boolean getIsSbnCustomer() {
        return isSbnCustomer;
    }

    public void setIsSbnCustomer(Boolean sbnCustomer) {
        isSbnCustomer = sbnCustomer;
    }

    public Boolean getIsSbnCustomerProcess() {
        return isSbnCustomerProcess;
    }

    public void setIsSbnCustomerProcess(Boolean sbnCustomerProcess) {
        isSbnCustomerProcess = sbnCustomerProcess;
    }

    public String generateNewToken(String salt) {
        String shownToken = TokenGenerator.generateToken();
        this.token = TokenGenerator.hash(shownToken, salt);
        System.out.println("Token hash :" + this.token);
        return shownToken;
    }
}
