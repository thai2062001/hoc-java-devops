package com.example.demodevops.service;

import com.example.demodevops.dto.MemberRequest;
import com.example.demodevops.model.Member;
import com.example.demodevops.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Member createMember(MemberRequest request) {
        if (memberRepository.findByEmail(request.email()).isPresent()) {
            throw new RuntimeException("Email đã tồn tại!");
        }
        Member member = new Member(request.name(), request.email());
        return memberRepository.save(member);
    }
}