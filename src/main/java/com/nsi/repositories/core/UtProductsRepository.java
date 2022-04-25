package com.nsi.repositories.core;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.nsi.domain.core.UtProducts;

public interface UtProductsRepository extends JpaRepository<UtProducts, Long> {
    public List<UtProducts> findAllByProductType(String productType);
}
