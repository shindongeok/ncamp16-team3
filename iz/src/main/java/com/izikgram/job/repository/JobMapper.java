package com.izikgram.job.repository;

import com.izikgram.job.entity.Job;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface JobMapper {

    @Insert("INSERT INTO iz_job_scrap VALUES (#{memberId}, #{job.id})")
    void addJobScrap(@Param("job") Job job, @Param("memberId") String memberId);

    @Delete("DELETE FROM iz_job_scrap WHERE member_id = #{memberId} AND job_rec_id = #{job.id}")
    void deleteJobScrap(@Param("job") Job job, @Param("memberId") String memberId);

    @Select("SELECT COUNT(*) > 0 FROM iz_job_scrap WHERE member_id = #{memberId} AND job_rec_id = #{jobId}")
    boolean checkIfScraped(@Param("jobId") String jobId, @Param("memberId") String memberId);

    @Select("SELECT job_rec_id FROM iz_job_scrap WHERE member_id = #{memberId}")
    List<String> getScrapedJobIds(@Param("memberId") String memberId);
}
