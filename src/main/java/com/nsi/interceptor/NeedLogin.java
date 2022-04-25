package com.nsi.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * this will check token either login as an agent (default) or login as a customer.
 * see {@link NeedLoginInterceptor}
 * @author hussein
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NeedLogin {
	UserLogin value() default UserLogin.AGENT_APPS;
	enum UserLogin{
		AGENT_APPS, CUSTOMER, CUSTOMER_WITHOUT_SIGNATURE, AGENT
	}
}
