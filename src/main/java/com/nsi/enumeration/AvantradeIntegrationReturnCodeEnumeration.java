package com.nsi.enumeration;

public enum AvantradeIntegrationReturnCodeEnumeration {

	INVALID_DATA_VERSION(2000001),
	VALID_DATA_VERSION(2000002),
	DATA_ALREADY_EXISTS(2000003),
	DATA_NOT_EXISTS(2000004),
	DATA_NOT_EXISTS_IN_PENDING_APPROVAL(2000005),
	DATA_ALREADY_EXISTS_IN_PENDING_APPROVAL(2000006),
	INVALID_REFERENTIAL_INTEGRITY(2000007),
	VALID_REFERENTIAL_INTEGRITY(2000008),
	DATA_FAILED_INSERT(2000009),
	DATA_SUCCESSFULLY_INSERT(2000010),
	DATA_FAILED_UPDATE(2000011),
	DATA_SUCCESSFULLY_UPDATE(2000012),
	DATA_FAILED_DELETE(2000013),
	DATA_SUCCESSFULLY_DELETE(2000014),
	DATA_FAILED_LOCK(2000015),
	DATA_SUCCESSFULLY_LOCK_INSERT(2000016),
	DATA_FAILED_UNLOCK(2000017),
	DATA_SUCCESSFULLY_UNLOCK(2000018),
	ERROR_DATABASE_ACCESS(2000019),
	ERROR_MANAGER(2000020),
	DATA_SUCCESSFULLY_SEND_TO_APPROVAL(2000021),
	DATA_FAIL_SEND_TO_APPROVAL(2000022),
	CODE_ALREADY_USED_IN_PENDING(2000023),
	CODE_AVAILABLE(2000024),
	DATA_STILL_REFERENCED(2000025),
	DATA_NOT_REFERENCED(2000026),
	MISSING_PARENT(2000027),
	PARENTS_COMPLETE(2000028),
	DATA_EMPTY(2000029),
	DATA_NOT_EMPTY(2000030),
	DATA_SUCCESSFULLY_LOCK_UPDATE(2000031),
	DATA_SUCCESSFULLY_LOCK_DELETE(2000032),
	OVERLAPING_DATA_DATE(2000057),
	//Transaction Return Code
	TRANSACTION_SUCCESSFULLY_INSERT(2000033),
	TRANSACTION_FAILED_INSERT(2000034),
	TRANSACTION_SUCCESSFULLY_SEND_TO_APPROVAL(2000035),
	TRANSACTION_FAIL_SEND_TO_APPROVAL(2000036),
	TRANSACTION_SUCCESSFULLY_APPROVED(2000037),
	TRANSACTION_FAILED_APPROVED(2000038),
	TRANSACTION_SUCCESSFULLY_REJECTED(2000039),
	TRANSACTION_FAILED_REJECTED(2000040),
	TRANSACTION_NOT_YET_SETTLED(2000041),
	TRANSACTION_ALREADY_SETTLED(2000042),
	TRANSACTION_SUCCESSFULLY_SETTLED(2000043),
	TRANSACTION_FAILED_SETTLED(2000044),
	SETTLEMENT_SUCCESSFULLY_APPROVED(2000045),
	SETTLEMENT_FAILED_APPROVED(2000046),
	TRANSACTION_SUCCESSFULLY_RESUBMIT(2000047);

	private Integer code;

	private AvantradeIntegrationReturnCodeEnumeration(Integer code){
		this.code=code;
	}

	public Integer getCode() {
		return code;
	}


}
