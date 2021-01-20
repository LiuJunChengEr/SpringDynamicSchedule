package com.xizhengtech.prototype.model;

import javax.validation.constraints.NotEmpty;

/**
 * @Time : 2021/1/19 5:45 下午
 * @Author : Liujuncheng
 * @FileName: Scheduler.java
 * @Software: prototype
 * @Des 创建任务调取的请求值
 * eg. {"cron":"0/10 * * * * ? ","taskContent":"0/10 * * * * ? "}
 */
public class Schedule {

    /**
     * cron 表达式
     * eg. cron":"0/10 * * * * ?
     */
    @NotEmpty
    private String cron;

    /**
     * 任务内容
     * 根据业务自定义
     */
    @NotEmpty
    private String taskContent;

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getTaskContent() {
        return taskContent;
    }

    public void setTaskContent(String taskContent) {
        this.taskContent = taskContent;
    }
}
