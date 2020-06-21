package cn.wtyoha.miaosha.globalexception;

import cn.wtyoha.miaosha.domain.result.CodeMsg;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

/**
 * 定义一个全局的异常处理器
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {GlobalException.class, BindException.class})
    public String myDefineExceptionHandler(Exception e, Model model) {
        e.printStackTrace();
        if (e instanceof GlobalException) {
            CodeMsg codeMsg = ((GlobalException) e).getCodeMsg();
            model.addAttribute("codeMsg", codeMsg);
            return codeMsg.getView();
        } else if (e instanceof BindException) {
            BindException ex = (BindException) e;
            List<ObjectError> errors = ex.getAllErrors();
            ObjectError error = errors.get(0);
            String msg = error.getDefaultMessage();
            CodeMsg codeMsg = CodeMsg.BIND_ERROR.fillArgs(msg);
            model.addAttribute("codeMsg", codeMsg);
            return codeMsg.getView();
        } else {
            throw new RuntimeException("错误的异常捕获");
        }
    }

    @ExceptionHandler(value = Exception.class)
    public String normalExceptionHandler(Exception e, Model model) {
        e.printStackTrace();
        model.addAttribute("errorMsg", e.getMessage());
        return "ErrorPage";
    }

}
