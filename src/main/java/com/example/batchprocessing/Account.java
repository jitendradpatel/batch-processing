package com.example.batchprocessing;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Account {
    private Integer aid;
    private Integer bid;
    private Integer abalance;
    private String filler;
}
