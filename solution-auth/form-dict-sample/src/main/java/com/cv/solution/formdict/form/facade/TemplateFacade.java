package com.cv.solution.formdict.form.facade;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cv.boot.common.exception.BizException;
import com.cv.solution.formdict.dict.pojo.po.SysDictItemPO;
import com.cv.solution.formdict.dict.service.ISysDictItemService;
import com.cv.solution.formdict.form.pojo.po.*;
import com.cv.solution.formdict.form.pojo.vo.*;
import com.cv.solution.formdict.form.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: xutu
 * @since: 2025/10/28 19:58
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TemplateFacade {

    private final ITemplateService templateService;
    private final ITemplateFieldService fieldService;
    private final ITemplateFieldOptionService optionService;
    private final ISysDictItemService dictItemService;

    /**
     * 根据模板编码获取模板及其字段定义（含选项）
     */
    public TemplateVO getTemplateWithFields(String templateCode) {
        // 1. 查询模板
        LambdaQueryWrapper<TemplatePO> templateQueryWrapper = new LambdaQueryWrapper<>();
        templateQueryWrapper.eq(TemplatePO::getTemplateCode, templateCode);
        TemplatePO template = templateService.getOne(templateQueryWrapper);
        if (template == null) {
            throw new BizException("模板不存在：" + templateCode);
        }

        // 2. 查询模板下字段
        LambdaQueryWrapper<TemplateFieldPO> fieldQueryWrapper = new LambdaQueryWrapper<>();
        fieldQueryWrapper
                .eq(TemplateFieldPO::getTemplateId, template.getId())
                .orderByAsc(TemplateFieldPO::getFieldSort);
        List<TemplateFieldPO> fields = fieldService.list(fieldQueryWrapper);
        if (fields.isEmpty()) {
            return convert(template, Collections.emptyList());
        }

        // 3. 查询模板下的字段选项（仅 optionSource=2 时使用）
        LambdaQueryWrapper<TemplateFieldOptionPO> optionQueryWrapper = new LambdaQueryWrapper<>();
        optionQueryWrapper
                .eq(TemplateFieldOptionPO::getTemplateId, template.getId())
                .orderByAsc(TemplateFieldOptionPO::getSortOrder);
        Map<Long, List<TemplateFieldOptionPO>> optionMap = optionService
                .list(optionQueryWrapper)
                .stream()
                .collect(Collectors.groupingBy(TemplateFieldOptionPO::getFieldId));

        // 4. 组装字段信息
        List<TemplateFieldVO> fieldVOList = fields.stream().map(field -> {
                    TemplateFieldVO vo = BeanUtil.copyProperties(field, TemplateFieldVO.class);

                    // 加载字段选项
                    if (field.getOptionSource() == 1 && StrUtil.isNotBlank(field.getDictCode())) {
                        // 字典选项来源
                        LambdaQueryWrapper<SysDictItemPO> itemQueryWrapper = new LambdaQueryWrapper<>();
                        itemQueryWrapper
                                .eq(SysDictItemPO::getDictCode, field.getDictCode())
                                .orderByAsc(SysDictItemPO::getItemSort);
                        vo.setOptions(dictItemService.list(itemQueryWrapper)
                                .stream()
                                .map(dict -> {
                                    TemplateFieldOptionVO opt = new TemplateFieldOptionVO();
                                    opt.setOptionCode(dict.getDictCode());
                                    opt.setOptionName(dict.getItemName());
                                    opt.setOptionValue(dict.getItemValue());
                                    return opt;
                                })
                                .collect(Collectors.toList()));
                    } else if (field.getOptionSource() == 2) {
                        // 模板专属选项来源
                        List<TemplateFieldOptionPO> opts = optionMap.getOrDefault(field.getId(), Collections.emptyList());
                        vo.setOptions(BeanUtil.copyToList(opts, TemplateFieldOptionVO.class));
                    }

                    return vo;
                }).sorted(Comparator.comparingInt(TemplateFieldVO::getFieldSort))
                .collect(Collectors.toList());

        // 5. 组装模板结构
        return convert(template, fieldVOList);
    }

    /**
     * 模板实体转VO并附带字段列表
     */
    private TemplateVO convert(TemplatePO template, List<TemplateFieldVO> fields) {
        TemplateVO vo = BeanUtil.copyProperties(template, TemplateVO.class);
        vo.setFields(fields);
        return vo;
    }
}