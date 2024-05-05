package com.api.mapper;

import com.api.model.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 20466
* @description 针对表【user(用户)】的数据库操作Mapper
* @createDate 2024-04-30 15:47:21
* @Entity com.api.model.entity.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




