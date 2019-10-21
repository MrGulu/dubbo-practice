package cn.tang.base.service;


import cn.tang.base.bean.Student;

public interface StudentService {

    String sayHello(String name);

    Student getStudentById(Integer id);
}
