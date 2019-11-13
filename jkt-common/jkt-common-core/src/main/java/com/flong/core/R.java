package com.flong.core;

import com.flong.core.constants.CommonConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 响应信息主体
 */
@Builder
@ToString
@Accessors(chain = true)
@AllArgsConstructor
@ApiModel(description = "通用接口响应对象")
public class R<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    @ApiModelProperty(value = "返回码(0：成功 ， 1：失败）", position = 0)
    private int code = CommonConstants.SUCCESS;

    @Getter
    @Setter
    @ApiModelProperty(value = "返回信息", position = 1)
    private String msg = "success";


    @Getter
    @Setter
    @ApiModelProperty(value = "返回数据", position = 2)
    private T data;

    public R() {
        super();
    }

    public R(T data) {
        super();
        this.data = data;
    }

    public R(T data, String msg) {
        super();
        this.data = data;
        this.msg = msg;
    }
    public R(int code, String msg) {
        super();
        this.code = code;
        this.msg = msg;
    }

    public R(Throwable e) {
        super();
        this.msg = e.getMessage();
        this.code = CommonConstants.FAIL;
    }
}