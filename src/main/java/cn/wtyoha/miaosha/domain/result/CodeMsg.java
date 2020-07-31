package cn.wtyoha.miaosha.domain.result;

import lombok.Data;

@Data
public class CodeMsg {
    private int code;
    private String msg;
    // 由全局异常控制捕获后选择的跳转页面
    private String view;

    // 通用异常
    public static CodeMsg SUCCESS = new CodeMsg(0, "success");
    public static CodeMsg NULL_PARAM = new CodeMsg(500000, "输入参数为空");
    public static CodeMsg WRONG_TELEPHONE_NUMBER = new CodeMsg(500100, "手机号为空或不符合格式");
    public static CodeMsg NULL_NICK_NAME = new CodeMsg(500200, "昵称为空");
    public static CodeMsg BIND_ERROR = new CodeMsg(500300, "参数校验异常");
    public static CodeMsg WRONG_PASSWORD = new CodeMsg(500400, "密码错误", "login");
    public static CodeMsg SERVER_ERROR = new CodeMsg(500500, "服务器异常");
    public static CodeMsg UNREGISTER_USER = new CodeMsg(500600, "用户未注册", "login");
    public static CodeMsg USER_UNLOGIN = new CodeMsg(500600, "用户未登陆", "login");
    public static CodeMsg REPEAT_LOGIN= new CodeMsg(500700, "该用户已登陆，请先退出" );
    public static CodeMsg ACCESS_TIME_OUT = new CodeMsg(500800, "访问超时" );
    public static CodeMsg ERROR_PATH = new CodeMsg(500900, "错误的访问" );

    public static CodeMsg PRODUCT_LACK_OF_STOCK = new CodeMsg(600100, "商品已售罄");
    public static CodeMsg TOO_LARGE_QUANTITY = new CodeMsg(600200, "秒杀商品限拍一件");
    public static CodeMsg CLOSED_ACTIVITY = new CodeMsg(600500, "秒杀活动未开始");
    public static CodeMsg OUT_OF_STOCK = new CodeMsg(600300, "商品库存不足");
    public static CodeMsg ERROR_PAYMENT =new CodeMsg(600400, "支付失败");
    public static CodeMsg WRONG_VERIFY_CODE=new CodeMsg(600600, "验证码错误");

    CodeMsg() {

    }

    CodeMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
        this.view = "ErrorPage";
    }

    CodeMsg(int code, String msg, String view) {
        this.code = code;
        this.msg = msg;
        this.view = view;
    }

    public CodeMsg fillArgs(Object... args) {
        int code = this.code;
        String message = String.format(msg, args);
        return new CodeMsg(code, message);
    }
}
