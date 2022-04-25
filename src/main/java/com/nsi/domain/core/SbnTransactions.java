package com.nsi.domain.core;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "sbn_transactions")
public class SbnTransactions {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sbn_transactions_id_generator")
    @SequenceGenerator(name="sbn_transactions_id_generator", sequenceName = "sbn_transactions_id_seq", allocationSize=1)
    private Long id;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @Column(name = "updated_date")
    private Date updatedDate;

    @Column(name = "id_sid")
    private Long idSid;

    @Column(name = "id_rekening_dana")
    private Long idRekeningDana;

    @Column(name = "id_seri")
    private Long idSeri;

    @Column(name = "id_rekening_sb")
    private Long idRekeningSb;

    @Column(name = "trx_amount")
    private Long trxAmount;

    @Column(name = "batas_waktu_bayar")
    private Date batasWaktuBayar;

    @Column(name = "id_status")
    private Long idStatus;

    @Column(name = "status_desc", length = 50)
    private String statusDesc;

    @Column(name = "ntpn", length = 50)
    private String ntpn;

    @Column(name = "ntb")
    private String ntb;

    @Column(name = "kode_billing")
    private String kodeBilling;

    @Column(name = "sisa_kepemilikan")
    private Long sisaKepemilikan;

    @Column(name = "redeemable_amount")
    private Double redeemableAmount;

    @Column(name = "kode_pemesanan", length = 50)
    private String kodePemesanan;

    @Column(name = "pop")
    private String pop;

    @ManyToOne
    @JoinColumn(name = "sbn_sid_id")
    private SbnSid sbnSid;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getIdSid() {
        return idSid;
    }

    public void setIdSid(Long idSid) {
        this.idSid = idSid;
    }

    public Long getIdRekeningDana() {
        return idRekeningDana;
    }

    public void setIdRekeningDana(Long idRekeningDana) {
        this.idRekeningDana = idRekeningDana;
    }

    public Long getIdSeri() {
        return idSeri;
    }

    public void setIdSeri(Long idSeri) {
        this.idSeri = idSeri;
    }

    public Long getIdRekeningSb() {
        return idRekeningSb;
    }

    public void setIdRekeningSb(Long idRekeningSb) {
        this.idRekeningSb = idRekeningSb;
    }

    public Long getTrxAmount() {
        return trxAmount;
    }

    public void setTrxAmount(Long trxAmount) {
        this.trxAmount = trxAmount;
    }

    public Date getBatasWaktuBayar() {
        return batasWaktuBayar;
    }

    public void setBatasWaktuBayar(Date batasWaktuBayar) {
        this.batasWaktuBayar = batasWaktuBayar;
    }

    public Long getIdStatus() {
        return idStatus;
    }

    public void setIdStatus(Long idStatus) {
        this.idStatus = idStatus;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    public String getNtpn() {
        return ntpn;
    }

    public void setNtpn(String ntpn) {
        this.ntpn = ntpn;
    }

    public String getNtb() {
        return ntb;
    }

    public void setNtb(String ntb) {
        this.ntb = ntb;
    }

    public String getKodeBilling() {
        return kodeBilling;
    }

    public void setKodeBilling(String kodeBilling) {
        this.kodeBilling = kodeBilling;
    }

    public Long getSisaKepemilikan() {
        return sisaKepemilikan;
    }

    public void setSisaKepemilikan(Long sisaKepemilikan) {
        this.sisaKepemilikan = sisaKepemilikan;
    }

    public Double getRedeemableAmount() {
        return redeemableAmount;
    }

    public void setRedeemableAmount(Double redeemableAmount) {
        this.redeemableAmount = redeemableAmount;
    }

    public String getKodePemesanan() {
        return kodePemesanan;
    }

    public void setKodePemesanan(String kodePemesanan) {
        this.kodePemesanan = kodePemesanan;
    }

    public String getPop() {
        return pop;
    }

    public void setPop(String pop) {
        this.pop = pop;
    }

    public SbnSid getSbnSid() {
        return sbnSid;
    }

    public void setSbnSid(SbnSid sbnSid) {
        this.sbnSid = sbnSid;
    }
}
