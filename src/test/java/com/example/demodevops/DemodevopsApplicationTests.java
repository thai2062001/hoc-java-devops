package com.example.demodevops;

import com.example.demodevops.DemoDevopsApplication;
import com.example.demodevops.controller.MemberController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = DemoDevopsApplication.class)
class DemodevopsApplicationTests {

    @Autowired
    private MemberController memberController;

    @Test
    void testGetAllMembersNotNull() {
        assertNotNull(memberController, "MemberController chưa được khởi tạo!");
        assertNotNull(memberController.getAllMembers(), "Danh sách thành viên không được null!");
    }
}