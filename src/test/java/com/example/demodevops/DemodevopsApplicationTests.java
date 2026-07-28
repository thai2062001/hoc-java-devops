package com.example.demodevops;

import com.example.demodevops.controller.MemberController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

// Chỉ định rõ ràng class Main chứa @SpringBootApplication cho Spring Test biết!
@SpringBootTest(classes = DemoDevopsApplication.class)
class DemodevopsApplicationTests {

    @Autowired
    private MemberController memberController;

    @Test
    void testGetAllMembersNotNull() {
        assertNotNull(memberController.getAllMembers(), "Lỗi nghiêm trọng: Danh sách trả về bị null rồi!");
    }
}