package cn.swiftchain.integration.vo;

import lombok.Data;

@Data
public class AccountResource {
    private Long freeNetLimit;

    private Long NetLimit;

    private Long TotalNetLimit;

    private Long TotalNetWeight;

    private Long EnergyLimit;

    private Long TotalEnergyLimit;

    private Long TotalEnergyWeight;
}
