package com.api.mapper;

import com.api.model.entity.UserInterfaceInfo;
import com.api.model.vo.InvokeInterfaceInfoVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author 20466
* @description 针对表【user_interface_info(用户调用接口关系表)】的数据库操作Mapper
* @createDate 2024-05-01 23:07:11
* @Entity com.api.model.entity.UserInterfaceInfo
*/
public interface UserInterfaceInfoMapper extends BaseMapper<UserInterfaceInfo> {
    List<InvokeInterfaceInfoVO> listTopInvokeInterfaceInfo(int limit);
}




