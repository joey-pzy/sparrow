package cn.kavier.demo;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;

import java.util.List;

public class MyClassLo extends ClassLoader{

    private RedisTemplate<String, String> redisTemplate;

    public static void main(String[] args) throws ClassNotFoundException {
        new MyClassLo().loadClass("cn/kavier/demo/es/TestEsInsert");



    }

    public void test() {
        List<Object> list = redisTemplate.executePipelined(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                RedisTemplate<String, String> rt = (RedisTemplate<String, String>) connection;
                rt.opsForList().rightPop("");


                return null;
            }
        });
        list.add(null);

        List<Object> list1 = redisTemplate.executePipelined(new SessionCallback<String>() {
            @Override
            public <K, V> String execute(RedisOperations<K, V> operations) throws DataAccessException {
                return null;
            }
        });
    }
}
