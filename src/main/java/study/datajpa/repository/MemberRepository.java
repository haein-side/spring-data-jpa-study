package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

// 인터페이스만 잡으면 Spring Data JPA가 구현 클래스를 만들어서 injection 해줌
// <type, id>
public interface MemberRepository extends JpaRepository<Member, Long> {

    // 도메인에 특화된 검색 조건이 들어가는 경우도 있음
    // JpaRepository interface에 method 존재 안 할 수도
    List<Member> findByUsername(String username);

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    List<Member> findHelloBy();

//    @Query("select m from Member m where m.username = :username and m.age = : age")
//    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);

    List<Member> findListByUsername(String username); // 컬렉션
    Member findMemberByUsername(String username); // 단건

    Optional<Member> findOptionalByUsername(String username); // 단건 optional
}
