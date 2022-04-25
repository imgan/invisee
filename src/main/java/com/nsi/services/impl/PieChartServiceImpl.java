/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nsi.services.impl;

import com.nsi.domain.core.Kyc;
import com.nsi.domain.core.User;
import com.nsi.repositories.core.KycRepository;
import com.nsi.services.PieChartService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author andri.gunawan
 */
@Service
public class PieChartServiceImpl implements PieChartService {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    KycRepository kycRepository;

    private Logger logger = Logger.getLogger(this.getClass());
    
    public Map valuepieChartMap(User user) {

        Kyc kyc = kycRepository.findByAccount(user);
        List<Map> resultPie = new ArrayList<>();

        String piequery = "SELECT "
                + " SUM(cust_bal_table.current_amount) AS marketValue, "
                + " ut_products.product_type AS product_type, "
                + " lookup_line.value AS fund_type, "
                + " lookup_line.description AS description "
                + "FROM "
                + " customer_balance AS cust_bal_table "
                + "LEFT JOIN "
                + " ("
                + " SELECT MIN(price_date) AS price_date, invAccountID "
                + "FROM "
                + " ( "
                + "     SELECT MAX(customer_balance.balance_date) AS price_date, (customer_balance.inv_account_id) AS invAccountID, customer_balance.ut_product_id "
                + "     FROM customer_balance "
                + "        WHERE customer_balance.customer_id = " + kyc.getId()
                + " GROUP BY inv_account_id, customer_balance.ut_product_id "
                + " ) AS tbl_max "
                + " GROUP BY invAccountID "
                + ") AS PD_table "
                + "ON cust_bal_table.inv_account_id = PD_table.invAccountID "
                + "LEFT JOIN "
                + " investment_accounts "
                + " ON investment_accounts.investment_account_id = cust_bal_table.inv_account_id "
                + "LEFT JOIN "
                + "            ut_products "
                + " ON cust_bal_table.ut_product_id=ut_products.product_id "
                + "LEFT JOIN "
                + "            lookup_line "
                + " ON cast(lookup_line.lookup_id as text)=ut_products.product_type "
                + "WHERE "
                + "cust_bal_table.balance_date = PD_table.price_date "
                + " AND cust_bal_table.customer_id = " + kyc.getId()
                + " AND cust_bal_table.inv_account_id = invAccountID "
                + "AND cust_bal_table.current_amount > 0 "
                + "GROUP BY ut_products.product_type, lookup_line.description,lookup_line.value "
                + "ORDER BY ut_products.product_type ASC";
//        logger.info(piequery);
        
        Query query = entityManager.createNativeQuery(piequery);
        List<Object[]> objects = query.getResultList();
        List<Map> details = new ArrayList<>();

        Double total = 0.0;
        for (Object[] objSub : objects) {
            total += (Double) objSub[0];
        }

        for (Object[] objSub : objects) {
            Double persentage = 0.0;
            if (total != 0) {
                Double val = (Double) objSub[0];
                persentage = val / total * 100;

                System.out.println("##val : " + val);
                System.out.println("##total : " + total);
                System.out.println("##persentage : " + persentage);
            }

            Map pieChart = new HashMap<>();
            pieChart.put("market_value", objSub[0]);
            pieChart.put("product_type", objSub[1]);
            pieChart.put("fund_type", objSub[2]);
            pieChart.put("description", objSub[3]);
            pieChart.put("percent", persentage);
            resultPie.add(pieChart);
        }

        Map pieChart = new HashMap<>();
        pieChart.put("piechart", resultPie);
        Map result = new HashMap<>();
        result.put("code", 0);
        if (resultPie.isEmpty()) {
            result.put("info", "piechart successfully loaded, but data is empty");
            result.put("data", null);
        } else {
            result.put("info", "piechart list successfully loaded");
            result.put("data", pieChart);
        }

        return result;
    }

}
