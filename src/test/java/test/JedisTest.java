package test;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import redis.clients.jedis.Jedis;

/**
 * @author ：xxx
 * @description：TODO
 * @date ：2024/02/27 20:50
 */
public class JedisTest {
    @Test
    public void testRedis(){
        //1.获取链接
        Jedis jedis = new Jedis("localhost",6379);
        //2.指定具体的操作
        jedis.set("username","xiaoming");
        //3.关闭链接
        jedis.close();
    }
}
