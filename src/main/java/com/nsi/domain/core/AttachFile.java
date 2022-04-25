package com.nsi.domain.core;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="attach_file")
public class AttachFile {

	private Long id;
	private Integer version;
	private String tipeFile;
	private String namaFile;
	private String lokasiFile;
	private Long ukuranFile;
	private String key;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attach_file_generator")
	@SequenceGenerator(name="attach_file_generator", sequenceName = "attach_file_id_seq", allocationSize=1)
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
	
	@Column(name="tipe_file")
	public String getTipeFile() {
		return tipeFile;
	}
	public void setTipeFile(String tipeFile) {
		this.tipeFile = tipeFile;
	}
	
	@Column(name="nama_file")
	public String getNamaFile() {
		return namaFile;
	}
	public void setNamaFile(String namaFile) {
		this.namaFile = namaFile;
	}
	
	@Column(name="lokasi_file")
	public String getLokasiFile() {
		return lokasiFile;
	}
	public void setLokasiFile(String lokasiFile) {
		this.lokasiFile = lokasiFile;
	}
	
	@Column(name="ukuran_file")
	public Long getUkuranFile() {
		return ukuranFile;
	}
	public void setUkuranFile(Long ukuranFile) {
		this.ukuranFile = ukuranFile;
	}
	
	@Column(name="key", length=36)
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	
}
