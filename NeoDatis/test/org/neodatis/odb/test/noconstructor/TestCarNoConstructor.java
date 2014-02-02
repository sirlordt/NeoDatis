package org.neodatis.odb.test.noconstructor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.neodatis.odb.ODB;
import org.neodatis.odb.test.ODBTest;

import sun.reflect.ReflectionFactory;

public class TestCarNoConstructor extends ODBTest {

	private static ReflectionFactory reflectionFactory = ReflectionFactory.getReflectionFactory();

	public void testNoConstructor() throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException,
			IllegalAccessException, InvocationTargetException {
		Class type = Car.class;
		Constructor javaLangObjectConstructor = Object.class.getDeclaredConstructor(new Class[0]);
		Constructor customConstructor = reflectionFactory.newConstructorForSerialization(type, javaLangObjectConstructor);

		Car h = (Car) customConstructor.newInstance(new Object[0]);

		assertNotNull(h);

	}

	public void testNoConstructor2() throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException,
			IllegalAccessException, InvocationTargetException {
		Class type = Car2.class;
		Constructor javaLangObjectConstructor = Object.class.getDeclaredConstructor(new Class[0]);
		Constructor customConstructor = reflectionFactory.newConstructorForSerialization(type, javaLangObjectConstructor);

		Car2 h = (Car2) customConstructor.newInstance(new Object[0]);

		assertNotNull(h);

	}

	public void testNoConstructor3() throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException,
			IllegalAccessException, InvocationTargetException {
		ODB odb = open("proctected");
		odb.store(new CarWithProtectedConstructor("car1"));
		odb.close();

		odb = open("proctected");
		CarWithProtectedConstructor car = (CarWithProtectedConstructor) odb.getObjects(CarWithProtectedConstructor.class).getFirst();
		assertEquals("car1", car.getName());
	}
}
