package com.nsi.services.impl;

import com.nsi.domain.core.*;
import com.nsi.repositories.core.*;
import com.nsi.services.AvantradeIntegrationService;
import com.nsi.util.ConstantUtil;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Service
public class AvantradeIntegrationServiceImpl extends BaseService implements AvantradeIntegrationService {
    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
    private final String DATA_SUCCESSFULLY_UPDATE = "2000012";
    @Autowired
    LookupLineRepository lookupLineRepository;
    @Autowired
    SettlementAccountsRepository settlementAccountsRepository;
    @Autowired
    GlobalParameterRepository globalParameterRepository;
    @Autowired
    CountriesRepository countriesRepository;
    @Autowired
    StatesRepository statesRepository;
    @Autowired
    CitiesRepository citiesRepository;

    public String updateCustomer(Kyc kyc) throws Exception {
        JSONObject jdata = new JSONObject();
        jdata.put("customerId", kyc.getAtCustomerId());
        jdata.put("portalCIF", kyc.getPortalcif());

        LookupLine uuidSal = lookupLineRepository.findByCategory_CategoryAndCode("SALUTATION", kyc.getSalutation());
        jdata.put("salutation", uuidSal == null ? "" : uuidSal.getAtLookupId());
        jdata.put("firstName", kyc.getFirstName() == null ? "" : kyc.getFirstName());
        jdata.put("middleName", kyc.getMiddleName() == null? "" : kyc.getMiddleName());

        if(kyc.getLastName() != null){
            jdata.put("lastName", kyc.getLastName());
        }

        jdata.put("gender", kyc.getGender().equals("ML") ? "-1" : "0");
        jdata.put("birthDate", kyc.getBirthDate() == null ? "" : sdf.format(kyc.getBirthDate()));
        jdata.put("birthPlace", kyc.getBirthPlace() == null ? "" : kyc.getBirthPlace());
        jdata.put("spouseName", kyc.getSpouseName() == null ? "" : kyc.getSpouseName());
        jdata.put("spouseDateOfBirth", kyc.getSpouseDateOfBirth() == null ? "" : sdf.format(kyc.getSpouseDateOfBirth()));
        LookupLine uuidMar = lookupLineRepository.findByCategory_CategoryAndCode("MARITAL_STATUS", kyc.getMaritalStatus());
        jdata.put("maritalStatus", uuidMar.getAtLookupId() == null ? "" : uuidMar.getAtLookupId());

        LookupLine uuidEdu = lookupLineRepository.findByCategory_CategoryAndCode("EDUCATION_BACKGROUND", kyc.getEducationBackground());
        jdata.put("educationalBackground", uuidEdu.getAtLookupId() == null ? "" : uuidEdu.getAtLookupId());

        LookupLine uuidRel = lookupLineRepository.findByCategory_CategoryAndCode("RELIGION", kyc.getReligion());
        jdata.put("religion",uuidRel.getAtLookupId() == null ? "" : uuidRel.getAtLookupId());

        jdata.put("motherMaidenName", kyc.getMotherMaidenName() == null ? "" : kyc.getMotherMaidenName());

        jdata.put("citizenship", kyc.getCitizenship().equals("DOM") ? "-1" : "0");

        jdata.put("riskProfile",kyc.getRiskProfile() == null ? "" :kyc.getRiskProfile().getAtScoreId());

        LookupLine uuidInv = lookupLineRepository.findByCategory_CategoryAndCode("INVESTMENT_PURPOSE", kyc.getInvestmentPurpose());
        jdata.put("investmentPurpose", uuidInv.getAtLookupId() == null ? "" : uuidInv.getAtLookupId());

        LookupLine uuidSrc = lookupLineRepository.findByCategory_CategoryAndCode("SOURCE_OF_INCOME", kyc.getSourceOfIncome());
        jdata.put("sourceOfIncome", uuidSrc.getAtLookupId() == null ? "" : uuidSrc.getAtLookupId());

        LookupLine uuidTot = lookupLineRepository.findByCategory_CategoryAndCode("ANNUAL_INCOME", kyc.getTotalIncomePa());
        jdata.put("totalIncomePerAnnum", uuidTot.getAtLookupId() == null ? "" : uuidTot.getAtLookupId());

        LookupLine uuidProf = lookupLineRepository.findByCategory_CategoryAndCode("OCCUPATION", kyc.getOccupation());
        jdata.put("profession",uuidProf == null ? "" : uuidProf.getAtLookupId());

        jdata.put("statementType", kyc.getPreferredMailingAddress());

        Countries nationality = countriesRepository.getOne(Long.valueOf(kyc.getNationality()));
        jdata.put("countryCode", kyc.getNationality() == null ? "" : nationality.getAtCountryCode());

        jdata.put("homeAddress", kyc.getHomeAddress() == null ? "" : kyc.getHomeAddress().replaceAll("\n", " "));
        Cities city = citiesRepository.getOne(Long.valueOf(kyc.getHomeCity()));
        jdata.put("homeAddressCity", city.getCityCode() == null ? "" : city.getCityCode());

        States state = statesRepository.findByStateCode(kyc.getHomeProvince());
        jdata.put("homeAddressState", state.getStateCode() == null ? "" : state.getStateCode());

        Countries country = countriesRepository.getOne(Long.valueOf(kyc.getHomeCountry()));
        jdata.put("homeAddressCountry", country.getAtCountryCode() == null ? "" : country.getAtCountryCode());

        jdata.put("homePostalCode", kyc.getHomePostalCode() == null ? "" : kyc.getHomePostalCode());

        jdata.put("legalAddress", kyc.getLegalAddress() == null ? "" : kyc.getLegalAddress().replaceAll("\n", " "));
        Cities legalCit = citiesRepository.getOne(Long.valueOf(kyc.getLegalCity()));
        jdata.put("legalAddressCity", legalCit.getCityCode() == null ? "" : legalCit.getCityCode());

        States legalStat = statesRepository.findByStateCode(kyc.getLegalProvince());
        jdata.put("legalAddressState", legalStat.getStateCode() == null ? "" : legalStat.getStateCode());

        Countries legalCoun = countriesRepository.getOne(Long.valueOf(kyc.getLegalCountry()));
        jdata.put("legalAddressCountry", legalCoun.getAtCountryCode() == null ? "" : legalCoun.getAtCountryCode());

        jdata.put("legalPostalCode", kyc.getLegalPostalCode() == null ? "" : kyc.getLegalPostalCode());

        jdata.put("officeAddress", kyc.getOfficeAddress() == null ? "" : kyc.getOfficeAddress().replaceAll("\n", " "));
        Cities offCity = citiesRepository.getOne(Long.valueOf(kyc.getOfficeCity()));
        jdata.put("officeAddressCity", offCity.getCityCode() == null ? "" : offCity.getCityCode());

        States offState = statesRepository.findByStateCode(kyc.getOfficeProvince());
        jdata.put("officeAddressState", offState.getStateCode() == null ? "" : offState.getStateCode());

        Countries offCountry = countriesRepository.getOne(Long.valueOf(kyc.getOfficeCountry()));
        jdata.put("officeAddressCountry", offCountry.getAtCountryCode() == null ? "" : offCountry.getAtCountryCode());

        jdata.put("officePostalCode", kyc.getOfficePostalCode() == null ? "" : kyc.getOfficePostalCode());

        jdata.put("homeContactNo", kyc.getHomePhoneNumber() == null ? "" : kyc.getHomePhoneNumber().replace("-", ""));
        jdata.put("officeContactNo", kyc.getOfficePhoneNumber() == null ? "" : kyc.getOfficePhoneNumber().replace("-", ""));
        jdata.put("officeFaxNo", kyc.getOfficeFaxNumber() == null ? "" : kyc.getOfficeFaxNumber().replace("-", ""));
        jdata.put("phoneNumber", kyc.getHomePhoneNumber() == null ? "" : kyc.getHomePhoneNumber().replace("-", ""));

        jdata.put("salesId", kyc.getAccount().getAgent() == null ? "" : kyc.getAccount().getAgent().getAvantradeSales());
        jdata.put("idNumber", kyc.getIdNumber() == null ? "" : kyc.getIdNumber());

        String nama = null;
        if(kyc.getFirstName() != null && !ConstantUtil.EMPTHY_STRING.equals(kyc.getFirstName().trim())){
            if(nama == null){
                nama = kyc.getFirstName();
            }
        }

        if(kyc.getMiddleName() != null && !ConstantUtil.EMPTHY_STRING.equals(kyc.getMiddleName().trim())){
            if(nama == null){
                nama = kyc.getMiddleName();
            }else{
                nama = nama.concat(" ").concat(kyc.getMiddleName());
            }
        }

        if(kyc.getLastName() != null && !ConstantUtil.EMPTHY_STRING.equals(kyc.getLastName().trim())){
            if(nama == null){
                nama = kyc.getLastName();
            }else{
                nama = nama.concat(" ").concat(kyc.getLastName());
            }
        }
        jdata.put("idName", nama);

        LookupLine uuidType = lookupLineRepository.findByCategory_CategoryAndCode("ID_TYPE", kyc.getIdType());
        jdata.put("idType", uuidType.getAtLookupId() == null ? "" : uuidType.getAtLookupId());
        jdata.put("idIssuedDate", kyc.getIdExpirationDate() == null ? "" : sdf.format(getIssuedDate(kyc.getIdExpirationDate())));
        jdata.put("idExpiredDate",kyc.getIdExpirationDate() == null ? "" : sdf.format(kyc.getIdExpirationDate()));
        jdata.put("emailAddress", kyc.getEmail() == null ? "" : kyc.getEmail());
        SettlementAccounts settlement = settlementAccountsRepository.findByKycs(kyc);
        jdata.put("bankName",settlement.getBankId() == null ? "" : settlement.getBankId().getAtBankId());
        jdata.put("bankBranch", kyc.getAccount().getAgent().getChannel().getAvantradeBranch());

        jdata.put("stlAccountNo", settlement.getSettlementAccountNo() == null ? "" : settlement.getSettlementAccountNo());
        jdata.put("stlAccountName",settlement.getSettlementAccountName() == null ? "" : settlement.getSettlementAccountName());
        jdata.put("stlAccountId",settlement.getAtSettlementAccountId() == null ? "" : settlement.getAtSettlementAccountId());
        jdata.put("taxIdNo", kyc.getTaxId() == null ? "" : kyc.getTaxId());

        logger.info("data update avantrade :"+jdata);
        RestTemplate restTemplate = new RestTemplate();
        GlobalParameter redirectUrl = globalParameterRepository.findByCategory("REDIRECT_URL_TO_AVANTRADE");
        String response = restTemplate.postForObject(redirectUrl.getValue() + "/services/customer/tes/UPD" , jdata, String.class);
        logger.info("response update avantrade :"+response);
        if(!DATA_SUCCESSFULLY_UPDATE.equals(response)){
            throw new Exception("AVANTRADE#"+response);
        }
        return response;
    }

    Date getIssuedDate(Date idExpirationDate){
        Date issuedDate = new Date();
        Calendar issueDateCal = Calendar.getInstance();
        issueDateCal.setTime(issuedDate);

        Calendar currentDateCal = Calendar.getInstance();
        currentDateCal.setTime(new Date());

        Calendar expiredDateCal = Calendar.getInstance();
        expiredDateCal.setTime(idExpirationDate);

        expiredDateCal.add(Calendar.YEAR,-5);
        issueDateCal.setTime(expiredDateCal.getTime());

        if(issueDateCal.compareTo(currentDateCal)>0){
            issueDateCal.setTime(currentDateCal.getTime());
        }

        return issueDateCal.getTime();
    }
}
