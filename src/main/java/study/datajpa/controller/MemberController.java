package study.datajpa.controller;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    @GetMapping("/members2/{id}")
    public String findMember(@PathVariable("id") Member member) {
        //도메인 클래스 컨버터
        //스프링이 중간에서 컨버팅하는 과정을 다 끝내고
        //멤버를 바로 이 파라미터 결과 안에 인젝션 해줌

        //HTTP요청은 회원 Id를 받지만 도메인 클래스 컨버터가 중간에 동작해서 멤버 엔티티 객체를 반환
        //도메인 클래스 컨버터도 리파지토리를 사용해서 엔티티 찾음
        //도메인 클래스 컨버터로 엔티티를 파라미터로 받으면 엔티티는 단순조회용으로만 사용해야 함
        //트랜잭션 없는 범위에서 엔티티 조회해서 엔티티 변경해도 DB에 반영 안됨
        return member.getUsername();
    }

    @GetMapping("/members")
    public Page<Member> list(@PageableDefault(size = 5, sort = "username") Pageable pageable) {
        // default 설정 바꿔준 것

        //Pageable : 파라미터 정보를 담은 인터페이스 -> 인터페이스이지만 구현체로 Spring Boot가 자동 세팅해줌!
        //localhost:8080/members?page=1&size=3&sort=id,desc&sort=username,desc
        //controller에서 바인딩될 때 Pageable이 있으면 PageRequest라는 객체를 생성해서
        //값을 채워다가 인젝션을 해줌!!
        //Page : 결과 정보를 담은 인터페이스
        //반환타입이 Page이므로 "totalPages": 34, "totalElements": 100 같은 것들이 나옴
        return memberRepository.findAll(pageable);
    }

    //엔티티를 API로 노출하면 문제 발생 가능 -> 엔티티를 꼭 DTO로 변환해야
    @GetMapping("/membersDto")
    public Page<MemberDto> listDto(@PageableDefault(size = 5) Pageable pageable) {
        Page<Member> page = memberRepository.findAll(pageable);
        return page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));
    }


//    @PostConstruct
    public void init() {
        for (int i = 0; i < 100; i++) {
            memberRepository.save(new Member("user" + i, i));
        }
    }
}
