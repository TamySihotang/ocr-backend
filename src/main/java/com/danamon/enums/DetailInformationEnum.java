package com.danamon.enums;

public enum DetailInformationEnum implements BaseEnum<String>{
    TOTAL_NO_CASH_DEPOSIT_WITHOUT_REMARK("Total No. of Cash Deposits(without remarks)"),
    TOTAL_AMOUNT_CASH_DEPOSIT_WITHOUT_REMARK("Total Amount of Cash Deposits (without remarks)"),
    TOTAL_NO_CASH_DEPOSIT_WITH_REMARK("Total No. of Cash Deposits (with remarks)"),
    TOTAL_AMOUNT_CASH_DEPOSIT_WITH_REMARK("Total Amount of Cash Deposits(with remarks)"),
    TOTAL_NO_OF_CASH_WITHDRAWALS("Total No. of Cash Withdrawals"),
    TOTAL_AMOUNT_OF_CASH_WITHDRAWALS("Total Amount of Cash Withdrawals"),
    TOTAL_NO_OF_INWARD_CHEQUE_BOUNCES("Total No. of Inward Cheque Bounces"),
    TOTAL_NO_OF_OUTWARD_CHEQUE_BOUNCES("Total No. of Outward Cheque Bounces"),
    TOTAL_AMOUNT_OF_SELF_TRANSACTION_CR("Total Amount of Self/Sister Transaction (Cr)"),
    TOTAL_AMOUNT_OF_SELF_TRANSACTION_DR("Total Amount of Self/Sister Transaction (Dr)"),
    TOTAL_NUMBER_OF_SELF_TRANSACTION_CR("Total Number of Self/Sister Transaction (Cr)"),
    TOTAL_NUMBER_OF_SELF_TRANSACTION_DR("Total Number of Self/Sister Transaction (Dr)"),
    BALANCE_1ST("Balance on 1st"),
    BALANCE_7TH("Balance on 7th"),
    BALANCE_14TH("Balance on 14th"),
    BALANCE_21TH("Balance on 21st"),
    BALANCE_LAST_DAY_OF_THE_MONTH("Balance on Last day of the month"),
    AVG_UTILIZATION("Avg Utilization as %age of Limit"),
    NUMBER_OF_DAYS_ACCOUNT_IS_OVERDRAWN("Number of days Account is Overdrawn"),
    DAYS_PAST_DUE_OR_INTEREST_PAY_DELAY("Days Past Due or Interest Pay Delay"),
    GROSS_NO_CREDIT_TRANSACTION("Gross No. of Credit Transactions"),
    GROSS_AMOUNT_CREDIT_TRANSACTION("Gross Amount of Credit Transactions"),
    GROSS_NO_DEBIT_TRANSACTION("Gross No. of Debit Transactions"),
    GROSS_AMOUNT_DEBIT_TRANSACTION("Gross Amount of Debit Transactions"),
    TOTAL_NO_NON_BUSINESS_CREDITS("Total no. of non-business credits(exclusions)"),
    TOTAL_AMOUNT_NON_BUSINESS_CREDITS("Total amount of non-business credits(exclusions)"),
    TOTAL_NO_NON_BUSINESS_DEBIT("Total no. of non-business debits(exclusions)"),
    TOTAL_AMOUNT_NON_BUSINESS_DEBIT("Total amount of non-business debits(exclusions)"),
    TOTAL_NO_NET_BUSINESS_CREDITS("Total No. of Net Business credits"),
    TOTAL_AMOUNT_NET_BUSINESS_CREDITS("Amount of Net Business credits"),
    TOTAL_NO_NET_BUSINESS_DEBIT("Total No. of Net Business debits"),
    TOTAL_AMOUNT_NET_BUSINESS_DEBIT("Amount of Net Business debits"),
    HIGHEST_EOD_BALANCE_OF_THE_MONTH("Highest EOD Balance of the Month"),
    LOWEST_EOD_BALANCE_OF_THE_MONTH("Lowest EOD Balance of the Month"),
    AVERAGE_EOD_BALANCE_OF_THE_MONTH("Average EOD Balance of the Month"),
    SWING_CALCULATION("Swing Calculation"),
    TOTAL_AMOUNT_WINDOW_DRESSING_DEBIT("Total Amount of Window dressing Debits"),
    TOTAL_AMOUNT_WINDOW_DRESSING_CREDIT("Total Amount of Window dressing Credits");
    private final String value;
    DetailInformationEnum(String value) {
        this.value = value;
    }

    @Override
    public String getInternalValue() {
        return value;
    }
}
