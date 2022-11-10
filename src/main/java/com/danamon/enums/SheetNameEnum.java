package com.danamon.enums;

public enum SheetNameEnum implements BaseEnum<String>{
    SUMMARY_OF_BS_ANALYSIS("Summary of BS Analysis"),
    WINDOW_DRESSING("Window Dressing"),
    TRANSACTIOM_REKENING("Transaction rekening1"),
    EOD_BALANCE("EOD Balances1"),
    LOAN_TRACK("Loan Track1"),
    TOP_20_FUND("Top 20 Funds1"),
    BOUNCED_PENAL_XNS("Bounced-Penal Xns"),
    RECURRING_CREDIT("Recurring Credit"),
    RECURRING_DEBIT("Recurring Debit"),
    TOP_20_FUND_CONSLD("Top 20 Funds Consld"),
    STATEMENTS_CONSIDERED("Statements Considered"),
    FCU_INDICATOR("FCU Indicators 1"),
    FCU_IRREGULAR("FCU Irregular Xns 1");
    private final String value;
    SheetNameEnum(String value) {
        this.value = value;
    }

    @Override
    public String getInternalValue() {
        return value;
    }
}
