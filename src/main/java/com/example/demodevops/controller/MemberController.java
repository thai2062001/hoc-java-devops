package com.example.demodevops.controller;

import com.example.demodevops.dto.MemberRequest;
import com.example.demodevops.model.Member;
import com.example.demodevops.service.MemberService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public List<Member> getAllMembers() {
        return memberService.getAllMembers();
    }

    @PostMapping
    public Member createMember(@RequestBody MemberRequest request) {
        return memberService.createMember(request);
    }
}