package com.cv.solution.formdict.form.export;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author: xutu
 * @since: 2025/12/18 17:24
 */
@Service
public class ExcelTemplateService {

    public List<TemplateFieldModel> getFieldsByTemplateId(Long templateId) {

        if (templateId == 16L) {
            return List.of(
                    new TemplateFieldModel(16L, "unitName", "单位名称", 1),
                    new TemplateFieldModel(16L, "fieldOfStudy", "所属领域", 2),
                    new TemplateFieldModel(16L, "unitNature", "单位性质", 3),
                    new TemplateFieldModel(16L, "socialCreditCode", "统一社会信用代码", 4),
                    new TemplateFieldModel(16L, "businessScope", "主要业务范围", 5),
                    new TemplateFieldModel(16L, "legalRepresentative", "法定代表人", 6),
                    new TemplateFieldModel(16L, "legalRepresentativePhone", "法人电话", 7),
                    new TemplateFieldModel(16L, "contactPerson", "联系人", 8),
                    new TemplateFieldModel(16L, "contactPosition", "联系人职务", 9),
                    new TemplateFieldModel(16L, "contactPhone", "联系人电话", 10),
                    new TemplateFieldModel(16L, "contactAddress", "通讯地址", 11)
            );
        }

        if (templateId == 17L) {
            return List.of(
                    new TemplateFieldModel(17L, "trainingSubject", "培训科目", 1),
                    new TemplateFieldModel(17L, "idCard", "身份证号", 2),
                    new TemplateFieldModel(17L, "name", "姓名", 3),
                    new TemplateFieldModel(17L, "phoneNumber", "手机号", 4)
            );
        }

        return Collections.emptyList();
    }
}
