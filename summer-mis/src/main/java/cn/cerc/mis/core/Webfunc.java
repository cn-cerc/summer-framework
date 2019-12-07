package cn.cerc.mis.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.cerc.mis.other.BookVersion;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Webfunc {
    // 允许版本号列表
    BookVersion version() default BookVersion.ctAll;

    // // 权限代码
    // String security() default Passport.all_user_pass;
    //
    // // 动作代码
    // ServiceAction action() default ServiceAction.Execute;

    // 合理最大执行时间
    long timeout() default 1000;
}
