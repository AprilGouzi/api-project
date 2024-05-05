package com.api.service.impl.inner;

import cn.hutool.core.util.StrUtil;
import com.api.common.ErrorCode;
import com.api.exception.BusinessException;
import com.api.mapper.InterfaceInfoMapper;
import com.api.model.entity.InterfaceInfo;
import com.api.service.InnerInterfaceInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * @author 囍崽
 * version 1.0
 */
@DubboService
public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService {

    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;

    @Override
    public InterfaceInfo getInvokeInterfaceInfo(String url, String method) {
        if (StrUtil.hasBlank(url, method)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LambdaQueryWrapper<InterfaceInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(InterfaceInfo::getUrl, url)
                .eq(InterfaceInfo::getMethod, method);

        return interfaceInfoMapper.selectOne(queryWrapper);
    }
}
