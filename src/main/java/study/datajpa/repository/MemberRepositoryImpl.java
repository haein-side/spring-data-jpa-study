package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

import java.util.List;

@RequiredArgsConstructor // final로 선언된 필드를 이용한 생성자 자동 생성
public class MemberRepositoryImpl implements MemberRepositoryCustom { // 인터페이스 이름 + Impl으로 클래스 이름 맞춰야 함!

    private final EntityManager em;

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m")
                .getResultList();
    }
}
