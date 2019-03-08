package com.crossoverjie.cim.wsserver.controller;

import com.crossoverjie.cim.common.enums.StatusEnum;
import com.crossoverjie.cim.common.res.BaseResponse;
import com.crossoverjie.cim.wsserver.vo.req.SendMsgReqVO;
import com.crossoverjie.cim.wsserver.vo.res.SendMsgResVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

//import org.springframework.boot.actuate.metrics.CounterService;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 22/05/2018 14:46
 * @since JDK 1.8
 */
@Controller
@RequestMapping("/")
public class IndexController {

//    @Autowired
//    private CIMServer cimServer;


    /**
     * 统计 service
     */
//    @Autowired
//    private CounterService counterService;

    /**
     * 向服务端发任务，让服务端向客户端推消息
     * @param sendMsgReqVO
     * @return
     */
    @ApiOperation("服务端发送消息")
    @RequestMapping(value = "sendMsg",method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse<SendMsgResVO> sendMsg(@RequestBody SendMsgReqVO sendMsgReqVO){
        BaseResponse<SendMsgResVO> res = new BaseResponse();

        //todo: 通过websocket向客户端推消息
        //cimServer.sendMsg(sendMsgReqVO) ;

        //counterService.increment(Constants.COUNTER_SERVER_PUSH_COUNT);

        SendMsgResVO sendMsgResVO = new SendMsgResVO() ;
        sendMsgResVO.setMsg("OK") ;
        res.setCode(StatusEnum.SUCCESS.getCode()) ;
        res.setMessage(StatusEnum.SUCCESS.getMessage()) ;
        res.setDataBody(sendMsgResVO) ;
        return res ;
    }

}
