package com.zycode.job.dao;

import com.zycode.job.pojo.JobInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobInfoDao extends JpaRepository<JobInfo,Long> {
}
