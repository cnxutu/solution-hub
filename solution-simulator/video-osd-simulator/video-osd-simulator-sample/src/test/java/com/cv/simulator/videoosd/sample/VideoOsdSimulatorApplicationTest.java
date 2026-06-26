package com.cv.simulator.videoosd.sample;

import com.cv.boot.mybatisplus.pojo.vo.PageInfoVO;
import com.cv.simulator.videoosd.sample.pojo.entity.DeviceTelemetryEntity;
import com.cv.simulator.videoosd.sample.pojo.entity.StreamTaskEntity;
import com.cv.simulator.videoosd.sample.pojo.query.StreamTaskPageQuery;
import com.cv.simulator.videoosd.sample.pojo.query.TelemetryPageQuery;
import com.cv.simulator.videoosd.sample.service.DeviceTelemetryService;
import com.cv.simulator.videoosd.sample.service.StreamTaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class VideoOsdSimulatorApplicationTest {

    @Autowired
    private StreamTaskService streamTaskService;

    @Autowired
    private DeviceTelemetryService telemetryService;

    @Test
    void startsWithH2SchemaAndProvidesCrudPages() {
        PageInfoVO<StreamTaskEntity> taskPage = streamTaskService.pageList(new StreamTaskPageQuery());
        PageInfoVO<DeviceTelemetryEntity> telemetryPage = telemetryService.pageList(new TelemetryPageQuery());

        assertTrue(taskPage.getTotal() >= 1);
        assertTrue(telemetryPage.getTotal() >= 1);
        assertEquals("demo-rtmp-task", taskPage.getRecords().get(0).getTaskName());
        assertEquals("dock-001", telemetryPage.getRecords().get(0).getDeviceSn());
    }
}
