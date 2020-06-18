package cn.wtyoha.miaosha.domain.result;

import lombok.Data;

/**
 * 系统的通用返回结果
 */
@Data
public class Result<T> {
    private int code;
    private String msg;
    private T data;

    // 返回成功结果
    static public <T> Result<T> success(T data) {
        return new Result<>(data);
    }

    static public <T> Result<T> error(CodeMsg codeMsg) {
        return new Result<>(codeMsg);
    }

    private Result (T data){
        this.code =0;
        this.msg = "success";
        this.data = data;
    }

    private Result(CodeMsg codeMsg) {
        if (codeMsg == null) {
            return;
        }
        this.code = codeMsg.getCode();
        this.msg = codeMsg.getMsg();
    }


}
