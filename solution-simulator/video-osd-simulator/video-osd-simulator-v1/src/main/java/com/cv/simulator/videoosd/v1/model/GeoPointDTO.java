package com.cv.simulator.videoosd.v1.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeoPointDTO {
    private BigDecimal latitude;
    private BigDecimal longitude;
}
