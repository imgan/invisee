package com.nsi.services.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nsi.domain.core.FundPackageFeeSetup;
import com.nsi.domain.core.FundPackages;
import com.nsi.domain.core.Holiday;
import com.nsi.domain.core.InvestmentAccounts;
import com.nsi.domain.core.Kyc;
import com.nsi.domain.core.User;
import com.nsi.domain.core.UtTransactionType;
import com.nsi.repositories.core.FundPackageFeeSetupRepository;
import com.nsi.repositories.core.HolidayRepository;
import com.nsi.repositories.core.InvestmentAccountsRepository;
import com.nsi.repositories.core.LookupHeaderRepository;
import com.nsi.repositories.core.LookupLineRepository;
import com.nsi.repositories.core.UserRepository;
import com.nsi.repositories.core.UtTransactionsCartRepository;
import com.nsi.repositories.core.UtTransactionsGroupRepository;
import com.nsi.repositories.core.UtTransactionsRepository;
import com.nsi.services.GlobalService;
import com.nsi.util.DateTimeUtil;

@Service
public class GlobalServiceImpl implements GlobalService {

    @Autowired
    LookupHeaderRepository lookupHeaderService;
    @Autowired
    LookupLineRepository lookupLineService;
    @Autowired
    UserRepository userService;
    @Autowired
    UtTransactionsCartRepository utTransactionCartService;
    @Autowired
    FundPackageFeeSetupRepository fundPackageFeeSetupService;
    @Autowired
    InvestmentAccountsRepository investmentAccountsService;
    @Autowired
    HolidayRepository holidayService;
    @Autowired
    UtTransactionsRepository utTransactionsService;
    @Autowired
    UtTransactionsGroupRepository utTransactionsGroupRepository;
    @Autowired
    private InvestmentAccountsRepository investmentAccountsRepository;
    @Autowired
    UtTransactionsRepository utTransactionsRepository;


    private int tokenTTLMillis = 72000000;
    private int tokenTimeoutMillis = 18000000;
    private int tokenTimePrecisionMillis = 2400000;

    @Override
    public Boolean checkpinValid(Map map, User user) {
        List<Map> x = (List<Map>) map.get("pin");
        String indeks = "";
        String pins = "";
        Boolean result = true;
        for (Map m : x) {
            indeks = indeks + "" + String.valueOf(m.get("index"));
            pins = pins + "" + String.valueOf(m.get("value"));
        }

        if (user != null) {
            String[] getpin = pins.split("");
            String[] ins = indeks.split("");

            for (Integer i = 1; i < getpin.length; i++) {
                if (!String.valueOf(user.getPin().charAt(Integer.parseInt(ins[i]))).equals(String.valueOf(getpin[i]))) {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public Map checkToken(String token, HttpServletRequest request) {
        Map resultMap = new HashMap();
        if (token == "") {
            resultMap.put("code", 100);
            resultMap.put("info", "Invalid token");
            return resultMap;
        }

        System.out.println(request.getRemoteAddr());
        return resultMap;

    }

    @Override
    public String generateOrderNoTransfer(int counting, String vaCode) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        String pref = vaCode + sdf.format(new Date());
        String seq = String.format("%05d", utTransactionsRepository.getNextSeriesId("orderno_newseq"));
        String orderNo = pref.concat(seq);

        return orderNo;
    }

    @Override
    public Double getFeeAmountTransCart(List<FundPackageFeeSetup> feeSetups, Double orderAmount) {
        Double feeAmount = new Double(0);
        if (feeSetups.size() > 1) {
            for (FundPackageFeeSetup feeSetup : feeSetups) {
                if (feeSetup.getAmountMax() >= orderAmount && feeSetup.getAmountMin() < orderAmount) {
                    feeAmount = feeSetup.getFeeAmount() * orderAmount;
                } else if (feeSetup.getAmountMin() < orderAmount && feeSetup.getAmountMax() == 0) {
                    feeAmount = feeSetup.getFeeAmount() * orderAmount;
                }
            }
        } else {
            feeAmount = feeSetups.get(0).getFeeAmount() * orderAmount;
        }
        return feeAmount;
    }

    @Override
    public InvestmentAccounts saveInvestmentAccount(FundPackages fundPackages, Kyc kyc) {
        InvestmentAccounts investmentAccounts = new InvestmentAccounts();
        String name = "";
        if (kyc.getMiddleName() == "") {
            name = kyc.getFirstName() + " " + kyc.getLastName();
        } else {
            name = kyc.getFirstName() + " " + kyc.getMiddleName() + " " + kyc.getLastName();
        }
        investmentAccounts.setInvestmentAccountNo(this.generateInvestmentNo(1));
        investmentAccounts.setInvestmentAccountName(name);
        investmentAccounts.setKycs(kyc);
        investmentAccounts.setCreatedBy(kyc.getEmail());
        investmentAccounts.setCreatedDate(new Date());
        investmentAccounts.setFundPackages(fundPackages);
        investmentAccounts.setAtInvestmentAccountId(UUID.randomUUID().toString());
        investmentAccounts = investmentAccountsService.save(investmentAccounts);
        return investmentAccounts;
    }

    @Override
    public String generateInvestmentNo(int counting) {
        SimpleDateFormat sdf = new SimpleDateFormat("YYMM");
        String pref = "I" + sdf.format(new Date());
        Long seq = investmentAccountsRepository.getNextSeriesId("seq_investment_account");
        String n = String.format("%06d", seq);
        return pref.concat(n);
    }

    @Override
    public Boolean checkOverTransactionCutOffOrNot(Date transactionCutOff, Date currentTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.S");
        String tCutOff = sdf.format(transactionCutOff);

        Calendar calcur = Calendar.getInstance();
        calcur.setTime(currentTime);

        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(sdf.parse(tCutOff));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cal.set(Calendar.YEAR, calcur.get(Calendar.YEAR));
        cal.set(Calendar.MONTH, calcur.get(Calendar.MONTH));
        cal.set(Calendar.DAY_OF_MONTH, calcur.get(Calendar.DAY_OF_MONTH));

        Boolean isHoliday = this.isHoliday(currentTime);
        if (calcur.getTime().before(cal.getTime()) && !isHoliday) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public Boolean isHoliday(Date currentDate) {
        List<Holiday> holiday = holidayService.find(currentDate);
        if (holiday == null || holiday.isEmpty()) {
            Boolean result = false;
            Calendar cal = Calendar.getInstance();
            cal.setTime(currentDate);

            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            switch (dayOfWeek) {
                case Calendar.SATURDAY:
                    result = true;
                    break;
                case Calendar.SUNDAY:
                    result = true;
                    break;
                default:
                    break;
            }

            return result;
        } else {
            return true;
        }
    }

    @Override
    public String generateTrxNo(UtTransactionType type, int counting) {
        String trxno = null;
        SimpleDateFormat sdf = new SimpleDateFormat("YYMMdd");
        String strType = type.getTrxName().substring(0, 1);
        String pref = strType + "" + sdf.format(new Date());
        if ("S".equalsIgnoreCase(strType)) {
            String seq = String.format("%010d", utTransactionsRepository.getNextSeriesId("seq_trx_no_subs"));
            trxno = pref.concat(seq);
        } else if ("T".equalsIgnoreCase(strType)) {
            String seq = String.format("%010d", utTransactionsRepository.getNextSeriesId("seq_trx_no_topup"));
            trxno = pref.concat(seq);
        } else if ("R".equalsIgnoreCase(strType)) {
            String seq = String.format("%010d", utTransactionsRepository.getNextSeriesId("seq_trx_no_redemp"));
            trxno = pref.concat(seq);
        } else {
            String n = "000000000000000000" + (utTransactionsService.countByTrxNoLike(pref + "%") + counting);
            trxno = pref + n.substring(n.length() - 10);
        }

        return trxno;
    }

    @Override
    public String generateOrderNo(Double amount, String channelName) throws InterruptedException {
        String format = "O" + new SimpleDateFormat("yyMMdd").format(new Date());
        String seq = String.format("%05d", utTransactionsRepository.getNextSeriesId("orderno_newseq"));
        String orderNo = format.concat(seq);

        return orderNo;
    }

    @Override
    public Date getWorkingDate(Date date) {
        boolean isHoliday = true;
        while (isHoliday) {            
            if(!isBankHoliday(date)) {
                List<Holiday> holidays = holidayService.find(date);
                if(holidays == null || holidays.isEmpty()) isHoliday = false;
            } 
            
            if(isHoliday) date = DateTimeUtil.getCustomDate(date, 1);
        }
        return date;
    }

    public Date getPrevWorkingDay(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int dow = cal.get(Calendar.DAY_OF_WEEK);
        while (dow == Calendar.SATURDAY || dow == Calendar.SUNDAY || holidayService.find(DateTimeUtil.clearTime(cal.getTime())).size() > 0){
            cal.add(Calendar.DATE, -1);
            dow = cal.get(Calendar.DAY_OF_WEEK);
        }

        return DateTimeUtil.clearTime(cal.getTime());
    }
    
    private boolean isBankHoliday(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        int x  = c.get(Calendar.DAY_OF_WEEK);
        return (x == Calendar.SATURDAY || x == Calendar.SUNDAY);
    }
    
    /*
    
        public Date getNextWorkingDate(Date updatedDate) {
        List<Date> holidays = holidayService.getHolidayDate();
        List<String> holidayStr = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (Date day : holidays) {
            holidayStr.add(sdf.format(day));
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(updatedDate);

        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeek) {
            case Calendar.FRIDAY:
                cal.add(Calendar.DATE, 3);
                while (holidayStr.contains(sdf.format(cal.getTime()))) {
                    cal.add(Calendar.DATE, 1);
                }
                break;
            case Calendar.SATURDAY:
                cal.add(Calendar.DATE, 2);
                while (holidayStr.contains(sdf.format(cal.getTime()))) {
                    cal.add(Calendar.DATE, 1);
                }
                break;
            case Calendar.SUNDAY:
                cal.add(Calendar.DATE, 1);
                while (holidayStr.contains(sdf.format(cal.getTime()))) {
                    cal.add(Calendar.DATE, 1);
                }
                break;
            default:
                cal.add(Calendar.DATE, 1);
                ;
                while (holidayStr.contains(sdf.format(cal.getTime()))) {
                    int dayOfWeek2 = cal.get(Calendar.DAY_OF_WEEK);
                    switch (dayOfWeek2) {
                        case Calendar.FRIDAY:
                            cal.add(Calendar.DATE, 3);
                            break;
                        case Calendar.SATURDAY:
                            cal.add(Calendar.DATE, 2);
                            break;
                        case Calendar.SUNDAY:
                            cal.add(Calendar.DATE, 1);
                            break;
                        default:
                            cal.add(Calendar.DATE, 1);
                            break;
                    }
                }
                break;
        }
        return cal.getTime();
    }
    
    */

    @Override
    public Date getNextWorkingDate(Date updatedDate) {
        return getWorkingDate(updatedDate);
    }

}
