package cn.wtyoha.miaosha.globalexception;

import cn.wtyoha.miaosha.domain.result.CodeMsg;
import cn.wtyoha.miaosha.domain.result.Result;
import com.alibaba.druid.sql.visitor.functions.Bin;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 定义一个全局的异常处理器
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(value = {GlobalException.class, BindException.class})
    public Result<String> myDefineExceptionHandler(Exception e) {
        e.printStackTrace();
        if (e instanceof GlobalException) {
            return Result.error(((GlobalException) e).getCodeMsg());
        } else if (e instanceof BindException) {
            BindException ex = (BindException) e;
            List<ObjectError> errors = ex.getAllErrors();
            ObjectError error = errors.get(0);
            String msg = error.getDefaultMessage();
            return Result.error(CodeMsg.BIND_ERROR.fillArgs(msg));
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
