package cn.wtyoha.miaosha.globalexception;

import cn.wtyoha.miaosha.domain.result.CodeMsg;
import cn.wtyoha.miaosha.domain.result.Result;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 定义一个全局的异常处理器
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {GlobalException.class, BindException.class})
    @ResponseBody
    public Result<CodeMsg> myDefineExceptionHandler(Exception e) {
        e.printStackTrace();
        CodeMsg codeMsg = null;
        if (e instanceof GlobalException) {
            codeMsg = ((GlobalException) e).getCodeMsg();
        } else if (e instanceof BindException) {
            BindException ex = (BindException) e;
            List<ObjectError> errors = ex.getAllErrors();
            ObjectError error = errors.get(0);
            String msg = error.getDefaultMessage();
            codeMsg = CodeMsg.BIND_ERROR.fillArgs(msg);
        } else {
            throw new RuntimeException("错误的异常捕获");
        }
        return Result.error(codeMsg);
    }

    @ExceptionHandler(value = Exception.class)
    public String normalExceptionHandler(Exception e, Model model) {
        e.printStackTrace();
        model.addAttribute("errorMsg", e.getMessage());
        return "ErrorPage";
    }

}
