package cn.tang.cacheframework.rediscache;

import redis.clients.jedis.Jedis;

import java.io.*;

/**
 * @author yanglingxiao
 * @description: redis序列化对象工具类
 * @date 2018/7/10 9:28
 */
public class RedisUtil {

    public static byte[] seriallize(Object object) {
        byte[] bytes = new byte[0];
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos);) {
            oos.writeObject(object);
            bytes = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public static Object unseriallize(byte[] bytes) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInputStream ois = new ObjectInputStream(bis);) {
            return ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean validateServer(Jedis jedis) {
        boolean result = false;
        try {
            if (jedis.ping().equals("PONG")) {
                result = true;
            }
        } catch (Exception ex) {
        }
        return result;
    }
}
