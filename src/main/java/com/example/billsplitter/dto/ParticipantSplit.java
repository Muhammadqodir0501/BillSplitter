package com.example.billsplitter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantSplit {

    private String participantName;
    private BigDecimal individualCost;
    private BigDecimal commonShare;
    private BigDecimal commissionShare;
    private BigDecimal finalAmountDue;
}