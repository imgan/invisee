package com.nsi.repositories.core;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nsi.domain.core.UtProductFundPrices;
import com.nsi.domain.core.UtProducts;
import org.springframework.data.repository.query.Param;

public interface UtProductFundPricesRepository extends JpaRepository<UtProductFundPrices, Long>{

	public UtProductFundPrices findByUtProductsAndPriceDate(UtProducts utProducts, Date priceDate);

	@Query(value = "SELECT utfp.* FROM ut_product_fund_prices utfp " +
			"JOIN (SELECT MAX(price_date) AS price_date, products_id FROM ut_product_fund_prices WHERE price_date<=:priceDate and products_id=:productId GROUP BY products_id) fp ON (utfp.price_date=fp.price_date AND utfp.products_id=fp.products_id)",
			nativeQuery = true)
	public UtProductFundPrices findByUtProductsAndPriceDateWithCustomQuery(@Param("productId") Long productId, @Param("priceDate") Date priceDate);
	public List<UtProductFundPrices> findTop1ByUtProductsOrderByPriceDateDesc(UtProducts utProducts);

	public UtProductFundPrices findTop1ByUtProducts_idOrderByPriceDateDesc(Long id);
	
	@Query("select max(up.priceDate) from UtProductFundPrices up where up.utProducts=?1")
	public Date findByUtProductWithMaxPriceDateQuery(UtProducts utProducts);
        
	@Query("select up.bidPrice from UtProductFundPrices up where up.utProducts=?1 and up.priceDate=?2")
	public Double findByUtProductWithBidPriceQuery(UtProducts utProducts, Date date);

	@Query("select min(up.priceDate) from UtProductFundPrices up where up.utProducts=?1 and DATE(up.priceDate) >= DATE(?2)")
	public Date findByUtProductWithMaxPriceDateQuery(UtProducts utProducts, Date date);
	
	@Query("select min(up.priceDate) from UtProductFundPrices up where up.utProducts=?1")
	public Date findByUtProductWithMinPriceDateQuery(UtProducts utProducts);
	
	public List<UtProductFundPrices> findAllByUtProductsAndPriceDateGreaterThanEqualOrderByPriceDateAsc(UtProducts utProducts, Date priceDate);
	public List<UtProductFundPrices> findAllByUtProductsAndPriceDateLessThanEqualOrderByPriceDateDesc(UtProducts utProducts, Date priceDate);
}