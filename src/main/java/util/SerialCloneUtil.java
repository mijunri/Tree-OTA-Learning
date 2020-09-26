package util;

import com.alibaba.fastjson.JSONObject;


public class SerialCloneUtil {

    public static <T> T deepClone(T obj) {
        return (T) JSONObject.parseObject(JSONObject.toJSONBytes(obj), obj.getClass());
    }

}
