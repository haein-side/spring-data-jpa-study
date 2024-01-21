package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    // 페이징 처리
    // Pagebale 인터페이스 구현체를 넘기면 됨! - 보통 PageRequest를 많이 사용함
    // totalCount 쿼리는 left outer join 할 필요 없이 쿼리 분리하기
    @Query(value = "select m from Member m left join m.team t",
            countQuery = "select count(m) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);

    // 벌크 연산은 db에 있는 거 다 무시하고 쿼리를 날려버림
    // 영.컨에는 변경내용 반영 안 되었으므로 clear()해주어야함
    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    // 패치 조인
    // Team 객체까지 다 생성해서 Team 엔티티에 있는 필드까지 값이 다 채워짐!
    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();
}
