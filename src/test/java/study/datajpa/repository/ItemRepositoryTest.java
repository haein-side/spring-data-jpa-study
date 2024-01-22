package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.datajpa.entity.Item;

@SpringBootTest
public class ItemRepositoryTest {

    @Autowired ItemRepository itemRepository;

    @Test
    public void save() {
        Item item = new Item("A");
        itemRepository.save(item);//이미 pk값이 있으므로 persist 호출이 안 됨! -> merge로 동작함
        //merge는 DB에 있을 거라고 생각하고 DB에서 찾아오려고 함 -> 없으면 새 거라고 판단해서 DB에 새로 넣어줌
        //merge는 데이터를 다 갈아끼우므로 별로 좋지 못함.. -> merge를 쓸 일은 별로 없음 (Entity가 detached 상황일 때
        //데이터에 대한 "변경 감지 기능"을 반드시 써야 함!!
        //데이터에 대한 저장은 "persist"를 써야 함!!
    }
}
