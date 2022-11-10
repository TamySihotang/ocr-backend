package com.danamon.vo;

import lombok.Data;

import java.util.Date;

@Data
public class DetailInformationVO {
    private int totalNoCashDepositWithoutRemark;
    private Double totalAmountCashDepositWithoutRemark;
    private int totalNoCashDepositWithRemark;
    private Double totalAmountCashDepositWithRemark;
    private int totalNoOfCashWithdrawals;
    private Double totalAmountOfCashWithdrawals;
    private int totalNoOfInwardChequeBounces;
    private int totalNoOfOutwardChequeBounces;
    private Double totalAmountOfSelfTransactionCr;
    private Double totalAmountOfSelfTransactionDr;
    private int totalNumberOfSelfTransactionCr;
    private int totalNumberOfSelfTransactionDr;
    private Double balance1st;
    private Double balance7th;
    private Double balance14th;
    private Double balance21th;
    private Double balancelastDayOfTheMonth;
    private String avgUtilization;
    private String numberOfDaysAccountIsOverDrawn;
    private String daysPastDueOrInterestPayDelay;
    private String grossNoCreditTransaction;
    private String grossAmountCreditTransaction;
    private String grossNoDebitTransaction;
    private String grossAmountDebitTransaction;
    private String totalNoNonBusinessCredits;
    private String totalAmountNonBusinessCredits;
    private String totalNoNonBusinessDebit;
    private String totalAmountNonBusinessDebit;
    private String totalNoNetBusinessCredits;
    private String totalAmountNetBusinessCredits;
    private String totalNoNetBusinessDebit;
    private String totalAmountNetBusinessDebit;
    private String highestEodBalanceOfTheMonth;
    private String lowestEodBalanceOfTheMonth;
    private String averageEodBalanceOfTheMonth;
    private String swingCalculation;
    private String totalAmountWindowDressingDebit;
    private String totalAmountWindowDressingCredit;
    private Date periode;
}
