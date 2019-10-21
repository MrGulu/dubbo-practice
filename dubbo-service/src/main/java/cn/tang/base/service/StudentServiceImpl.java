package cn.tang.base.service;

import cn.tang.base.bean.Student;
import cn.tang.base.dao.StudentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl implements StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentServiceImpl.class);

    @Autowired
    private StudentMapper studentMapper;

    @Override
    public String sayHello(String name) {
        logger.info("接收到的消息：service receive msg:" + name);
        return "Hi,SpringBoot-Dubbo" + name;
    }

    @Override
    public Student getStudentById(Integer id) {
        return studentMapper.selectByPrimaryKey(id);
    }

}

