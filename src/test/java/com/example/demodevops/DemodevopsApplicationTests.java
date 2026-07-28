package com.example.demodevops;

import com.example.demodevops.controller.MemberController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class DemodevopsApplicationTests {

    @Autowired
    private MemberController memberController;

    @Test
    void testGetAllMembersNotNull() {
        // Kiểm tra xem MemberController và hàm getAllMembers() mới hoạt động ngon lành, không bị NULL!
        assertNotNull(memberController.getAllMembers(), "Lỗi nghiêm trọng: Danh sách trả về bị null rồi!");
    }
}