package com.example.billsplitter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    private BigDecimal totalBillAmount;
    private BigDecimal totalAmountBeforeCommission;
    private BigDecimal commonDishAmount;
    private List<String> participants;
    private List<IndividualPurchase> individualPurchases;
}