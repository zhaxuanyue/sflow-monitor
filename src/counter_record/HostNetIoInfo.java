package counter_record;

import db.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class HostNetIoInfo extends HostCounterRecord {
    private long bytes_in;     /* total bytes in */
    private long packets_in;   /* total packets in */
    private long errs_in;      /* total errors in */
    private long drops_in;     /* total drops in */
    private long bytes_out;    /* total bytes out */
    private long packets_out;  /* total packets out */
    private long errs_out;     /* total errors out */
    private long drops_out;    /* total drops out */

    private HostNetIoInfo(byte[] bytes, String host_ip, long timestamp) {
        super(bytes, host_ip, timestamp);
    }

    static public HostNetIoInfo fromBytes(byte[] bytes, String host_ip, long timestamp)
            throws Exception {
        HostNetIoInfo info = new HostNetIoInfo(bytes, host_ip, timestamp);
        info.decode();
        return info;
    }

    @Override
    public void decode() throws Exception {
        bytes_in = buffer.getLong();
        packets_in = Utils.bufferGetUint32(buffer);
        errs_in = Utils.bufferGetUint32(buffer);
        drops_in = Utils.bufferGetUint32(buffer);

        bytes_out = buffer.getLong();
        packets_out = Utils.bufferGetUint32(buffer);;
        errs_out = Utils.bufferGetUint32(buffer);
        drops_out = Utils.bufferGetUint32(buffer);
    }

    @Override
    protected HashMap<String, Object> getMap() {
        HashMap<String, Object> map = super.getMap();
        map.put("bytes_in", bytes_in);
        map.put("packets_in", packets_in);
        map.put("errs_in", errs_in);
        map.put("drops_in", drops_in);
        map.put("bytes_out", bytes_out);
        map.put("packets_out", packets_out);
        map.put("errs_out",errs_out);
        map.put("drops_out", drops_out);
        return map;
    }

    static public String schema() {
        return "CREATE TABLE IF NOT EXISTS host_net_io (" +
                "host_id INTEGER, " +
                "timestamp INTEGER, " +
                "bytes_in INTEGER, " +
                "packets_in INTEGER, " +
                "errs_in INTEGER, " +
                "drops_in INTEGER, " +
                "bytes_out INTEGER, " +
                "packets_out INTEGER, " +
                "errs_out INTEGER, " +
                "drops_out INTEGER, " +
                "FOREIGN KEY(host_id) REFERENCES host_description(rowid) );";
    }

    @Override
    public void saveToDb(Connection conn) throws Exception {
        Long host_id = HostDescription.getHostId(host_ip);
        String sql = "INSERT INTO host_net_io " +
                "(host_id, timestamp, bytes_in, packets_in, errs_in, drops_in, " +
                "bytes_out, packets_out, errs_out, drops_out) " +
                "VALUES(?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setLong(1, host_id);
        pstmt.setLong(2, timestamp);
        pstmt.setLong(3, bytes_in);
        pstmt.setLong(4, packets_in);
        pstmt.setLong(5, errs_in);
        pstmt.setLong(6, drops_in);
        pstmt.setLong(7, bytes_out);
        pstmt.setLong(8, packets_out);
        pstmt.setLong(9, errs_out);
        pstmt.setLong(10, drops_out);
        pstmt.executeUpdate();
    }

    static public void fromDb(String host_ip, Long timestamp, List<HashMap> list)
            throws Exception {
        Long host_id = HostDescription.getHostId(host_ip);
        Long start = timestamp - Utils.tenMinutes();

        String sql = "SELECT * FROM host_net_io WHERE host_id = ? AND ? < timestamp AND timestamp <= ?;";

        PreparedStatement pstmt = DB.db_conn.prepareStatement(sql);
        pstmt.setLong(1, host_id);
        pstmt.setLong(2, start);
        pstmt.setLong(3, timestamp);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            HashMap<String, Object> map = new LinkedHashMap<String, Object>();
            map.put("host_ip", host_ip);
            map.put("timestamp", rs.getLong("timestamp"));
            map.put("bytes_in", rs.getLong("bytes_in"));
            map.put("packets_in", rs.getLong("packets_in"));
            map.put("errs_in", rs.getLong("errs_in"));
            map.put("drops_in", rs.getLong("drops_in"));
            map.put("bytes_out", rs.getLong("bytes_out"));
            map.put("packets_out", rs.getLong("packets_out"));
            map.put("errs_out", rs.getLong("errs_out"));
            map.put("drops_out", rs.getLong("drops_out"));
            list.add(map);
        }
    }


}
