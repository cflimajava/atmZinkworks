package com.zinkworks.atm.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zinkworks.atm.interfaces.services.IAtmService;

@Aspect
@Component
public class ValidationAccessAspect {
	
	@Autowired
	private IAtmService atmComponent;	

	@Pointcut("execution(* com.zinkworks.atm.controllers.ATMController*.*(..))")
	protected void allATMControllerMethods() {}
	
	@Before("allATMControllerMethods() && args(.., accountNumber, pin)) ")
	public void validatePinBefore(JoinPoint joinPoint, String accountNumber, Integer pin){	
		atmComponent.validatePin(accountNumber, pin);		
	}
	
}
