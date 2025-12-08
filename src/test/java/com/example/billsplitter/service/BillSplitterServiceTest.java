package com.example.billsplitter.service;

import com.example.billsplitter.dto.FinalSplitResponse;
import com.example.billsplitter.dto.IndividualPurchase;
import com.example.billsplitter.dto.PaymentRequest;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BillSplitterServiceTest {

    private final BillSplitterService splitterService = new BillSplitterService();

    @Test
    void testStandardSplitCalculation() {
        PaymentRequest request = PaymentRequest.builder()
                .totalBillAmount(new BigDecimal("110.00"))
                .totalAmountBeforeCommission(new BigDecimal("100.00"))
                .commonDishAmount(new BigDecimal("40.00"))
                .participants(List.of("Малика", "Бобур", "Чарли", "Дэвид"))
                .individualPurchases(List.of(
                        IndividualPurchase.builder().participantName("Малика").amount(new BigDecimal("20.00")).build(),
                        IndividualPurchase.builder().participantName("Бобур").amount(new BigDecimal("30.00")).build(),
                        IndividualPurchase.builder().participantName("Чарли").amount(new BigDecimal("10.00")).build(),
                        IndividualPurchase.builder().participantName("Дэвид").amount(new BigDecimal("0.00")).build()
                ))
                .build();

        FinalSplitResponse response = splitterService.calculateSplit(request);


        assertEquals(new BigDecimal("110.00"), response.getTotalCalculatedSum(),
                "Общая сумма расчета должна совпадать с TotalBillAmount");

        assertEquals(4, response.getSplits().size(), "Должно быть 4 участника");

        response.getSplits().stream()
                .filter(s -> s.getParticipantName().equals("Бобур"))
                .findFirst()
                .ifPresent(bobSplit -> {
                    assertEquals(new BigDecimal("44.00"), bobSplit.getFinalAmountDue(), "Бобур должен 44.00");
                    assertEquals(new BigDecimal("10.00"), bobSplit.getCommonShare(), "Доля Бобура в общем блюде 10.00");
                    assertEquals(new BigDecimal("4.00"), bobSplit.getCommissionShare(), "Доля Бобура в комиссии 4.00");
                });
    }

    @Test
    void testZeroCommission() {
        PaymentRequest request = PaymentRequest.builder()
                .totalBillAmount(new BigDecimal("100.00"))
                .totalAmountBeforeCommission(new BigDecimal("100.00"))
                .commonDishAmount(new BigDecimal("40.00"))
                .participants(List.of("Малика", "Бобур"))
                .individualPurchases(List.of(
                        IndividualPurchase.builder().participantName("Малика").amount(new BigDecimal("60.00")).build(),
                        IndividualPurchase.builder().participantName("Бобур").amount(new BigDecimal("0.00")).build()
                ))
                .build();

        FinalSplitResponse response = splitterService.calculateSplit(request);

        assertTrue(response.getSplits().stream().allMatch(s -> s.getCommissionShare().compareTo(BigDecimal.ZERO) == 0));

        response.getSplits().stream()
                .filter(s -> s.getParticipantName().equals("Малика"))
                .findFirst()
                .ifPresent(aliceSplit -> assertEquals(new BigDecimal("80.00"), aliceSplit.getFinalAmountDue()));
    }
}