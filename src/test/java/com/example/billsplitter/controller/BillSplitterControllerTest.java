package com.example.billsplitter.controller;

import com.example.billsplitter.dto.IndividualPurchase;
import com.example.billsplitter.dto.PaymentRequest;
import com.example.billsplitter.service.BillSplitterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BillSplitterController.class)
class BillSplitterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BillSplitterService splitterService;

    @Test
    void testCalculateSplitSuccess() throws Exception {
        PaymentRequest validRequest = PaymentRequest.builder()
                .totalBillAmount(new BigDecimal("110.00"))
                .commonDishAmount(new BigDecimal("40.00"))
                .individualPurchases(List.of(
                        IndividualPurchase.builder().participantName("Test").amount(new BigDecimal("60.00")).build()
                ))
                .build();

        when(splitterService.calculateSplit(any(PaymentRequest.class)))
                .thenReturn(com.example.billsplitter.dto.FinalSplitResponse.builder().build());

        mockMvc.perform(post("/api/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "totalBillAmount": 110.00,
                          "totalAmountBeforeCommission": 100.00,
                          "commonDishAmount": 40.00,
                          "participants": ["Малика", "Бобур"],
                          "individualPurchases": [
                            {
                              "participantName": "Малика",
                              "amount": 60.00
                            },
                            {
                              "participantName": "Бобур",
                              "amount": 0.00
                            }
                          ]
                        }
                        """))
                .andExpect(status().isOk());
    }
}