package com.anurag.ecomdemo.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReserveRequest {
    private Long itemId;
    private int quantity;
    private String reservedBy;

}
