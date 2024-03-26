package test;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author ：xxx
 * @description：TODO
 * @date ：2024/02/28 9:52
 */

@SpringBootTest
@RunWith(SpringRunner.class)
public class Springboot_redisTest {
    @Autowired
    private RedisTemplate redisTemplate;
    @Test
    public void testString(){
        redisTemplate.opsForValue().set("city","beijing");

    }
}
