package mincaml;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import jvm.InvocationTarget;
import jvm.JavaStaticField;

public class MinCamlStandardApi {
	static Map<String, InvocationTarget> targetMap = new HashMap<String, InvocationTarget>();
	static Map<String, JavaStaticField> instanceMap = new HashMap<String, JavaStaticField>();

	static {
		Import("print_int", InvocationTarget.newVirtualTarget(PrintStream.class, void.class, "println", int.class),
				new JavaStaticField(System.class, "out", PrintStream.class));
		Import("print_float", InvocationTarget.newVirtualTarget(PrintStream.class, void.class, "println", double.class),
				new JavaStaticField(System.class, "out", PrintStream.class));
	}

	public static void Import(String name, InvocationTarget target, JavaStaticField field) {
		targetMap.put(name, target);
		instanceMap.put(name, field);
	}

	public static InvocationTarget get(String name) {
		return targetMap.get(name);
	}

	public static JavaStaticField getInstance(String name) {
		return instanceMap.get(name);
	}
}
