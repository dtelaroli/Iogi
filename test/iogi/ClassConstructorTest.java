package iogi;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import iogi.conversion.Converter;
import iogi.conversion.StringConverter;
import iogi.conversion.TypeConverter;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.junit.Test;


public class ClassConstructorTest {
	private Constructor<Foo> fooConstructor;
	private Converter converter;
	
	public ClassConstructorTest() throws SecurityException, NoSuchMethodException {
		fooConstructor = Foo.class.getConstructor(String.class, String.class);
		converter = new Converter(Collections.<TypeConverter<?>>singleton(new StringConverter()));		
	}
	
	@Test
	public void twoClassConstructorsWithTheSameSetOfNamesAreEqual() throws Exception {
		HashSet<String> set1 = new HashSet<String>(asList("foo", "bar", "baz"));
		HashSet<String> set2 = new HashSet<String>(asList("foo", "bar", "baz"));
		
		ClassConstructor classConstructor1 = new ClassConstructor(set1);
		ClassConstructor classConstructor2 = new ClassConstructor(set2);
		
		assertEquals(classConstructor1, classConstructor2);
	}
	
	@Test
	public void canCreateAClassConstructorFromAConstructor() throws Exception {
		HashSet<String> parameterNames = new HashSet<String>(asList("one", "two"));
		ClassConstructor fromNames = new ClassConstructor(parameterNames);
		ClassConstructor fromConstructor = new ClassConstructor(fooConstructor);
		
		assertEquals(fromNames, fromConstructor);
	}
	
	@Test
	public void canInstantiateFromArgumentNames() throws Exception {
		ClassConstructor constructor = new ClassConstructor(fooConstructor); 
		Map<String, String> arguments = new HashMap<String,String>();
		arguments.put("two", "b");
		arguments.put("one", "a");
		Foo foo = (Foo)constructor.instantiate(converter, arguments);
		assertEquals("a", foo.getOne());
		assertEquals("b", foo.getTwo());
	}
	
	public static class Foo {
		private final String one;
		private final String two;

		public Foo(String one, String two) {
			this.one = one;
			this.two = two;
		}
		
		public String getOne() {
			return one;
		}
		
		public String getTwo() {
			return two;
		}
	}
}
