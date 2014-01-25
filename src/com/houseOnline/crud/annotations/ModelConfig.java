/*
 * ModelConfig.java
 * 
 * All Rights Reserved.
 */
package com.houseOnline.crud.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * ClassName: ModelConfig
 * </p>
 * 
 * <p>
 * Abstract:
 * </p>
 * <ul>
 * <li></li>
 * </ul>
 * 
 * @author Samuel Feng
 * @since Dec 22, 2013
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ModelConfig {

	String xsd();

	String indicator();

	String collection();
}
