package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import net.bytebuddy.description.type.TypeDefinition;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.FactoryBasedNavigableListAssert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    public void testMember() {
        // memberRepository = class jdk.proxy2.$Proxy120
        // Spring Data JPA가 인터페이스를 보고 프록시 객체, 구현 클래스를 만들어서 꽂음
        // Spring Data JPA가 인젝션을 함
        System.out.println("memberRepository = " + memberRepository.getClass());

        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Optional<Member> findMember = memberRepository.findById(savedMember.getId());

        if (findMember.isPresent()) {
            Member memberById = findMember.get();
            assertThat(memberById.getUsername()).isEqualTo(member.getUsername());
            assertThat(memberById.getId()).isEqualTo(member.getId());
            assertThat(memberById).isEqualTo(member);
        } else {
            // 값이 존재하지 않는 경우에 대한 처리
        }
    }

    @Test
    public void findByUsernameAndAgeGreaterThen() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        // 단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // 리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        // 카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void findHelloBy() {
        List<Member> findHelloBy = memberRepository.findHelloBy();
    }

    @Test
    public void testQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    public void findUsernameList() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernameList = memberRepository.findUsernameList();

        for (String s : usernameList) {
            System.out.println("s =  " + s);
        }
    }

    @Test
    public void findMemberDto() {
        Team team = new Team("TeamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10);
        m1.setTeam(team);
        memberRepository.save(m1);

        List<MemberDto> memberDto = memberRepository.findMemberDto();

        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }

    @Test
    public void findByNames() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));

        for (Member member : result) {
            System.out.println("member =  " + member);
        }
    }

    @Test
    public void returnType() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        Optional<Member> findMember = memberRepository.findOptionalByUsername("AAA");
        System.out.println("findMember = " + findMember); //Optional.emply -> .orElse()

        // 데이터가 있을 수도 있고 없을 수도 있음 -> Optional 사용
    }

    @Test
    public void paging() {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        //0번째에서 3개 가져오라고 했음
        //limit을 하기 위함
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        //when
        //반환타입에 따라 totalCount를 날릴 건지 안 날릴 건지 결정됨!
        //slice는 totalCount 안 날림
        //반환타입에 따라 totalCount 이런 게 결정됨
        //totalCount 자체가 데이터가 많아질수록 모든 데이터를 읽어야 하므로 성능이 안 좋음!
        //따라서 totalCount 쿼리는 분리하는 경우 많음
        //Member 엔티티 그대로 반환하면 안 됨!!!! -> DTO로 변환해서 리턴해야 함
        Page<Member> page = memberRepository.findByAge(age, pageRequest);
        
        //엔티티로 변환
        //json으로 변환해줌!!
        Page<MemberDto> toMap = page.map(m -> new MemberDto(m.getId(), m.getUsername(), null));

        //then
        List<Member> content = page.getContent();

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5); //총 컨텐츠 개수 (total Count)
        assertThat(page.getNumber()).isEqualTo(0); //페이지 번호
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue(); //첫번째 페이지인지
        assertThat(page.hasNext()).isTrue(); //다음 페이지가 존재하는지
    }


    @Test
    public void bulkUpdate() {
        //given
        //jpql날라가기 전에 기본적으로 db에 데이터 반영함
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 30));
        memberRepository.save(new Member("member5", 40));

        //when
        //bulk연산은 영속성 컨텍스트를 거치지 않고 바로 DB로 쿼리 쏴짐
        int resultCount = memberRepository.bulkAgePlus(20);
        //영속성 컨텍스트에 혹시나 남아있는 내용들이 db에 반영됨
//        em.clear(); // 영속성 컨텍스트 안의 데이터를 날리고 깔끔한 상태에서 db에서 값을 가져오게

        //bulk 연산 이후에는 영속성 컨텍스트 날려야 함!
        //같은 트랜지션 안에서 또다른 로직이 일어나면 큰일남!

        //find()를 통해 영속성 컨텍스트에 있는 값을 가져옴
        //아직 영속성 컨텍스트에는 반영이 안 되었음!! -> 영속성 컨텍스트를 날려야 함!!
        List<Member> result = memberRepository.findByUsername("member5");
        Member member5 = result.get(0);
        System.out.println("member5 = " + member5);

        //then
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void findMemberLazy() {
        //given
        //member1 -> teamA
        //member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);

        memberRepository.save(member1);
        memberRepository.save(member2);

        //영속성 컨텍스트에 있는 캐시 정보들을 DB에 완전히 반영
        //이후에 영속성 컨텍스트를 완전히 날려버림
        em.flush();
        em.clear();

        //when
        //select Member N+1문제 발생
        //1
        //List<Member> members = memberRepository.findAll();

        List<Member> members = memberRepository.findMemberFetchJoin();

        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            //Team이 영속성컨텍스트에 없으므로 쿼리가 members의 개수만큼 또 나감
            //N (나온 members만큼 N번 쿼리가 또 돌음!)
//            select
//            t1_0.team_id,
//                    t1_0.name
//            from
//            team t1_0
//            where
//            t1_0.team_id=?
            System.out.println("member.team = " + member.getTeam().getName());
        }
    }
}
