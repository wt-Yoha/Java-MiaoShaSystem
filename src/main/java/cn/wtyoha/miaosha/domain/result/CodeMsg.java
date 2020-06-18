package cn.wtyoha.miaosha.domain.result;

import lombok.Data;

@Data
public class CodeMsg {
    private int code;
    private String msg;

    // 通用异常
    public static CodeMsg SUCCESS = new CodeMsg(0, "success");
    public static CodeMsg NULL_PARAM = new CodeMsg(500000, "输入参数为空");
    public static CodeMsg WRONG_TELEPHONE_NUMBER = new CodeMsg(500100, "手机号为空或不符合格式");
    public static CodeMsg NULL_NICK_NAME = new CodeMsg(500200, "昵称为空");
    public static CodeMsg BIND_ERROR = new CodeMsg(500300, "参数校验异常: %s");
    public static CodeMsg WRONG_PASSWORD = new CodeMsg(500400, "密码错误");
    public static CodeMsg SERVER_ERROR = new CodeMsg(500500, "服务器异常");

    CodeMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public CodeMsg fillArgs(Object... args) {
        int code = this.code;
        String message = String.format(msg, args);
        return new CodeMsg(code, message);
    }
}
