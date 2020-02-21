package com.zycode.job.pojo;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name="job_info")
@Data
public class JobInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String companyName;
    private String companyAddr;
    private String companyInfo;
    private String jobName;
    private String jobAddr;
    private String jobInfo;
    private String salary;
    private String url;
    private String time;
}
