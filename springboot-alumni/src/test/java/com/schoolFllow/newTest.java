package com.schoolFllow;

import com.schoolFllow.Mapper.ActivityMapper;
import com.schoolFllow.Mapper.AlumniMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class newTest {
    @Resource
    private AlumniMapper alumniMapper;
    @Resource
    private ActivityMapper activityMapper;
    @Test
    public void dddterst(){
        System.out.println(alumniMapper.selectAll());
    }
    @Test
    public void testAC(){
        System.out.println(activityMapper.selectAll());
    }
    @Test
    public void testAC2(){
        System.out.println(activityMapper.selectByName("书"));
    }
}
