package com.danamon.service.impl;

import com.danamon.enums.CustomerInformationEnum;
import com.danamon.enums.DetailInformationEnum;
import com.danamon.persistence.domain.Company;
import com.danamon.persistence.domain.CustomerInformation;
import com.danamon.persistence.domain.TransactionDetail;
import com.danamon.persistence.domain.TransactionHeader;
import com.danamon.persistence.repository.CompanyRepository;
import com.danamon.persistence.repository.TransactionDetailRepository;
import com.danamon.service.GenerateFileExcelService;
import com.danamon.utils.DateUtil;
import com.danamon.vo.DetailInformationVO;
import lombok.extern.slf4j.Slf4j;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.Span;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class GenerateFileExcelServiceImpl extends AbstractBaseService implements GenerateFileExcelService {
    private final TransactionDetailRepository transactionDetailRepository;
    private final CompanyRepository companyRepository;
    private static final String SETORAN = "SETORAN";
    private static final String SETORAN_TUNAI = "SETORAN TUNAI";
    private static final String SETORAN_VIA = "SETORAN VIA";
    private static final String TARIKAN = "TARIKAN";
    private static final String TOLAKAN_KLIRING = "TOLAKAN KLIRING";
    private static final String BIAYA_ADM = "BIAYA ADM";
    private static final String CR_KOREKSI_BUNGA = "CR KOREKSI BUNGA";
    private static final String BUNGA = "BUNGA";
    private static final String PAJAK_BUNGA = "PAJAK BUNGA";
    private static final String SALDO_AWAL = "SALDO AWAL";

    @Autowired
    public GenerateFileExcelServiceImpl(TransactionDetailRepository transactionDetailRepository,
                                        CompanyRepository companyRepository) {
        this.transactionDetailRepository = transactionDetailRepository;
        this.companyRepository = companyRepository;
    }

    @Override
    public void generateSheetSummaryAnalysisValue(List<TransactionHeader> transactionHeaders, Sheet sheet, Workbook workbook, List<TransactionDetail> transactionDetailList) {
        generateInformation(transactionHeaders.get(0).getCustomerInformation(), sheet);
        generateDetailInformation(transactionHeaders, sheet, workbook, transactionDetailList);
    }

    private void generateInformation(CustomerInformation customerInformation, Sheet sheet) {
        int firstRow = 1;
        int type = 1;
        Row rowName = sheet.createRow(firstRow);
        Cell name = rowName.createCell(0, type);
        name.setCellValue(CustomerInformationEnum.NAME.getInternalValue());
        Cell nameValue = rowName.createCell(1, type);
        nameValue.setCellValue(customerInformation.getNama());

        Row rowAddress = sheet.createRow(sheet.getLastRowNum() + firstRow);
        Cell address = rowAddress.createCell(0, type);
        address.setCellValue(CustomerInformationEnum.ADDRESS.getInternalValue());
        Cell addressValue = rowAddress.createCell(1, type);
        addressValue.setCellValue(customerInformation.getAlamat());

        Row rowAccountNumber = sheet.createRow(sheet.getLastRowNum() + firstRow);
        Cell accountNumberCell = rowAccountNumber.createCell(0, type);
        accountNumberCell.setCellValue(CustomerInformationEnum.ACCOUNT_NUMBER.getInternalValue());
        Cell accountNumberCellValue = rowAccountNumber.createCell(1, type);
        accountNumberCellValue.setCellValue(customerInformation.getNomorRekening());

        Row rowBank = sheet.createRow(sheet.getLastRowNum() + firstRow);
        Cell bankCell = rowBank.createCell(0, type);
        bankCell.setCellValue(CustomerInformationEnum.BANK_NAME.getInternalValue());
        Cell bankCellValue = rowBank.createCell(1, type);
        bankCellValue.setCellValue(customerInformation.getBank());

        Row rowBankAccountType = sheet.createRow(sheet.getLastRowNum() + firstRow);
        Cell bankAccountTypeCell = rowBankAccountType.createCell(0, type);
        bankAccountTypeCell.setCellValue(CustomerInformationEnum.BANK_ACCOUNT_TYPE.getInternalValue());
        Cell bankAccountTypeCellValue = rowBankAccountType.createCell(1, type);
        bankAccountTypeCellValue.setCellValue("");

        Row currency = sheet.createRow(sheet.getLastRowNum() + firstRow);
        Cell currencyCell = currency.createCell(0, type);
        currencyCell.setCellValue(CustomerInformationEnum.CURRENCY.getInternalValue());
        Cell currencyCellValue = currency.createCell(1, type);
        currencyCellValue.setCellValue("IDR");

        Row limitOfDraft = sheet.createRow(sheet.getLastRowNum() + firstRow);
        Cell limitOfDraftCell = limitOfDraft.createCell(0, type);
        limitOfDraftCell.setCellValue(CustomerInformationEnum.LIMIT_OVERDRAFT.getInternalValue());
        Cell limitOfDraftCellValue = limitOfDraft.createCell(1, type);
        limitOfDraftCellValue.setCellValue("0,00");

        Row isBankHitFraud = sheet.createRow(sheet.getLastRowNum() + firstRow);
        Cell isBankHitFraudCell = isBankHitFraud.createCell(0, type);
        isBankHitFraudCell.setCellValue(CustomerInformationEnum.IS_BANK_STATEMENT_HIT_FRAUD_INDICATOR.getInternalValue());
        Cell isBankHitFraudCellValue = isBankHitFraud.createCell(1, type);
        isBankHitFraudCellValue.setCellValue("");

        Row totalFraudHit = sheet.createRow(sheet.getLastRowNum() + firstRow);
        Cell totalFraudHitCell = totalFraudHit.createCell(0, type);
        totalFraudHitCell.setCellValue(CustomerInformationEnum.TOTAL_FRAUD_HIT.getInternalValue());
        Cell totalFraudHitCellValue = totalFraudHit.createCell(1, type);
        totalFraudHitCellValue.setCellValue("");

        Row hyperlinkToFraud = sheet.createRow(sheet.getLastRowNum() + firstRow);
        Cell hyperlinkToFraudCell = hyperlinkToFraud.createCell(0, type);
        hyperlinkToFraudCell.setCellValue(CustomerInformationEnum.HYPERLINK_TO_FRAUD_INDICATOR.getInternalValue());
        Cell hyperlinkToFraudCellValue = hyperlinkToFraud.createCell(1, type);
        hyperlinkToFraudCellValue.setCellValue("");

        Row isBankHitBehaviour = sheet.createRow(sheet.getLastRowNum() + firstRow);
        Cell isBankHitBehaviourCell = isBankHitBehaviour.createCell(0, type);
        isBankHitBehaviourCell.setCellValue(CustomerInformationEnum.IS_BANK_STATEMENT_HIT_BEHAVIOUR_INDICATOR.getInternalValue());
        Cell isBankHitBehaviourCellValue = isBankHitBehaviour.createCell(1, type);
        isBankHitBehaviourCellValue.setCellValue("");

        Row totalBehaviourHit = sheet.createRow(sheet.getLastRowNum() + firstRow);
        Cell totalBehaviourHitCell = totalBehaviourHit.createCell(0, type);
        totalBehaviourHitCell.setCellValue(CustomerInformationEnum.TOTAL_BEHAVIOUR_HIT.getInternalValue());
        Cell totalBehaviourHitCellValue = totalBehaviourHit.createCell(1, type);
        totalBehaviourHitCellValue.setCellValue("");

        Row hyperlinkToBehaviour = sheet.createRow(sheet.getLastRowNum() + firstRow);
        Cell hyperlinkToBehaviourCell = hyperlinkToBehaviour.createCell(0, type);
        hyperlinkToBehaviourCell.setCellValue(CustomerInformationEnum.HYPERLINK_TO_BEHAVIOUR_INDICATOR.getInternalValue());
        Cell hyperlinkToBehaviourCellValue = hyperlinkToBehaviour.createCell(1, type);
        hyperlinkToBehaviourCellValue.setCellValue("");

    }

    private void generateDetailInformation(List<TransactionHeader> transactionHeaders, Sheet sheet, Workbook workbook, List<TransactionDetail> transactionDetailList) {
        Row headerMonthOfStatement = sheet.createRow(sheet.getLastRowNum() + 2);
        Cell headerMonthOfStatementCell = headerMonthOfStatement.createCell(1);
        headerMonthOfStatementCell.setCellValue(CustomerInformationEnum.MONTH_OF_BANK_STATEMENT.getInternalValue());

        Row headerDescription = createNewRow(sheet);
        Cell headerDescriptionCell = headerDescription.createCell(0);
        headerDescriptionCell.setCellValue(CustomerInformationEnum.DESCRIPTION.getInternalValue());

        Row totalNoCashDepositWithoutRemark = createNewRow(sheet);
        Cell totalNoCashDepositWithoutRemarkCell = totalNoCashDepositWithoutRemark.createCell(0);
        totalNoCashDepositWithoutRemarkCell.setCellValue(DetailInformationEnum.TOTAL_NO_CASH_DEPOSIT_WITHOUT_REMARK.getInternalValue());

        Row totalAmountCashDepositWithoutRemark = createNewRow(sheet);
        Cell totalAmountCashDepositWithoutRemarkCell = totalAmountCashDepositWithoutRemark.createCell(0);
        totalAmountCashDepositWithoutRemarkCell.setCellValue(DetailInformationEnum.TOTAL_AMOUNT_CASH_DEPOSIT_WITHOUT_REMARK.getInternalValue());

        Row totalNoCashDepositWithRemark = createNewRow(sheet);
        Cell totalNoCashDepositWithRemarkCell = totalNoCashDepositWithRemark.createCell(0);
        totalNoCashDepositWithRemarkCell.setCellValue(DetailInformationEnum.TOTAL_NO_CASH_DEPOSIT_WITH_REMARK.getInternalValue());

        Row totalAmountCashDepositWithRemark = createNewRow(sheet);
        Cell totalAmountCashDepositWithRemarkCell = totalAmountCashDepositWithRemark.createCell(0);
        totalAmountCashDepositWithRemarkCell.setCellValue(DetailInformationEnum.TOTAL_AMOUNT_CASH_DEPOSIT_WITH_REMARK.getInternalValue());

        Row totalNoOfCashWithdrawals = createNewRow(sheet);
        Cell totalNoOfCashWithdrawalsCell = totalNoOfCashWithdrawals.createCell(0);
        totalNoOfCashWithdrawalsCell.setCellValue(DetailInformationEnum.TOTAL_NO_OF_CASH_WITHDRAWALS.getInternalValue());

        Row totalAmountOfCashWithdrawals = createNewRow(sheet);
        Cell totalAmountOfCashWithdrawalsCell = totalAmountOfCashWithdrawals.createCell(0);
        totalAmountOfCashWithdrawalsCell.setCellValue(DetailInformationEnum.TOTAL_AMOUNT_OF_CASH_WITHDRAWALS.getInternalValue());

        Row totalNoOfInwardChequeBounces = createNewRow(sheet);
        Cell totalNoOfInwardChequeBouncesCell = totalNoOfInwardChequeBounces.createCell(0);
        totalNoOfInwardChequeBouncesCell.setCellValue(DetailInformationEnum.TOTAL_NO_OF_INWARD_CHEQUE_BOUNCES.getInternalValue());

        Row totalNoOfOutwardChequeBounces = createNewRow(sheet);
        Cell totalNoOfOutwardChequeBouncesCell = totalNoOfOutwardChequeBounces.createCell(0);
        totalNoOfOutwardChequeBouncesCell.setCellValue(DetailInformationEnum.TOTAL_NO_OF_OUTWARD_CHEQUE_BOUNCES.getInternalValue());

        Row totalAmountOfSelfTransactionCr = createNewRow(sheet);
        Cell totalAmountOfSelfTransactionCrCell = totalAmountOfSelfTransactionCr.createCell(0);
        totalAmountOfSelfTransactionCrCell.setCellValue(DetailInformationEnum.TOTAL_AMOUNT_OF_SELF_TRANSACTION_CR.getInternalValue());

        Row totalAmountOfSelfTransactionDr = createNewRow(sheet);
        Cell totalAmountOfSelfTransactionDrCell = totalAmountOfSelfTransactionDr.createCell(0);
        totalAmountOfSelfTransactionDrCell.setCellValue(DetailInformationEnum.TOTAL_AMOUNT_OF_SELF_TRANSACTION_DR.getInternalValue());

        Row totalNumberOfSelfTransactionCr = createNewRow(sheet);
        Cell totalNumberOfSelfTransactionCrCell = totalNumberOfSelfTransactionCr.createCell(0);
        totalNumberOfSelfTransactionCrCell.setCellValue(DetailInformationEnum.TOTAL_NUMBER_OF_SELF_TRANSACTION_CR.getInternalValue());

        Row totalNumberOfSelfTransactionDr = createNewRow(sheet);
        Cell totalNumberOfSelfTransactionDrCell = totalNumberOfSelfTransactionDr.createCell(0);
        totalNumberOfSelfTransactionDrCell.setCellValue(DetailInformationEnum.TOTAL_NUMBER_OF_SELF_TRANSACTION_DR.getInternalValue());

        Row balance1st = createNewRow(sheet);
        Cell balance1stCell = balance1st.createCell(0);
        balance1stCell.setCellValue(DetailInformationEnum.BALANCE_1ST.getInternalValue());

        Row balance7th = createNewRow(sheet);
        Cell balance7thCell = balance7th.createCell(0);
        balance7thCell.setCellValue(DetailInformationEnum.BALANCE_7TH.getInternalValue());

        Row balance14th = createNewRow(sheet);
        Cell balance14thCell = balance14th.createCell(0);
        balance14thCell.setCellValue(DetailInformationEnum.BALANCE_14TH.getInternalValue());

        Row balance21th = createNewRow(sheet);
        Cell balance21thCell = balance21th.createCell(0);
        balance21thCell.setCellValue(DetailInformationEnum.BALANCE_21TH.getInternalValue());

        Row balancelastDayOfTheMonth = createNewRow(sheet);
        Cell balancelastDayOfTheMonthCell = balancelastDayOfTheMonth.createCell(0);
        balancelastDayOfTheMonthCell.setCellValue(DetailInformationEnum.BALANCE_LAST_DAY_OF_THE_MONTH.getInternalValue());

        Row avgUtilization = createNewRow(sheet);
        Cell avgUtilizationCell = avgUtilization.createCell(0);
        avgUtilizationCell.setCellValue(DetailInformationEnum.AVG_UTILIZATION.getInternalValue());

        Row numberOfDaysAccountIsOverDrawn = createNewRow(sheet);
        Cell numberOfDaysAccountIsOverDrawnCell = numberOfDaysAccountIsOverDrawn.createCell(0);
        numberOfDaysAccountIsOverDrawnCell.setCellValue(DetailInformationEnum.NUMBER_OF_DAYS_ACCOUNT_IS_OVERDRAWN.getInternalValue());

        Row daysPastDueOrInterestPayDelay = createNewRow(sheet);
        Cell daysPastDueOrInterestPayDelayCell = daysPastDueOrInterestPayDelay.createCell(0);
        daysPastDueOrInterestPayDelayCell.setCellValue(DetailInformationEnum.DAYS_PAST_DUE_OR_INTEREST_PAY_DELAY.getInternalValue());

        Row grossNoCreditTransaction = createNewRow(sheet);
        Cell grossNoCreditTransactionCell = grossNoCreditTransaction.createCell(0);
        grossNoCreditTransactionCell.setCellValue(DetailInformationEnum.GROSS_NO_CREDIT_TRANSACTION.getInternalValue());

        Row grossAmountCreditTransaction = createNewRow(sheet);
        Cell grossAmountCreditTransactionCell = grossAmountCreditTransaction.createCell(0);
        grossAmountCreditTransactionCell.setCellValue(DetailInformationEnum.GROSS_AMOUNT_CREDIT_TRANSACTION.getInternalValue());

        Row grossNoDebitTransaction = createNewRow(sheet);
        Cell grossNoDebitTransactionCell = grossNoDebitTransaction.createCell(0);
        grossNoDebitTransactionCell.setCellValue(DetailInformationEnum.GROSS_NO_DEBIT_TRANSACTION.getInternalValue());

        Row grossAmountDebitTransaction = createNewRow(sheet);
        Cell grossAmountDebitTransactionCell = grossAmountDebitTransaction.createCell(0);
        grossAmountDebitTransactionCell.setCellValue(DetailInformationEnum.GROSS_AMOUNT_DEBIT_TRANSACTION.getInternalValue());

        Row totalNoNonBusinessCredits = createNewRow(sheet);
        Cell totalNoNonBusinessCreditsCell = totalNoNonBusinessCredits.createCell(0);
        totalNoNonBusinessCreditsCell.setCellValue(DetailInformationEnum.TOTAL_NO_NON_BUSINESS_CREDITS.getInternalValue());

        Row totalAmountNonBusinessCredits = createNewRow(sheet);
        Cell totalAmountNonBusinessCreditsCell = totalAmountNonBusinessCredits.createCell(0);
        totalAmountNonBusinessCreditsCell.setCellValue(DetailInformationEnum.TOTAL_AMOUNT_NON_BUSINESS_CREDITS.getInternalValue());

        Row totalNoNonBusinessDebit = createNewRow(sheet);
        Cell totalNoNonBusinessDebitCell = totalNoNonBusinessDebit.createCell(0);
        totalNoNonBusinessDebitCell.setCellValue(DetailInformationEnum.TOTAL_NO_NON_BUSINESS_DEBIT.getInternalValue());

        Row totalAmountNonBusinessDebit = createNewRow(sheet);
        Cell totalAmountNonBusinessDebitCell = totalAmountNonBusinessDebit.createCell(0);
        totalAmountNonBusinessDebitCell.setCellValue(DetailInformationEnum.TOTAL_AMOUNT_NON_BUSINESS_DEBIT.getInternalValue());

        Row totalNoNetBusinessCredits = createNewRow(sheet);
        Cell totalNoNetBusinessCreditsCell = totalNoNetBusinessCredits.createCell(0);
        totalNoNetBusinessCreditsCell.setCellValue(DetailInformationEnum.TOTAL_NO_NET_BUSINESS_CREDITS.getInternalValue());

        Row totalAmountNetBusinessCredits = createNewRow(sheet);
        Cell totalAmountNetBusinessCreditsCell = totalAmountNetBusinessCredits.createCell(0);
        totalAmountNetBusinessCreditsCell.setCellValue(DetailInformationEnum.TOTAL_AMOUNT_NET_BUSINESS_CREDITS.getInternalValue());

        Row totalNoNetBusinessDebit = createNewRow(sheet);
        Cell totalNoNetBusinessDebitCell = totalNoNetBusinessDebit.createCell(0);
        totalNoNetBusinessDebitCell.setCellValue(DetailInformationEnum.TOTAL_NO_NET_BUSINESS_DEBIT.getInternalValue());

        Row totalAmountNetBusinessDebit = createNewRow(sheet);
        Cell totalAmountNetBusinessDebitCell = totalAmountNetBusinessDebit.createCell(0);
        totalAmountNetBusinessDebitCell.setCellValue(DetailInformationEnum.TOTAL_AMOUNT_NET_BUSINESS_DEBIT.getInternalValue());

        Row highestEodBalanceOfTheMonth = createNewRow(sheet);
        Cell highestEodBalanceOfTheMonthCell = highestEodBalanceOfTheMonth.createCell(0);
        highestEodBalanceOfTheMonthCell.setCellValue(DetailInformationEnum.HIGHEST_EOD_BALANCE_OF_THE_MONTH.getInternalValue());

        Row lowestEodBalanceOfTheMonth = createNewRow(sheet);
        Cell lowestEodBalanceOfTheMonthCell = lowestEodBalanceOfTheMonth.createCell(0);
        lowestEodBalanceOfTheMonthCell.setCellValue(DetailInformationEnum.LOWEST_EOD_BALANCE_OF_THE_MONTH.getInternalValue());

        Row averageEodBalanceOfTheMonth = createNewRow(sheet);
        Cell averageEodBalanceOfTheMonthCell = averageEodBalanceOfTheMonth.createCell(0);
        averageEodBalanceOfTheMonthCell.setCellValue(DetailInformationEnum.AVERAGE_EOD_BALANCE_OF_THE_MONTH.getInternalValue());

        Row swingCalculation = createNewRow(sheet);
        Cell swingCalculationCell = swingCalculation.createCell(0);
        swingCalculationCell.setCellValue(DetailInformationEnum.SWING_CALCULATION.getInternalValue());

        Row totalAmountWindowDressingDebit = createNewRow(sheet);
        Cell totalAmountWindowDressingDebitCell = totalAmountWindowDressingDebit.createCell(0);
        totalAmountWindowDressingDebitCell.setCellValue(DetailInformationEnum.TOTAL_AMOUNT_WINDOW_DRESSING_DEBIT.getInternalValue());

        Row totalAmountWindowDressingCredit = createNewRow(sheet);
        Cell totalAmountWindowDressingCreditCell = totalAmountWindowDressingCredit.createCell(0);
        totalAmountWindowDressingCreditCell.setCellValue(DetailInformationEnum.TOTAL_AMOUNT_WINDOW_DRESSING_CREDIT.getInternalValue());

        DataFormat format = workbook.createDataFormat();
        CellStyle styleNumber = workbook.createCellStyle();
        styleNumber.setDataFormat(format.getFormat("#,##0.00"));
        List<DetailInformationVO> detailInformationVOList = getDataDetailInformation(transactionHeaders, transactionDetailList);
        detailInformationVOList.forEach(detailInformationVO -> {
            Cell monthCell = headerDescription.createCell(headerDescription.getLastCellNum());
            monthCell.setCellValue(DateUtil.formatDateToString(detailInformationVO.getPeriode(), DateUtil.DATE_PATTERN_6));

            Cell totalNoCashDepositWithoutRemarkCellValue = totalNoCashDepositWithoutRemark.createCell(totalNoCashDepositWithoutRemark.getLastCellNum());
            totalNoCashDepositWithoutRemarkCellValue.setCellValue(detailInformationVO.getTotalNoCashDepositWithoutRemark());

            Cell totalAmountCashDepositWithoutRemarkCellValue = totalAmountCashDepositWithoutRemark.createCell(totalAmountCashDepositWithoutRemark.getLastCellNum());
            totalAmountCashDepositWithoutRemarkCellValue.setCellStyle(styleNumber);
            totalAmountCashDepositWithoutRemarkCellValue.setCellValue(detailInformationVO.getTotalAmountCashDepositWithoutRemark());

            Cell totalNoCashDepositWithRemarkCellValue = totalNoCashDepositWithRemark.createCell(totalNoCashDepositWithRemark.getLastCellNum());
            totalNoCashDepositWithRemarkCellValue.setCellValue(detailInformationVO.getTotalNoCashDepositWithRemark());

            Cell totalAmountCashDepositWithRemarkCellValue = totalAmountCashDepositWithRemark.createCell(totalAmountCashDepositWithRemark.getLastCellNum());
            totalAmountCashDepositWithRemarkCellValue.setCellStyle(styleNumber);
            totalAmountCashDepositWithRemarkCellValue.setCellValue(detailInformationVO.getTotalAmountCashDepositWithRemark());

            Cell totalNoOfCashWithdrawalsCellValue = totalNoOfCashWithdrawals.createCell(totalNoOfCashWithdrawals.getLastCellNum());
            totalNoOfCashWithdrawalsCellValue.setCellValue(detailInformationVO.getTotalNoOfCashWithdrawals());

            Cell totalAmountOfCashWithdrawalsCellValue = totalAmountOfCashWithdrawals.createCell(totalAmountOfCashWithdrawals.getLastCellNum());
            totalAmountOfCashWithdrawalsCellValue.setCellStyle(styleNumber);
            totalAmountOfCashWithdrawalsCellValue.setCellValue(detailInformationVO.getTotalAmountOfCashWithdrawals());

            Cell totalNoOfInwardChequeBouncesCellValue = totalNoOfInwardChequeBounces.createCell(totalNoOfInwardChequeBounces.getLastCellNum());
            totalNoOfInwardChequeBouncesCellValue.setCellValue(detailInformationVO.getTotalNoOfInwardChequeBounces());

            Cell totalNoOfOutwardChequeBouncesCellValue = totalNoOfOutwardChequeBounces.createCell(totalNoOfOutwardChequeBounces.getLastCellNum());
            totalNoOfOutwardChequeBouncesCellValue.setCellValue(detailInformationVO.getTotalNoOfOutwardChequeBounces());

            Cell totalAmountOfSelfTransactionCrCellValue = totalAmountOfSelfTransactionCr.createCell(totalAmountOfSelfTransactionCr.getLastCellNum());
            totalAmountOfSelfTransactionCrCellValue.setCellStyle(styleNumber);
            totalAmountOfSelfTransactionCrCellValue.setCellValue(detailInformationVO.getTotalAmountOfSelfTransactionCr());

            Cell totalAmountOfSelfTransactionDrCellValue = totalAmountOfSelfTransactionDr.createCell(totalAmountOfSelfTransactionDr.getLastCellNum());
            totalAmountOfSelfTransactionDrCellValue.setCellStyle(styleNumber);
            totalAmountOfSelfTransactionDrCellValue.setCellValue(detailInformationVO.getTotalAmountOfSelfTransactionDr());

            Cell totalNumberOfSelfTransactionCrCellValue = totalNumberOfSelfTransactionCr.createCell(totalNumberOfSelfTransactionCr.getLastCellNum());
            totalNumberOfSelfTransactionCrCellValue.setCellValue(detailInformationVO.getTotalNumberOfSelfTransactionCr());

            Cell totalNumberOfSelfTransactionDrCellValue = totalNumberOfSelfTransactionDr.createCell(totalNumberOfSelfTransactionDr.getLastCellNum());
            totalNumberOfSelfTransactionDrCellValue.setCellValue(detailInformationVO.getTotalNumberOfSelfTransactionDr());

            Cell balance1stCellValue = balance1st.createCell(balance1st.getLastCellNum());
            balance1stCellValue.setCellStyle(styleNumber);
            balance1stCellValue.setCellValue(detailInformationVO.getBalance1st());

            Cell balance7thCellValue = balance7th.createCell(balance7th.getLastCellNum());
            balance7thCellValue.setCellStyle(styleNumber);
            balance7thCellValue.setCellValue(detailInformationVO.getBalance7th());

            Cell balance14thCellValue = balance14th.createCell(balance14th.getLastCellNum());
            balance14thCellValue.setCellStyle(styleNumber);
            balance14thCellValue.setCellValue(detailInformationVO.getBalance14th());

            Cell balance21thCellValue = balance21th.createCell(balance21th.getLastCellNum());
            balance21thCellValue.setCellStyle(styleNumber);
            balance21thCellValue.setCellValue(detailInformationVO.getBalance21th());

            Cell balancelastDayOfTheMonthCellValue = balancelastDayOfTheMonth.createCell(balancelastDayOfTheMonth.getLastCellNum());
            balancelastDayOfTheMonthCellValue.setCellStyle(styleNumber);
            balancelastDayOfTheMonthCellValue.setCellValue(detailInformationVO.getBalancelastDayOfTheMonth());

            Cell avgUtilizationCellValue = avgUtilization.createCell(avgUtilization.getLastCellNum());
            avgUtilizationCellValue.setCellValue(detailInformationVO.getAvgUtilization());

            Cell numberOfDaysAccountIsOverDrawnCellValue = numberOfDaysAccountIsOverDrawn.createCell(numberOfDaysAccountIsOverDrawn.getLastCellNum());
            numberOfDaysAccountIsOverDrawnCellValue.setCellValue(detailInformationVO.getNumberOfDaysAccountIsOverDrawn());

            Cell daysPastDueOrInterestPayDelayCellValue = daysPastDueOrInterestPayDelay.createCell(daysPastDueOrInterestPayDelay.getLastCellNum());
            daysPastDueOrInterestPayDelayCellValue.setCellValue(detailInformationVO.getDaysPastDueOrInterestPayDelay());

            Cell grossNoCreditTransactionCellValue = grossNoCreditTransaction.createCell(grossNoCreditTransaction.getLastCellNum());
            grossNoCreditTransactionCellValue.setCellValue(detailInformationVO.getGrossNoCreditTransaction());

            Cell grossAmountCreditTransactionCellValue = grossAmountCreditTransaction.createCell(grossAmountCreditTransaction.getLastCellNum());
            grossAmountCreditTransactionCellValue.setCellValue(detailInformationVO.getGrossAmountCreditTransaction());

            Cell grossNoDebitTransactionCellValue = grossNoDebitTransaction.createCell(grossNoDebitTransaction.getLastCellNum());
            grossNoDebitTransactionCellValue.setCellValue(detailInformationVO.getGrossNoDebitTransaction());

            Cell grossAmountDebitTransactionCellValue = grossAmountDebitTransaction.createCell(grossAmountDebitTransaction.getLastCellNum());
            grossAmountDebitTransactionCellValue.setCellValue(detailInformationVO.getGrossAmountDebitTransaction());

            Cell totalNoNonBusinessCreditsCellValue = totalNoNonBusinessCredits.createCell(totalNoNonBusinessCredits.getLastCellNum());
            totalNoNonBusinessCreditsCellValue.setCellValue(detailInformationVO.getTotalNoNonBusinessCredits());

            Cell totalAmountNonBusinessCreditsCellValue = totalAmountNonBusinessCredits.createCell(totalAmountNonBusinessCredits.getLastCellNum());
            totalAmountNonBusinessCreditsCellValue.setCellValue(detailInformationVO.getTotalAmountNonBusinessCredits());

            Cell totalNoNonBusinessDebitCellValue = totalNoNonBusinessDebit.createCell(totalNoNonBusinessDebit.getLastCellNum());
            totalNoNonBusinessDebitCellValue.setCellValue(detailInformationVO.getTotalNoNonBusinessDebit());

            Cell totalAmountNonBusinessDebitCellValue = totalAmountNonBusinessDebit.createCell(totalAmountNonBusinessDebit.getLastCellNum());
            totalAmountNonBusinessDebitCellValue.setCellValue(detailInformationVO.getTotalAmountNonBusinessDebit());

            Cell totalNoNetBusinessCreditsCellValue = totalNoNetBusinessCredits.createCell(totalNoNetBusinessCredits.getLastCellNum());
            totalNoNetBusinessCreditsCellValue.setCellValue(detailInformationVO.getTotalNoNetBusinessCredits());

            Cell totalAmountNetBusinessCreditsCellValue = totalAmountNetBusinessCredits.createCell(totalAmountNetBusinessCredits.getLastCellNum());
            totalAmountNetBusinessCreditsCellValue.setCellValue(detailInformationVO.getTotalAmountNetBusinessCredits());

            Cell totalNoNetBusinessDebitCellValue = totalNoNetBusinessDebit.createCell(totalNoNetBusinessDebit.getLastCellNum());
            totalNoNetBusinessDebitCellValue.setCellValue(detailInformationVO.getTotalNoNetBusinessDebit());

            Cell totalAmountNetBusinessDebitCellValue = totalAmountNetBusinessDebit.createCell(totalAmountNetBusinessDebit.getLastCellNum());
            totalAmountNetBusinessDebitCellValue.setCellValue(detailInformationVO.getTotalAmountNetBusinessDebit());

            Cell highestEodBalanceOfTheMonthCellValue = highestEodBalanceOfTheMonth.createCell(highestEodBalanceOfTheMonth.getLastCellNum());
            highestEodBalanceOfTheMonthCellValue.setCellValue(detailInformationVO.getHighestEodBalanceOfTheMonth());

            Cell lowestEodBalanceOfTheMonthCellValue = lowestEodBalanceOfTheMonth.createCell(lowestEodBalanceOfTheMonth.getLastCellNum());
            lowestEodBalanceOfTheMonthCellValue.setCellValue(detailInformationVO.getLowestEodBalanceOfTheMonth());

            Cell averageEodBalanceOfTheMonthCellValue = averageEodBalanceOfTheMonth.createCell(averageEodBalanceOfTheMonth.getLastCellNum());
            averageEodBalanceOfTheMonthCellValue.setCellValue(detailInformationVO.getAverageEodBalanceOfTheMonth());

            Cell swingCalculationCellValue = swingCalculation.createCell(swingCalculation.getLastCellNum());
            swingCalculationCellValue.setCellValue(detailInformationVO.getSwingCalculation());

            Cell totalAmountWindowDressingDebitCellValue = totalAmountWindowDressingDebit.createCell(totalAmountWindowDressingDebit.getLastCellNum());
            totalAmountWindowDressingDebitCellValue.setCellValue(detailInformationVO.getTotalAmountWindowDressingDebit());

            Cell totalAmountWindowDressingCreditCellValue = totalAmountWindowDressingCredit.createCell(totalAmountWindowDressingCredit.getLastCellNum());
            totalAmountWindowDressingCreditCellValue.setCellValue(detailInformationVO.getTotalAmountWindowDressingCredit());

        });


    }

    private Row createNewRow(Sheet sheet) {
        return sheet.createRow(sheet.getLastRowNum() + 1);
    }

    private List<DetailInformationVO> getDataDetailInformation(List<TransactionHeader> transactionHeaderList, List<TransactionDetail> transactionDetailList) {
        List<DetailInformationVO> detailInformationVOList = new ArrayList<>();
        for (TransactionHeader transactionHeader : transactionHeaderList) {
            DetailInformationVO detailInformationVO = new DetailInformationVO();
            List<TransactionDetail> transactionCashDepositWithoutRemark = transactionDetailList.stream()
                    .filter(transactionDetail -> transactionDetail.getTransactionHeader().equals(transactionHeader) && (transactionDetail.getKeterangan().equalsIgnoreCase(SETORAN) ||
                            transactionDetail.getKeterangan().equalsIgnoreCase(SETORAN_TUNAI)))
                    .collect(Collectors.toList());
            detailInformationVO.setPeriode(transactionHeader.getPeriode());
            detailInformationVO.setTotalNoCashDepositWithoutRemark(transactionCashDepositWithoutRemark.size());
            detailInformationVO.setTotalAmountCashDepositWithoutRemark(transactionCashDepositWithoutRemark.stream().mapToDouble(i -> i.getKredit()).sum());

            List<TransactionDetail> transactionCashDepositWithRemark = transactionDetailList.stream()
                    .filter(transactionDetail -> transactionDetail.getTransactionHeader().equals(transactionHeader) && (transactionDetail.getKeterangan().matches("(?i).*" + SETORAN_TUNAI + " .*") ||
                            transactionDetail.getKeterangan().matches("(?i).*" + SETORAN_VIA + " .*")))
                    .collect(Collectors.toList());
            detailInformationVO.setTotalNoCashDepositWithRemark(transactionCashDepositWithRemark.size());
            detailInformationVO.setTotalAmountCashDepositWithRemark(transactionCashDepositWithRemark.stream().mapToDouble(i -> i.getKredit()).sum());

            List<TransactionDetail> transactionCashWithdrawals = transactionDetailList.stream()
                    .filter(transactionDetail -> transactionDetail.getTransactionHeader().equals(transactionHeader) && transactionDetail.getKeterangan().matches("(?i).*" + TARIKAN + " .*"))
                    .collect(Collectors.toList());
            detailInformationVO.setTotalNoOfCashWithdrawals(transactionCashWithdrawals.size());
            detailInformationVO.setTotalAmountOfCashWithdrawals(transactionCashWithdrawals.stream().mapToDouble(i -> i.getDebet()).sum());
            detailInformationVO.setTotalNoOfInwardChequeBounces(0);
            List<TransactionDetail> transactionOutwardChequeBounces = transactionDetailList.stream()
                    .filter(transactionDetail -> transactionDetail.getTransactionHeader().equals(transactionHeader) && (transactionDetail.getKeterangan().matches("(?i).*" + TOLAKAN_KLIRING + " .*") &&
                            transactionDetail.getDebet() != null))
                    .collect(Collectors.toList());
            detailInformationVO.setTotalNoOfOutwardChequeBounces(transactionOutwardChequeBounces.size());
            detailInformationVO.setTotalAmountOfSelfTransactionCr(0d);
            detailInformationVO.setTotalAmountOfSelfTransactionDr(0d);
            detailInformationVO.setTotalNumberOfSelfTransactionCr(0);
            detailInformationVO.setTotalNumberOfSelfTransactionDr(0);
            Calendar c = Calendar.getInstance();
            c.setTime(transactionHeader.getPeriode());
            c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
            Date lastDateOfMonth = c.getTime();
            c.set(Calendar.DAY_OF_MONTH, c.getActualMinimum(Calendar.DAY_OF_MONTH));
            Date firstDateOfMonth = c.getTime();
            c.set(Calendar.DAY_OF_MONTH, 7);
            Date day7OfMonth = c.getTime();
            c.set(Calendar.DAY_OF_MONTH, 14);
            Date day14OfMonth = c.getTime();
            c.set(Calendar.DAY_OF_MONTH, 21);
            Date day21OfMonth = c.getTime();
            TransactionDetail transactionBalance1st = transactionDetailList.stream()
                    .filter(transactionDetail -> transactionDetail.getTanggalTransaksi().equals(firstDateOfMonth) && transactionDetail.getTransactionHeader().equals(transactionHeader))
                    .reduce((first, second) -> second).orElse(null);
            TransactionDetail transactionBalance7th = transactionDetailList.stream()
                    .filter(transactionDetail -> transactionDetail.getTanggalTransaksi().equals(day7OfMonth) && transactionDetail.getTransactionHeader().equals(transactionHeader))
                    .reduce((first, second) -> second).orElse(null);
            TransactionDetail transactionBalance14th = transactionDetailList.stream()
                    .filter(transactionDetail -> transactionDetail.getTanggalTransaksi().equals(day14OfMonth) && transactionDetail.getTransactionHeader().equals(transactionHeader))
                    .reduce((first, second) -> second).orElse(null);
            TransactionDetail transactionBalance21th = transactionDetailList.stream()
                    .filter(transactionDetail -> transactionDetail.getTanggalTransaksi().equals(day21OfMonth) && transactionDetail.getTransactionHeader().equals(transactionHeader))
                    .reduce((first, second) -> second).orElse(null);
            TransactionDetail transactionBalanceLastOfDay = transactionDetailList.stream()
                    .filter(transactionDetail -> transactionDetail.getTanggalTransaksi().equals(lastDateOfMonth) && transactionDetail.getTransactionHeader().equals(transactionHeader))
                    .reduce((first, second) -> second).orElse(null);
            if (transactionBalance1st != null) detailInformationVO.setBalance1st(transactionBalance1st.getSaldo());
            else detailInformationVO.setBalance1st(0d);
            if (transactionBalance7th != null) detailInformationVO.setBalance7th(transactionBalance7th.getSaldo());
            else detailInformationVO.setBalance7th(0d);
            if (transactionBalance14th != null) detailInformationVO.setBalance14th(transactionBalance14th.getSaldo());
            else detailInformationVO.setBalance14th(0d);
            if (transactionBalance21th != null) detailInformationVO.setBalance21th(transactionBalance21th.getSaldo());
            else detailInformationVO.setBalance21th(0d);
            if (transactionBalanceLastOfDay != null)
                detailInformationVO.setBalancelastDayOfTheMonth(transactionBalanceLastOfDay.getSaldo());
            else detailInformationVO.setBalancelastDayOfTheMonth(0d);

            detailInformationVOList.add(detailInformationVO);
        }
        detailInformationVOList = detailInformationVOList.stream()
                .sorted(Comparator.comparing(DetailInformationVO::getPeriode))
                .collect(Collectors.toList());
        return detailInformationVOList;
    }

    @Override
    public void generateTransactionDetail(Sheet sheet, List<TransactionDetail> transactionDetails, Workbook workbook, CustomerInformation customerInformation) {
        headerSheetTransactionDetail(sheet);
        valueSheetTransactionDetail(sheet, transactionDetails, workbook, customerInformation);
    }

    private Row headerSheetTransactionDetail(Sheet sheet) {
        Row header = sheet.createRow(0);
        header.createCell(0, 1).setCellValue("Tanggal");
        header.createCell(1, 1).setCellValue("Keterangan");
        header.createCell(2, 1).setCellValue("Cbg");
        header.createCell(3, 1).setCellValue("Debet");
        header.createCell(4, 1).setCellValue("Kredit");
        header.createCell(5, 1).setCellValue("Saldo");
        header.createCell(6, 1).setCellValue("Klasifikasi 1");
        header.createCell(7, 1).setCellValue("Klasifikasi 2");
        return header;
    }

    private void valueSheetTransactionDetail(Sheet sheet, List<TransactionDetail> transactionDetailList, Workbook workbook, CustomerInformation customerInformation) {
        int firstRow = 1;
        DataFormat format = workbook.createDataFormat();
        CellStyle styleNumber = workbook.createCellStyle();
        styleNumber.setDataFormat(format.getFormat("#,##0.00"));

        for (TransactionDetail transactionDetail : transactionDetailList) {
            Row row = sheet.createRow(firstRow);
            Cell tanggalCell = row.createCell(0, 1);
            tanggalCell.setCellValue(DateUtil.formatDateToString(transactionDetail.getTanggalTransaksi(), DateUtil.DATE_PATTERN2));
            Cell keteranganCell = row.createCell(1, 1);
            if(transactionDetail.getKeterangan() != null){
                keteranganCell.setCellValue(transactionDetail.getKeterangan().replace("|"," "));
                Cell klasification1Cell = row.createCell(6, 1);
                Cell klasification2Cell = row.createCell(7, 1);

                String[] split = transactionDetail.getKeterangan().split("\\|");
                String namePT = "";
                for (String o : split){
                    namePT = o;
                }
                log.info("name : {}", namePT);

                String[] nameSplit = customerInformation.getNama().split(" ");
                String name = nameSplit[0];
                if (nameSplit.length > 1) name = name + " " + nameSplit[1];

                if(customerInformation.getNama().toUpperCase().contains(namePT.toUpperCase())){
                    klasification1Cell.setCellValue(customerInformation.getNama());
                    klasification2Cell.setCellValue("Self");
                }else if(transactionDetail.getKeterangan().toUpperCase().contains(TARIKAN) || transactionDetail.getKeterangan().toUpperCase().contains(SETORAN)){
                    klasification2Cell.setCellValue(transactionDetail.getKeterangan().replace("|"," "));
                }else if(namePT.toUpperCase().equalsIgnoreCase(BIAYA_ADM)
                        || namePT.toUpperCase().equalsIgnoreCase(CR_KOREKSI_BUNGA)
                        || namePT.toUpperCase().equalsIgnoreCase(BUNGA)
                        || namePT.toUpperCase().equalsIgnoreCase(PAJAK_BUNGA)
                        || namePT.toUpperCase().equalsIgnoreCase(SALDO_AWAL)
                        || transactionDetail.getKeterangan().toUpperCase().contains("KR OTOMATIS"))
                {

                }else if(transactionDetail.getKeterangan().toUpperCase().contains("PENERIMAAN NEGARA")){
                    klasification2Cell.setCellValue("PENERIMAAN NEGARA");
                }else {
                    List<Company> companyList = companyRepository.findByPerusahaanLikeIgnoreCase(convertToLikeQuery(namePT));
                    if(!companyList.isEmpty()) {
                        klasification1Cell.setCellValue(companyList.get(0).getPerusahaan());
                        klasification2Cell.setCellValue("Company");
                    }else{
                        klasification1Cell.setCellValue(namePT);
                        klasification2Cell.setCellValue("Person");
                    }
                }
            }
            Cell cbgCell = row.createCell(2, 1);
            if(transactionDetail.getCbg() != null) cbgCell.setCellValue(transactionDetail.getCbg());
            Cell debetCell = row.createCell(3, 1);
            debetCell.setCellStyle(styleNumber);
            if(transactionDetail.getDebet() != null) debetCell.setCellValue(Long.valueOf(transactionDetail.getDebet().longValue()));
            Cell kreditCell = row.createCell(4, 1);
            kreditCell.setCellStyle(styleNumber);
            if(transactionDetail.getKredit() != null) kreditCell.setCellValue(Long.valueOf(transactionDetail.getKredit().longValue()));
            Cell saldoCell = row.createCell(5, 1);
            saldoCell.setCellStyle(styleNumber);
            if(transactionDetail.getSaldo() != null) saldoCell.setCellValue(Long.valueOf(transactionDetail.getSaldo().longValue()));
            firstRow += 1;

        }
    }

    private void download(String url, File destination) throws IOException {
        URL website = new URL(url);
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        FileOutputStream fos = new FileOutputStream(destination);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
    }

    @Override
    public void generateSheetTop20Fund(List<TransactionHeader> transactionHeaders, List<TransactionDetail> transactionDetailList, Sheet sheet, Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 15);
        style.setFont(font);
        Row rowName = sheet.createRow(0);
        Cell name = rowName.createCell(0, 1);
        name.setCellValue("Top 20 Funds Received ");
        name.setCellStyle(style);
        transactionHeaders.forEach(transactionHeader -> {
            Row rowHeaderMonth = createNewRow(sheet);
            Cell cellHeaderMonth = rowHeaderMonth.createCell(rowHeaderMonth.getLastCellNum() < 0 ? 0 : rowHeaderMonth.getLastCellNum());
            cellHeaderMonth.setCellValue(DateUtil.formatDateToString(transactionHeader.getPeriode(), DateUtil.DATE_PATTERN_12));
            Row rowHeaderDescription = createNewRow(sheet);
            createCell(rowHeaderDescription).setCellValue("Description");
            createCell(rowHeaderDescription).setCellValue("Amount (IDR)");
            createCell(rowHeaderDescription).setCellValue("Number of Transaction ");

            List<String> transactionDetailListValue = transactionDetailList.stream()
                    .filter(transactionDetail -> transactionDetail.getTransactionHeader().equals(transactionHeader) && transactionDetail.getKredit() != null)
                    .map(transactionDetail -> transactionDetail.getKeterangan().split("\\|")[transactionDetail.getKeterangan().split("\\|").length - 1]).distinct()
                    .collect(Collectors.toList());

//            List<String> fromValue = transactionDetailList.stream()
//                    .filter(transactionDetail -> transactionDetail.getKeterangan().split("\\|")[transactionDetail.getKeterangan().split("\\|").length - 1].equals(fromName))
//                    .reduce((first, second) -> second);
            Map<String, Map<Double, Long>> mapMap = new HashMap<>();
            transactionDetailListValue.forEach(fromName -> {
                Map<Double, Long> doubleLongMap = new HashMap<>();
                TransactionDetail amount = transactionDetailList.stream()
                        .filter(transactionDetail -> transactionDetail.getKeterangan().split("\\|")[transactionDetail.getKeterangan().split("\\|").length - 1].equals(fromName))
                        .reduce((first, second) -> second).orElse(null);
                long numberofTransaction = transactionDetailList.stream()
                        .filter(transactionDetail -> transactionDetail.getKeterangan().split("\\|")[transactionDetail.getKeterangan().split("\\|").length - 1].equals(fromName))
                        .count();
                doubleLongMap.put(amount.getKredit(), numberofTransaction);
                mapMap.put(fromName, doubleLongMap);
            });
            Comparator<Double> byAmount = (Double amount1, Double amount2) -> amount1.compareTo(amount2);
            Comparator<Map.Entry<Double, Long>> byAmountMap = (Map.Entry<Double, Long> amount1, Map.Entry<Double, Long> amount2) -> amount1.getKey().compareTo(amount2.getKey());

//            Stream<Map.Entry<String,Map<Double, Long>>> sorted =
//                    mapMap.entrySet().stream()
//                            .sorted(Collections.reverseOrder(Map.Entry.<String,Map<Double, Long>>comparingByValue()));

//            mapMap.entrySet().stream()
//                    .sorted((Comparator<? super Map.Entry<String, Map<Double, Long>>>) Map.Entry.<String, Map.Entry<Double, Long>>comparingByValue(byAmountMap))
//                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

            log.info("mapMap {}", mapMap);
            transactionDetailListValue.forEach(fromName -> {
//                String[] split = transactionDetail.getKeterangan().split("\\|");
//                String fromName = split[split.length - 1];
                Row rowValueFundReceived = createNewRow(sheet);
                createCell(rowValueFundReceived).setCellValue("Transfer from " + fromName);
                TransactionDetail amount = transactionDetailList.stream()
                        .filter(transactionDetail -> transactionDetail.getKeterangan().split("\\|")[transactionDetail.getKeterangan().split("\\|").length - 1].equals(fromName))
                        .reduce((first, second) -> second).orElse(null);
                long numberofTransaction = transactionDetailList.stream()
                        .filter(transactionDetail -> transactionDetail.getKeterangan().split("\\|")[transactionDetail.getKeterangan().split("\\|").length - 1].equals(fromName))
                        .count();
                createCell(rowValueFundReceived).setCellValue(amount.getKredit());
                createCell(rowValueFundReceived).setCellValue(numberofTransaction);
            });


        });
    }

    private Cell createCell(Row row) {
        return row.createCell(row.getLastCellNum() < 0 ? 0 : row.getLastCellNum());
    }
}
