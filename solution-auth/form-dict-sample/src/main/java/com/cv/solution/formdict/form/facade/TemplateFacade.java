package com.cv.solution.formdict.form.facade;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.cv.boot.common.exception.BizException;
import com.cv.solution.formdict.dict.pojo.po.SysDictItemPO;
import com.cv.solution.formdict.dict.service.ISysDictItemService;
import com.cv.solution.formdict.form.pojo.param.TemplateFieldOptionParam;
import com.cv.solution.formdict.form.pojo.param.TemplateFieldParam;
import com.cv.solution.formdict.form.pojo.param.TemplateParam;
import com.cv.solution.formdict.form.pojo.po.*;
import com.cv.solution.formdict.form.pojo.vo.*;
import com.cv.solution.formdict.form.service.*;
import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    public TemplateVO getFullTemplateWithTemplateCode(String templateCode) {
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

    /**
     * 基础保存模板方法
     *
     * @param param
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveTemplateData(TemplateParam param) {

        // 1️⃣ 保存模板主表信息
        TemplatePO template = new TemplatePO();
        BeanUtil.copyProperties(param, template);
        templateService.save(template);

        // 2️⃣ 保存字段信息
        if (CollectionUtils.isNotEmpty(param.getTemplateFields())) {
            for (TemplateFieldParam fieldParam : param.getTemplateFields()) {
                TemplateFieldPO field = new TemplateFieldPO();
                BeanUtil.copyProperties(fieldParam, field);
                field.setTemplateId(template.getId());
                fieldService.save(field);

                // 3️⃣ 保存选项信息（如果有）
                if (CollectionUtils.isNotEmpty(fieldParam.getTemplateFieldOptions())) {
                    for (TemplateFieldOptionParam optionParam : fieldParam.getTemplateFieldOptions()) {
                        TemplateFieldOptionPO option = new TemplateFieldOptionPO();
                        BeanUtil.copyProperties(optionParam, option);
                        option.setFieldId(field.getId());
                        optionService.save(option);
                    }
                }
            }
        }
    }

    /**
     * 基础保存模板方法（改良版）
     *
     * @param param
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveTemplateDataNew(TemplateParam param) {
        // 1️⃣ 保存模板主表
        TemplatePO template = new TemplatePO();
        BeanUtil.copyProperties(param, template);
        templateService.save(template);

        if (CollectionUtils.isEmpty(param.getTemplateFields())) {
            return;
        }

        // 2️⃣ 构建字段列表（使用 stream map + BeanUtil 可以兼顾拷贝 + 其它字段的赋值等额外操作 更灵活）
        List<TemplateFieldPO> fieldList = param.getTemplateFields().stream()
                .map(fieldParam -> {
                    TemplateFieldPO field = new TemplateFieldPO();
                    BeanUtil.copyProperties(fieldParam, field);
                    field.setTemplateId(template.getId());
                    return field;
                })
                .collect(Collectors.toList());

        // 2️⃣.1 批量保存字段
        fieldService.saveBatch(fieldList);

        // 2️⃣.2 构建「字段临时映射」用于回填（fieldParam → fieldPO）
        ImmutableMap<TemplateFieldParam, TemplateFieldPO> fieldMapping =
                ImmutableMap.copyOf(IntStream.range(0, fieldList.size())
                        .boxed() // 装箱操作，把基本类型 int 转成 Integer 对象流，否则下面 map 的 key 无法用基本类型
                        .collect(Collectors.toMap(
                                i -> param.getTemplateFields().get(i),
                                fieldList::get
                        ))); // 转换成常规 map，key 为 i，value 为 fieldPO

        // 3️⃣ 构建所有选项并批量保存
        List<TemplateFieldOptionPO> optionList = param.getTemplateFields().stream()
                .filter(f -> CollectionUtils.isNotEmpty(f.getTemplateFieldOptions()))
                .flatMap(f -> // 使用 flatmap，把每个字段 f 内部的选项列表 List<TemplateFieldOptionParam> 的流 “打平” 成一个单层的流。

                        f.getTemplateFieldOptions().stream().map(opt -> {
                            TemplateFieldOptionPO option = new TemplateFieldOptionPO();
                            BeanUtil.copyProperties(opt, option);
                            // 从映射表中取出 fieldId
                            option.setFieldId(fieldMapping.get(f).getId());
                            return option;
                        })).collect(Collectors.toList()
                );

        if (CollectionUtils.isNotEmpty(optionList)) {
            optionService.saveBatch(optionList);
        }
    }


}