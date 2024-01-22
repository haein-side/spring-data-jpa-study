package study.datajpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class) // @CreatedDate를 활용하기 위해.. 보통은 BaseEntity로 해결함
@NoArgsConstructor(access = AccessLevel.PROTECTED) //JPA는 기본 생성자가 있어야 하므로 (protected까지 가능)
public class Item implements Persistable<String> {

//    @Id @GeneratedValue
//    private Long id; //id값은 JPA에 persist를 하면 그 안에서 값이 들어감!

    //private long id; //0으로 판단함

    @Id
    private String id;

    @CreatedDate //jpa persist가 되기 전에 호출됨
    private LocalDateTime createdDate;

    public Item(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean isNew() { // 새거냐 아니냐를 직접 짜야 함
        return createdDate == null;
    }
}
