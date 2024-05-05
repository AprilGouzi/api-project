package com.api.controller;

import com.api.common.BaseResponse;
import com.api.common.ResultUtils;
import com.api.model.vo.InvokeInterfaceInfoVO;
import com.api.service.ChartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 囍崽
 * version 1.0
 */
@RestController
@RequestMapping("/chart")
@Slf4j
public class ChartController {

    @Resource
    private ChartService chartService;

    @GetMapping("/topInterface/invoke")
    public BaseResponse<List<InvokeInterfaceInfoVO>> listTopInvokeInterfaceInfo() {
        List<InvokeInterfaceInfoVO> listTopInvokeInterfaceInfo = chartService.listTopInvokeInterfaceInfo(3);
        return ResultUtils.success(listTopInvokeInterfaceInfo);
    }
}
