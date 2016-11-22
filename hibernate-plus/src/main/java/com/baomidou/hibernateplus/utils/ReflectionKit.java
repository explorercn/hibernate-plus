/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Caratacus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.baomidou.hibernateplus.utils;

import com.baomidou.framework.entity.EntityInfo;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.logging.Logger;

/**
 * <p>
 * 反射工具类
 * </p>
 *
 * @author Caratacus
 * @Date 2016-09-22
 */
public class ReflectionKit {

	protected static final Logger logger = Logger.getLogger("ReflectionKit");

	/**
	 * <p>
	 * 反射 method 方法名，例如 getId
	 * </p>
	 *
	 * @param str
	 *            属性字符串内容
	 * @return
	 */
	public static String getMethodCapitalize(final String str) {
		return StringUtils.concatCapitalize("get", str);
	}

	/**
	 * 获取 public get方法的值
	 *
	 * @param cls
	 * @param entity
	 *            实体
	 * @param str
	 *            属性字符串内容
	 * @return Object
	 */
	public static Object getMethodValue(Class<?> cls, Object entity, String str) {
		Object obj = null;
		try {
			Method method = cls.getMethod(getMethodCapitalize(str));
			obj = method.invoke(entity);
		} catch (NoSuchMethodException e) {
			logger.warning(String.format("Warn: No such method. in %s.  Cause:", cls.getSimpleName()) + e);
		} catch (IllegalAccessException e) {
			logger.warning(String.format("Warn: Cannot execute a private method. in %s.  Cause:", cls.getSimpleName()) + e);
		} catch (InvocationTargetException e) {
			logger.warning("Warn: Unexpected exception on getMethodValue.  Cause:" + e);
		}
		return obj;
	}

	/**
	 * 获取 public get方法的值
	 *
	 * @param entity
	 *            实体
	 * @param str
	 *            属性字符串内容
	 * @return Object
	 */
	public static Object getMethodValue(Object entity, String str) {
		if (null == entity) {
			return null;
		}
		return getMethodValue(entity.getClass(), entity, str);
	}

	/**
	 * 调用对象的get方法检查对象所有属性是否为null
	 *
	 * @param bean
	 *            检查对象
	 * @return boolean true对象所有属性不为null,false对象所有属性为null
	 */
	public static boolean checkFieldValueNotNull(Object bean) {
		if (null == bean) {
			return false;
		}
		Class<?> cls = bean.getClass();
		EntityInfo entityInfo = EntityInfoUtils.getEntityInfo(cls);
		boolean result = false;
		for (Field field : entityInfo.getFields()) {
			String fieldName = field.getName();
			Object val = getMethodValue(cls, bean, fieldName);
			if (StringUtils.checkValNotNull(val)) {
				result = true;
				break;
			}
		}
		return result;
	}

	/**
	 * 反射对象获取泛型
	 *
	 * @param clazz
	 *            对象
	 * @param index
	 *            泛型所在位置
	 * @return Class
	 */
	@SuppressWarnings("rawtypes")
	public static Class getSuperClassGenricType(final Class clazz, final int index) {

		Type genType = clazz.getGenericSuperclass();

		if (!(genType instanceof ParameterizedType)) {
			logger.warning(String.format("Warn: %s's superclass not ParameterizedType", clazz.getSimpleName()));
			return Object.class;
		}

		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

		if (index >= params.length || index < 0) {
			logger.warning(String.format("Warn: Index: %s, Size of %s's Parameterized Type: %s .", index, clazz.getSimpleName(),
					params.length));
			return Object.class;
		}
		if (!(params[index] instanceof Class)) {
			logger.warning(String.format("Warn: %s not set the actual class on superclass generic parameter",
					clazz.getSimpleName()));
			return Object.class;
		}

		return (Class) params[index];
	}
}