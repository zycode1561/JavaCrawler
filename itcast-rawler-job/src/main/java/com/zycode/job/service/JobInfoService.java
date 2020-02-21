package com.zycode.job.service;

import com.zycode.job.pojo.JobInfo;

import java.util.List;

public interface JobInfoService {

    /**
     * 保存工作信息
     * @param jobInfo
     */
    public void save(JobInfo jobInfo);

    public List<JobInfo> findJobInfo(JobInfo jobInfo);

}
