package com.health.netty.netty;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.health.netty.service.DataProcessService;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Sharable
@Component
public class DataHandler extends SimpleChannelInboundHandler<String> {

    private final DataProcessService dataProcessService;

    public DataHandler(DataProcessService dataProcessService) {
        this.dataProcessService = dataProcessService;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        try {
            JSONObject json = JSON.parseObject(msg);
            String type = json.getString("type");
            if ("PING".equalsIgnoreCase(type)) {
                ctx.writeAndFlush("PONG\n");
                return;
            }
            if ("DATA".equalsIgnoreCase(type)) {
                dataProcessService.processData(json);
                ctx.writeAndFlush(JSON.toJSONString(new JSONObject()
                        .fluentPut("code", 200)
                        .fluentPut("message", "接收成功")
                        .fluentPut("timestamp", System.currentTimeMillis())) + "\n");
            }
        } catch (Exception e) {
            log.error("处理Netty数据异常: {}", e.getMessage(), e);
            ctx.writeAndFlush(JSON.toJSONString(new JSONObject()
                    .fluentPut("code", 500)
                    .fluentPut("message", "数据处理失败: " + e.getMessage())) + "\n");
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                log.debug("连接读空闲, 关闭连接: {}", ctx.channel().remoteAddress());
                ctx.close();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.warn("Netty连接异常: {}", cause.getMessage());
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.debug("客户端连接: {}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.debug("客户端断开: {}", ctx.channel().remoteAddress());
    }
}