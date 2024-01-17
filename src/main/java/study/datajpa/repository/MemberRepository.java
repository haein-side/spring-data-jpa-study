package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.datajpa.entity.Member;

import java.util.List;

// 인터페이스만 잡으면 Spring Data JPA가 구현 클래스를 만들어서 injection 해줌
// <type, id>
public interface MemberRepository extends JpaRepository<Member, Long> {

    // 도메인에 특화된 검색 조건이 들어가는 경우도 있음
    // JpaRepository interface에 method 존재 안 할 수도
    List<Member> findByUsername(String username);
}
