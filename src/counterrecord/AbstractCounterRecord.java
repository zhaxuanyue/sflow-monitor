package counterrecord;
import config.Config;
import net.sf.json.JSONObject;

import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;


class AbstractCounterRecord {
    protected ByteBuffer buffer;
    protected String host_ip;
    protected long timestamp;

    public AbstractCounterRecord(byte[] bytes, String host_ip, long timestamp) {
        this.host_ip = host_ip;
        this.timestamp = timestamp;
        buffer = ByteBuffer.wrap(bytes);
    }

    public void decode() throws Exception {
        Config.LOG_ERROR("not implement method");
    }

    protected HashMap<String, Object> getMap() {
        HashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("host_ip", host_ip);
        map.put("timestamp", timestamp);
        return map;
    }

    @Override
    public String toString() {
        JSONObject jsonObject = JSONObject.fromObject(getMap());
        return jsonObject.toString(2);
    }

    public void saveToDb() throws Exception {
        Config.LOG_ERROR("not implement method");
    }

}

