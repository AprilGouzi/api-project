package com.api.service.impl;

import com.api.common.ErrorCode;
import com.api.exception.BusinessException;
import com.api.mapper.InterfaceInfoMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.api.model.entity.InterfaceInfo;
import com.api.service.InterfaceInfoService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author 20466
 * @description 针对表【interface_info(接口信息)】的数据库操作Service实现
 * @createDate 2024-04-30 11:13:02
 */
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
        implements InterfaceInfoService {

    @Override
    public void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add) {
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String name = interfaceInfo.getName();
        String description = interfaceInfo.getDescription();
        String url = interfaceInfo.getUrl();
        String requestHeader = interfaceInfo.getRequestHeader();
        String responseHeader = interfaceInfo.getResponseHeader();
        Integer status = interfaceInfo.getStatus();
        String method = interfaceInfo.getMethod();
        Long userId = interfaceInfo.getUserId();

        //创建时，所有参数必须非空
        if (add) {
            if (StringUtils.isAnyBlank(name, description, url, requestHeader,
                    responseHeader, method) || ObjectUtils.anyNull(userId, status)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }

        if (StringUtils.isNotBlank(name) && name.length() > 256){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"名字过长");
        }
        if (StringUtils.isNotBlank(description) && description.length() > 512){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"描述过长");
        }
    }
}




