package mincaml;

import java.util.HashMap;
import java.util.Map;

import jvm.ClassBuilder;
import jvm.ClassBuilder.MethodBuilder;
import jvm.CodeGenerator;
import jvm.UserDefinedClassLoader;

public class JVMByteCodeGenerator extends CodeGenerator {
	private Map<String, Class<?>> generatedClassMap = new HashMap<String, Class<?>>();
	private final static String packagePrefix = "mincaml/";

	private static int nameSuffix = -1;

	MinCamlTransducer mincaml;
	private ClassBuilder cBuilder;
	private UserDefinedClassLoader cLoader;
	private MethodBuilder mBuilder;

	public JVMByteCodeGenerator(MinCamlTransducer mincaml, String name) {
		this.mincaml = mincaml;
		this.cBuilder = new ClassBuilder(packagePrefix + name + ++nameSuffix, null, null, null);
		this.cLoader = new UserDefinedClassLoader();
	}

	public Class<?> generateClass() {
		UserDefinedClassLoader loader = new UserDefinedClassLoader();
		return loader.definedAndLoadClass(this.cBuilder.getInternalName(), cBuilder.toByteArray());
	}

	@Override
	public void generateTopLevel(MinCamlTree node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void generateVarDecl(MinCamlTree node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void generateVariable(MinCamlTree node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void generateFunctionDecl(MinCamlTree node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void generateFunctionCall(MinCamlTree node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void generateArguments(MinCamlTree node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void generateLiteral(MinCamlTree node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void generateOperator(MinCamlTree node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void generateCompOperator(MinCamlTree node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void generateIfExpression(MinCamlTree node) {
		// TODO Auto-generated method stub

	}

}
