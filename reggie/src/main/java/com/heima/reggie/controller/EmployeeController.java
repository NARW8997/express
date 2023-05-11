package com.heima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.heima.reggie.common.R;
import com.heima.reggie.entity.Employee;
import com.heima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;


    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        // check if the username exist
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Employee::getUsername, employee.getUsername());
        Employee res = employeeService.getOne(wrapper);
        if (res == null) {
            return R.error("Username not exists");
        }

        // transfer to md5 format
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // compare passwords
        if (!res.getPassword().equals(password)) {
            return R.error("Login failed");
        }

        // check employee status
        if (res.getStatus() == 0) {
            return R.error("You account has been locked");
        }

        request.getSession().setAttribute("employee", res.getId());
        return R.success(res);
    }

    @PostMapping("/logout")
    public R logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return R.success("Logout Successfully");
    }

    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("New created employee: {}", employee.getUsername());

        // set employee default password 123456
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        // set create and update time
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        
        // set create user and update user
        // get user id first
        Long opUserId = (Long) request.getSession().getAttribute("employee");
        employee.setCreateUser(opUserId);
        employee.setUpdateUser(opUserId);

        employeeService.save(employee);
        return R.success("new employee created successfully");
    }
}
