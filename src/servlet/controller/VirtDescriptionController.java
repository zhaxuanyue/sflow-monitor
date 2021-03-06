package servlet.controller;


import counter_record.VirtDescription;
import db.DB;
import net.sf.json.JSONArray;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VirtDescriptionController extends AbstractController {
    public VirtDescriptionController(Object params, String id) {
        super(params, id);
    }

    @Override
    protected Result doHandle(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        List<HashMap> list = new ArrayList<HashMap>();
        synchronized (DB.db_lock) {
            VirtDescription.fromDb(list);
        }
        JSONArray jsonArray = JSONArray.fromObject(list);
        return new Result(jsonArray.toString(2), id);
    }
}
