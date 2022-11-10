package com.danamon.enums;

public enum CustomerInformationEnum implements BaseEnum<String>{
    NAME("Name"),
    ADDRESS("Address"),
    ACCOUNT_NUMBER("Account Number"),
    BANK_NAME("Bank Name"),
    BANK_ACCOUNT_TYPE("Bank Account Type"),
    CURRENCY("Currency"),
    LIMIT_OVERDRAFT("Limit Overdraft"),
    IS_BANK_STATEMENT_HIT_FRAUD_INDICATOR("Is Bank Statement hit fraud indicator"),
    TOTAL_FRAUD_HIT("Total Fraud Hit"),
    HYPERLINK_TO_FRAUD_INDICATOR("Hyperlink to Fraud Indicator"),
    IS_BANK_STATEMENT_HIT_BEHAVIOUR_INDICATOR("Is Bank Statement hit behaviour indicator"),
    TOTAL_BEHAVIOUR_HIT("Total behaviour hit"),
    HYPERLINK_TO_BEHAVIOUR_INDICATOR("Hyperlink to behaviour indicator"),
    MONTH_OF_BANK_STATEMENT("Month of bank statement"),
    DESCRIPTION("Description");
    private final String value;
    CustomerInformationEnum(String value) {
        this.value = value;
    }

    @Override
    public String getInternalValue() {
        return value;
    }

}
