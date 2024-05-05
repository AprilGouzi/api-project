package com.api.service.impl;

import com.api.common.ErrorCode;
import com.api.exception.BusinessException;
import com.api.mapper.UserInterfaceInfoMapper;
import com.api.model.entity.InterfaceInfo;
import com.api.model.vo.InvokeInterfaceInfoVO;
import com.api.service.ChartService;
import com.api.service.InterfaceInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedHashMap
import java.util.List;

/**
 * @author 囍崽
 * version 1.0
 */
@Service
public class CharServiceImpl implements ChartService {

    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;

    @Resource
    private InterfaceInfoService interfaceInfoService;


    @Override
    public List<InvokeInterfaceInfoVO> listTopInvokeInterfaceInfo(int limit) {
        List<InvokeInterfaceInfoVO> invokeInterfaceInfoVOS = userInterfaceInfoMapper.listTopInvokeInterfaceInfo(limit);
        if (invokeInterfaceInfoVOS == null || invokeInterfaceInfoVOS.size() == 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }

        //根据id查询接口名称
        LinkedHashMap<Long, InvokeInterfaceInfoVO> voHashMap = new LinkedHashMap<>(invokeInterfaceInfoVOS.size());
        for (InvokeInterfaceInfoVO vo : invokeInterfaceInfoVOS) {
            voHashMap.put(vo.getId(), vo);
        }
        LambdaQueryWrapper<InterfaceInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(InterfaceInfo::getId, voHashMap.keySet());
        List<InterfaceInfo> infoList = interfaceInfoService.list(queryWrapper);

        for (InterfaceInfo interfaceInfo : infoList) {
            voHashMap.get(interfaceInfo.getId()).setName(interfaceInfo.getName());
        }
        return new ArrayList<>(voHashMap.values());
    }
}
