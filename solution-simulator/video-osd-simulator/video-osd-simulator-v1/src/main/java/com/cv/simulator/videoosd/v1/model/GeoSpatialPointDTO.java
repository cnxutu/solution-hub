package com.cv.simulator.videoosd.v1.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 三维空间点，既包含经纬度，也包含相对高度。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeoSpatialPointDTO {
    private BigDecimal latitude;
    private BigDecimal longitude;
    private BigDecimal height;
}
