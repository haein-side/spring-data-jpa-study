package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
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
//        List<Member> members = memberRepository.findAll();
        List<Member> members = memberRepository.findEntityGraphByUsername("member1");

//        List<Member> members = memberRepository.findMemberFetchJoin();

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

    @Test
    public void queryHint() {
        //given
        //JPA의 영속성 컨텍스트에 넣어둠
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        //JPA의 1차 캐시에 있는 결과를 db에 동기화함 -> 실제로 db에 insert 쿼리가 날라감!
        //1차 캐시를 날려버리는 것은 아님
        em.flush();
        //1차 캐시를 클리어 해버리면 영속성 컨텍스트가 다 날라감
        //다음부터 JPA 조회를 하면 영속성 컨텍스트에 1차 캐시가 없으므로 무조건 db에서 조회함
        em.clear();

        //when
        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.setUsername("member2");

        em.flush(); // dirty checking -> 변경 감지가 동작함! -> db에 업데이트 쿼리 나감!
    }

    @Test
    public void lock() {
        //given
        //JPA의 영속성 컨텍스트에 넣어둠
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        //when
        List<Member> result = memberRepository.findLockByUsername("member1");

        em.flush(); // dirty checking -> 변경 감지가 동작함! -> db에 업데이트 쿼리 나감!
    }

    @Test
    public void callCustom() {
        List<Member> result = memberRepository.findMemberCustom();
    }

    @Test
    public void queryByExample() { //inner join만 가능하고 Outer join이 안됨! 실무에서 잘 못 쓰는 이유.
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m1", 0, teamA);

        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        //when
        //Probe
        Member member = new Member("m1");

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreCase("age");

        //엔티티 자체가 검색조건이 됨
        Example<Member> example = Example.of(member, matcher);

        List<Member> result = memberRepository.findAll(example);

        Assertions.assertThat(result.get(0).getUsername()).isEqualTo("m1");
    }

    @Test
    public void projections() {
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA); // insert

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m1", 1, teamA);

        em.persist(m1); // insert
        em.persist(m2); // insert

        System.out.println("=================1==============");
        //insert into team (name,team_id) values ('teamA',1);

        //insert into member (age,created_by,created_date,last_modified_by,
        //last_modified_date,team_id,username,member_id) values (?,?,?,?,?,?,?,?)

        //insert into member (age,created_by,created_date,last_modified_by,
        //last_modified_date,team_id,username,member_id) values (?,?,?,?,?,?,?,?)
//        List<Member> members = em.createQuery("select m from Member m", Member.class)
//                .getResultList(); // flush()
        //JPQL이 실행될 때 flush() 자동 호출 -> 1차 캐시에 저장
//        select
//        m1_0.member_id,
//                m1_0.age,
//                m1_0.created_by,
//                m1_0.created_date,
//                m1_0.last_modified_by,
//                m1_0.last_modified_date,
//                m1_0.team_id,
//                m1_0.username
//        from
//        member m1_0
        // 1차 캐시에 있는 객체보다 < DB에 있는 값이 먼저 바뀔 수 있다.
        // 서버를 안 거치고도 db 값이 바뀔 수 있으니까
        //
        System.out.println("=================2==============");

//        Member m3 = new Member("m3", 3, teamA);
//        em.persist(m3);
        Member member2 = em.find(Member.class, "452"); //1차캐시 -> 없으면 바로 select문 -> 1차캐시 저장
        Member member = em.find(Member.class, "80");
        System.out.println("member2 = " + member2);
        System.out.println("member = " + member);

        System.out.println("=================3==============");
        em.flush(); //쓰기지연SQL저장소 - 삭제, 수정, 변경
        System.out.println("=================4==============");


//        em.flush();
//        em.clear();
//
//        //when
//        List<UsernameOnly> result = memberRepository.findProjectionsByUsername("m1");

//        for (UsernameOnly usernameOnly : result) {
//            System.out.println("usernameOnly = " + usernameOnly);
//        }
    }

}
