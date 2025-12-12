package com.example.billsplitter.controller;

import com.example.billsplitter.dto.FinalSplitResponse;
import com.example.billsplitter.dto.PaymentRequest;
import com.example.billsplitter.service.BillSplitterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BillSplitterController {

    private final BillSplitterService splitterService;

    @PostMapping(value = "/calculate",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public FinalSplitResponse calculateSplit(@RequestBody PaymentRequest request) {
        return splitterService.calculateSplit(request);
    }
}