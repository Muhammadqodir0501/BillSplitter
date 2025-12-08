package com.example.billsplitter.service;

import com.example.billsplitter.dto.FinalSplitResponse;
import com.example.billsplitter.dto.IndividualPurchase;
import com.example.billsplitter.dto.ParticipantSplit;
import com.example.billsplitter.dto.PaymentRequest;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class BillSplitterService {

    private static final int SCALE = 2;

    public FinalSplitResponse calculateSplit(PaymentRequest request) {

        long numberOfParticipants = request.getParticipants().stream().distinct().count();
        BigDecimal commonDishAmount = request.getCommonDishAmount();
        BigDecimal totalCommission = request.getTotalBillAmount().subtract(request.getTotalAmountBeforeCommission());

        Map<String, BigDecimal> individualCosts = request.getIndividualPurchases().stream()
                .collect(Collectors.toMap(
                        IndividualPurchase::getParticipantName,
                        IndividualPurchase::getAmount
                ));

        List<String> allParticipants = Stream.concat(
                request.getParticipants().stream(),
                request.getIndividualPurchases().stream().map(IndividualPurchase::getParticipantName)
        ).distinct().toList();

        final BigDecimal commonSharePerPerson;
        if (numberOfParticipants > 0) {
            commonSharePerPerson = commonDishAmount.divide(
                    new BigDecimal(numberOfParticipants), SCALE, RoundingMode.HALF_UP
            );
        } else {
            commonSharePerPerson = BigDecimal.ZERO;
        }

        BigDecimal baseSumForCommission = allParticipants.stream()
                .map(name -> individualCosts.getOrDefault(name, BigDecimal.ZERO).add(commonSharePerPerson))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<ParticipantSplit> splits = allParticipants.stream().map(name -> {
            BigDecimal individualCost = individualCosts.getOrDefault(name, BigDecimal.ZERO);
            BigDecimal personalBase = individualCost.add(commonSharePerPerson);

            BigDecimal commissionShare = BigDecimal.ZERO;
            if (baseSumForCommission.compareTo(BigDecimal.ZERO) > 0) {
                commissionShare = personalBase
                        .divide(baseSumForCommission, 10, RoundingMode.HALF_UP)
                        .multiply(totalCommission)
                        .setScale(SCALE, RoundingMode.HALF_UP);
            }

            BigDecimal finalAmountDue = individualCost
                    .add(commonSharePerPerson)
                    .add(commissionShare)
                    .setScale(SCALE, RoundingMode.HALF_UP);

            return ParticipantSplit.builder()
                    .participantName(name)
                    .individualCost(individualCost)
                    .commonShare(commonSharePerPerson)
                    .commissionShare(commissionShare)
                    .finalAmountDue(finalAmountDue)
                    .build();
        }).collect(Collectors.toList());

        BigDecimal totalCalculatedSum = splits.stream()
                .map(ParticipantSplit::getFinalAmountDue)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(SCALE, RoundingMode.HALF_UP);

        return FinalSplitResponse.builder()
                .splits(splits)
                .totalCalculatedSum(totalCalculatedSum)
                .build();
    }
}
