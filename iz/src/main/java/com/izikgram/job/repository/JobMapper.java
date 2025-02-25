package com.izikgram.job.repository;

import com.izikgram.job.entity.AlarmJob;
import com.izikgram.job.entity.Job;
import com.izikgram.job.entity.JobDto;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface JobMapper {

    @Insert("INSERT INTO iz_job_scrap VALUES (#{memberId}, #{job.id})")
    void addJobScrap(@Param("job") JobDto jobDto, @Param("memberId") String memberId);


    @Delete("DELETE FROM iz_job_scrap WHERE member_id = #{memberId} AND job_rec_id = #{job.id}")
    void deleteJobScrap(@Param("job") JobDto jobDto, @Param("memberId") String memberId);

    @Select("SELECT COUNT(*) > 0 FROM iz_job_scrap WHERE member_id = #{memberId} AND job_rec_id = #{jobId}")
    boolean checkIfScraped(@Param("jobId") String jobId, @Param("memberId") String memberId);

    @Select("SELECT job_rec_id FROM iz_job_scrap WHERE member_id = #{memberId}")
    List<String> getScrapedJobIds(@Param("memberId") String memberId);

    @Select("select * from iz_alarm_scrap where job_rec_id = #{job_rec_id} and member_id = #{member_id}")
    AlarmJob getAlarmScrapList(@Param("job_rec_id") String job_rec_id,
                                     @Param("member_id") String member_id);


//    @Select("select alarm_id from iz_alarm_scarp where alarm")
//    // 스크랩된 공고의 알람 ID 조회 (추가)
//    Long getAlarmIdByJobAndMember(@Param("alarm_id") String alarm_id, @Param("member_id") String member_id);

}
