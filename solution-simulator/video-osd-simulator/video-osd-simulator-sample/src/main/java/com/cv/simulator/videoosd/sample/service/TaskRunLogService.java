package com.cv.simulator.videoosd.sample.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cv.boot.crud.mp.support.MpCrudSupport;
import com.cv.boot.mybatisplus.pojo.vo.PageInfoVO;
import com.cv.simulator.videoosd.sample.mapper.TaskRunLogMapper;
import com.cv.simulator.videoosd.sample.pojo.entity.TaskRunLogEntity;
import com.cv.simulator.videoosd.sample.pojo.query.RunLogPageQuery;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TaskRunLogService extends ServiceImpl<TaskRunLogMapper, TaskRunLogEntity> {

    public void record(Long taskId, String eventType, String status, String message, String commandLine, Integer sentCount) {
        TaskRunLogEntity entity = new TaskRunLogEntity();
        entity.setTaskId(taskId);
        entity.setEventType(eventType);
        entity.setStatus(status);
        entity.setMessage(message);
        entity.setCommandLine(commandLine);
        entity.setSentCount(sentCount);
        entity.setCreateTime(LocalDateTime.now());
        save(entity);
    }

    public PageInfoVO<TaskRunLogEntity> pageList(RunLogPageQuery query) {
        Page<TaskRunLogEntity> page = lambdaQuery()
                .eq(query.getTaskId() != null, TaskRunLogEntity::getTaskId, query.getTaskId())
                .orderByDesc(TaskRunLogEntity::getId)
                .page(new Page<>(query.getCurrent(), query.getSize()));
        return MpCrudSupport.buildPage(page);
    }
}
