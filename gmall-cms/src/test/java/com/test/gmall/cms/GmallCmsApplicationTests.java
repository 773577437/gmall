package com.test.gmall.cms;

import com.test.gmall.cms.entity.Subject;
import com.test.gmall.cms.service.SubjectService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
class GmallCmsApplicationTests {

    @Autowired
    private SubjectService subjectService;

    @Test
    void contextLoads() {
        Subject subject = subjectService.getById(1);
        System.out.println(subject.toString());
    }

}
