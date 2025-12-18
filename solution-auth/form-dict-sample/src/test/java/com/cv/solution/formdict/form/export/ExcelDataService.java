package com.cv.solution.formdict.form.export;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: xutu
 * @since: 2025/12/18 17:24
 */
@Service
public class ExcelDataService {

    public List<Map<String, Object>> getDataByTemplateId(Long templateId) {

        List<Map<String, Object>> list = new ArrayList<>();

        if (templateId == 16L) {
            Map<String, Object> row1 = new HashMap<>();
            row1.put("unitName", "某某科技有限公司");
            row1.put("fieldOfStudy", "人工智能");
            row1.put("unitNature", "民营");
            row1.put("socialCreditCode", "9133XXXXXXXX");
            row1.put("businessScope", "软件开发");
            row1.put("legalRepresentative", "张三");
            row1.put("legalRepresentativePhone", "13800000000");
            row1.put("contactPerson", "李四");
            row1.put("contactPosition", "经理");
            row1.put("contactPhone", "13900000000");
            row1.put("contactAddress", "杭州市西湖区");
            list.add(row1);
        }

        if (templateId == 17L) {
            Map<String, Object> row1 = new HashMap<>();
            row1.put("trainingSubject", "Java 后端");
            row1.put("idCard", "3301************");
            row1.put("name", "王五");
            row1.put("phoneNumber", "13700000000");
            list.add(row1);
        }

        return list;
    }
}
