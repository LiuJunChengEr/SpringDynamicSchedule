package com.xizhengtech.prototype.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xizhengtech.prototype.model.Schedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * @Time : 2021/1/19 5:24 下午
 * @Author : Liujuncheng
 * @FileName: TaskController.java
 * @Software: prototype
 * @Des 任务调度动态启、停
 */
@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    private final Logger logger = LoggerFactory.getLogger(ScheduleController.class);
    private final ObjectMapper json = new ObjectMapper();
    private final Map<String, ScheduledFuture<?>> scheduleMap = new ConcurrentHashMap<>();

    private final TaskScheduler taskScheduler;

    public ScheduleController(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    @PostMapping("")
    public ResponseEntity<String> createSchedule(@RequestBody Schedule param) throws JsonProcessingException {
        final String body = json.writeValueAsString(param);
        logger.debug(body);

        // 触发器，即 cron 表达式
        final CronTrigger trigger = new CronTrigger(param.getCron());

        // 任务，具体执行业务逻辑
        final Runnable task = () -> logger.info(param.getTaskContent());

        // 注册任务调度
        final ScheduledFuture<?> schedule = taskScheduler.schedule(task, trigger);

        if (null != schedule) {
            final String scheduleId = UUID.randomUUID().toString();
            // 保留任务调度，用于后续停止等
            scheduleMap.put(scheduleId, schedule);

            // 将任务调度 ID 返回，前端可以控制任务后续停止
            return ResponseEntity.ok(scheduleId);
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> removeSchedule(@PathVariable("id") String id) {
        // 查找已有的任务调度
        final ScheduledFuture<?> scheduledFuture = scheduleMap.get(id);
        if (null == scheduledFuture) {
            return ResponseEntity.ok(id);
        }

        // 任务调度已经取消
        if (scheduledFuture.isCancelled()) {
            return ResponseEntity.ok(id);
        }

        // 尝试取消任务调度
        final boolean cancel = scheduledFuture.cancel(true);
        if (cancel) {
            return ResponseEntity.ok(id);
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

