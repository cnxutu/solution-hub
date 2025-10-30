package com.cv.solution.formdict.form.controller;

import com.cv.boot.web.response.Result;
import com.cv.solution.formdict.form.facade.TemplateFacade;
import com.cv.solution.formdict.form.pojo.param.FormTemplateDataParam;
import com.cv.solution.formdict.form.pojo.param.TemplateParam;
import com.cv.solution.formdict.form.pojo.vo.TemplateDataVO;
import com.cv.solution.formdict.form.pojo.vo.TemplateVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: xutu
 * @since: 2025/10/30 15:00
 */
@RestController
@RequestMapping("/templateAggregate")
@Slf4j
public class TemplateAggregateController {

    @Resource
    private TemplateFacade templateFacade;


    /**
     * 根据模板编码获取模板定义（含字段与选项）
     */
    @GetMapping("/fullTemplate/{code}")
    public Result<TemplateVO> getFullTemplate(@PathVariable String code) {
        return Result.success(templateFacade.getFullTemplateWithTemplateCode(code));
    }

    /**
     * 保存模板
     */
    @PostMapping("/saveTemplateForm")
    public Result saveTemplateForm(@RequestBody @Validated TemplateParam param) {
        templateFacade.saveTemplateFormNew(param);
        return Result.success();
    }

    /**
     * 保存对应模板的数据
     */
    @PostMapping("/saveTemplateData")
    public Result saveTemplateData(@RequestBody FormTemplateDataParam param) {
        templateFacade.saveTemplateData(param);
        return Result.success();
    }

    /**
     * 获取模板录入数据详情
     */
    @GetMapping("/templateData/detail/{templateDataId}")
    public Result<TemplateDataVO> getTemplateDataDetail(@PathVariable Long templateDataId) {
        TemplateDataVO data = templateFacade.getTemplateDataDetail(templateDataId);
        return Result.success(data);
    }


}
