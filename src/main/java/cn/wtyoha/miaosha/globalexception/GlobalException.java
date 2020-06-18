package cn.wtyoha.miaosha.globalexception;

import cn.wtyoha.miaosha.domain.result.CodeMsg;

public class GlobalException extends RuntimeException {
    CodeMsg codeMsg;

    public GlobalException(CodeMsg msg) {
        super(msg.toString());
        codeMsg = msg;
    }

    public CodeMsg getCodeMsg() {
        return codeMsg;
    }
}
