package com.nsi.repositories.core;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.nsi.domain.core.UtProducts;
import com.nsi.domain.core.UtProductsSettlement;

public interface UtProductsSettlementRepository extends JpaRepository<UtProductsSettlement, Long> {
    public List<UtProductsSettlement> findAllByUtProduct(UtProducts utProducts);

    UtProductsSettlement findByUtProduct(UtProducts utProducts);
}
