package com.nsi.domain.core;

import org.hibernate.annotations.Type;
import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "sbn_packages")
public class SbnPackages {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sbn_packages_id_generator")
    @SequenceGenerator(name="sbn_packages_id_generator", sequenceName = "sbn_packages_id_seq", allocationSize=1)
    private Long id;

    @Column(name = "created_date")
    private Date createdDate = new Date();

    @Column(name = "created_by")
    private String createdBy = "SYSTEM";

    @Column(name = "updated_date")
    private Date updatedDate;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "activated")
    private Boolean activated;

    @Column(name = "deleted")
    private Boolean deleted = false;

    @Column(name = "package_name")
    private String packageName;

    @Column(name = "package_code")
    private String packageCode;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "package_desc")
    private String packageDesc;

    @Column(name = "picture")
    private String picture;

    @Column(name = "effective_date")
    private Date effectiveDate;

    @Column(name = "currency")
    private String currency;

    @Column(name = "period_start")
    private Date periodStart;

    @Column(name = "period_end")
    private Date periodEnd;

    @Column(name = "minimum_transaction")
    private Double minimumTransaction;

    @Column(name = "maximum_transaction")
    private Double maximumTransaction;

    @Column(name = "subs_fee")
    private Long subsFee;

    @Column(name = "redeem_fee")
    private Long redeemFee;

    @Column(name = "coupon_rate")
    private Double couponRate;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "coupon_desc")
    private String couponDesc;

    @Column(name = "content")
    private String content;

    @Column(name = "id_seri")
    private Long idSeri;

    @Column(name = "batas_bawah_kupon")
    private Long batasBawahKupon;

    @Column(name = "batas_atas_kupon")
    private Long batasAtasKupon;

    @Column(name = "jenis_kupon")
    private String jenisKupon;

    @Column(name = "tgl_bayar_kupon")
    private String tglBayarKupon;

    @Column(name = "tgl_setelmen")
    private Date tglSetelmen;

    @Column(name = "tgl_jatuh_tempo")
    private Date tglJatuhTempo;

    @Column(name = "kelipatan_pemesanan")
    private Long kelipatanPemesanan;

    @Column(name = "early_start_redemption")
    private Date earlyStartRedemption;

    @Column(name = "early_end_redemption")
    private Date earlyEndRedemption;

    @Column(name = "early_redeemable_percentage")
    private Double earlyRedeemablePercentage;

    @Column(name = "settlement_date")
    private Date settlementDate;

    @Column(name = "quota_date")
    private Date quotaDate;

    @Column(name = "due_date")
    private Date dueDate;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "due_date_text")
    private String dueDateText;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Boolean getActivated() {
        return activated;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public String getPackageDesc() {
        return packageDesc;
    }

    public void setPackageDesc(String packageDesc) {
        this.packageDesc = packageDesc;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Date getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(Date periodStart) {
        this.periodStart = periodStart;
    }

    public Date getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(Date periodEnd) {
        this.periodEnd = periodEnd;
    }

    public Double getMinimumTransaction() {
        return minimumTransaction;
    }

    public void setMinimumTransaction(Double minimumTransaction) {
        this.minimumTransaction = minimumTransaction;
    }

    public Double getMaximumTransaction() {
        return maximumTransaction;
    }

    public void setMaximumTransaction(Double maximumTransaction) {
        this.maximumTransaction = maximumTransaction;
    }

    public Long getSubsFee() {
        return subsFee;
    }

    public void setSubsFee(Long subsFee) {
        this.subsFee = subsFee;
    }

    public Long getRedeemFee() {
        return redeemFee;
    }

    public void setRedeemFee(Long redeemFee) {
        this.redeemFee = redeemFee;
    }

    public Double getCouponRate() {
        return couponRate;
    }

    public void setCouponRate(Double couponRate) {
        this.couponRate = couponRate;
    }

    public String getCouponDesc() {
        return couponDesc;
    }

    public void setCouponDesc(String couponDesc) {
        this.couponDesc = couponDesc;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getIdSeri() {
        return idSeri;
    }

    public void setIdSeri(Long idSeri) {
        this.idSeri = idSeri;
    }

    public Long getBatasBawahKupon() {
        return batasBawahKupon;
    }

    public void setBatasBawahKupon(Long batasBawahKupon) {
        this.batasBawahKupon = batasBawahKupon;
    }

    public Long getBatasAtasKupon() {
        return batasAtasKupon;
    }

    public void setBatasAtasKupon(Long batasAtasKupon) {
        this.batasAtasKupon = batasAtasKupon;
    }

    public String getJenisKupon() {
        return jenisKupon;
    }

    public void setJenisKupon(String jenisKupon) {
        this.jenisKupon = jenisKupon;
    }

    public String getTglBayarKupon() {
        return tglBayarKupon;
    }

    public void setTglBayarKupon(String tglBayarKupon) {
        this.tglBayarKupon = tglBayarKupon;
    }

    public Date getTglSetelmen() {
        return tglSetelmen;
    }

    public void setTglSetelmen(Date tglSetelmen) {
        this.tglSetelmen = tglSetelmen;
    }

    public Date getTglJatuhTempo() {
        return tglJatuhTempo;
    }

    public void setTglJatuhTempo(Date tglJatuhTempo) {
        this.tglJatuhTempo = tglJatuhTempo;
    }

    public Long getKelipatanPemesanan() {
        return kelipatanPemesanan;
    }

    public void setKelipatanPemesanan(Long kelipatanPemesanan) {
        this.kelipatanPemesanan = kelipatanPemesanan;
    }

    public Date getEarlyStartRedemption() {
        return earlyStartRedemption;
    }

    public void setEarlyStartRedemption(Date earlyStartRedemption) {
        this.earlyStartRedemption = earlyStartRedemption;
    }

    public Date getEarlyEndRedemption() {
        return earlyEndRedemption;
    }

    public void setEarlyEndRedemption(Date earlyEndRedemption) {
        this.earlyEndRedemption = earlyEndRedemption;
    }

    public Double getEarlyRedeemablePercentage() {
        return earlyRedeemablePercentage;
    }

    public void setEarlyRedeemablePercentage(Double earlyRedeemablePercentage) {
        this.earlyRedeemablePercentage = earlyRedeemablePercentage;
    }

    public Date getSettlementDate() {
        return settlementDate;
    }

    public void setSettlementDate(Date settlementDate) {
        this.settlementDate = settlementDate;
    }

    public Date getQuotaDate() {
        return quotaDate;
    }

    public void setQuotaDate(Date quotaDate) {
        this.quotaDate = quotaDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getDueDateText() {
        return dueDateText;
    }

    public void setDueDateText(String dueDateText) {
        this.dueDateText = dueDateText;
    }
}
