package com.cv.solution.formdict.form.export;

import com.alibaba.excel.EasyExcel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

/**
 * @author: xutu
 * @since: 2025/12/18 17:25
 */
@SpringBootTest
public class DynamicExportTest {

    @Autowired
    private ExcelTemplateService excelTemplateService;

    @Autowired
    private ExcelDataService excelDataService;

    @Test
    void exportByTemplateId() throws Exception {

        Long templateId = 16L; // 👉 改这里即可导出不同模板

        // 1️⃣ 查询模板字段
        List<TemplateFieldModel> fields =
                new ArrayList<>(excelTemplateService.getFieldsByTemplateId(templateId));

        // 按顺序排序（很重要）
        fields.sort(Comparator.comparing(TemplateFieldModel::getFieldOrder));

        // 2️⃣ 构建 Excel 表头
        List<List<String>> head = fields.stream()
                .map(f -> Collections.singletonList(f.getFieldName()))
                .toList();

        // 3️⃣ 查询业务数据
        List<Map<String, Object>> rawData = excelDataService.getDataByTemplateId(templateId);

        // 4️⃣ 按模板字段顺序组装行数据（关键修正点）
        List<List<Object>> excelData = new ArrayList<>();

        for (Map<String, Object> row : rawData) {
            List<Object> line = new ArrayList<>();
            for (TemplateFieldModel field : fields) {
                line.add(row.get(field.getFieldCode()));
            }
            excelData.add(line);
        }

        // 5️⃣ 导出
        String fileName = "template_" + templateId + "_export.xlsx";

        EasyExcel.write(fileName)
                .head(head)
                .sheet("数据导出")
                .doWrite(excelData);

        System.out.println("导出完成：" + fileName);
    }
}
