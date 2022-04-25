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
@Table(name="fund_package_products")
public class FundPackageProducts extends BaseDomain {

	private Long id;
	private Double compositition;
	private FundPackages fundPackages;
	private UtProducts utProducts;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fund_package_products_generator")
	@SequenceGenerator(name="fund_package_products_generator", sequenceName = "fund_package_products_fund_package_product_id_seq", allocationSize=1)
	@Column(name="fund_package_product_id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="compositition")
	public Double getCompositition() {
		return compositition;
	}
	public void setCompositition(Double compositition) {
		this.compositition = compositition;
	}
	
	@ManyToOne
	@JoinColumn(name="fund_packages_id")
	public FundPackages getFundPackages() {
		return fundPackages;
	}
	public void setFundPackages(FundPackages fundPackages) {
		this.fundPackages = fundPackages;
	}
	
	@ManyToOne
	@JoinColumn(name="ut_products_id")
	public UtProducts getUtProducts() {
		return utProducts;
	}
	public void setUtProducts(UtProducts utProducts) {
		this.utProducts = utProducts;
	}

    @Override
    public String toString() {
        return "FundPackageProducts{" + "id=" + id + ", compositition=" + compositition + ", fundPackages=" + fundPackages + ", utProducts=" + utProducts + '}';
    }
	
	
}
