package mincaml;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import jvm.ClassBuilder;
import jvm.ClassBuilder.MethodBuilder;
import jvm.ClassBuilder.VarEntry;
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

	class MinCamlScope {
		MinCamlScope prev;
		Map<String, VarEntry> varMap;

		public MinCamlScope() {
			this.varMap = new HashMap<String, VarEntry>();
		}

		public MinCamlScope(MinCamlScope prev) {
			this.prev = prev;
			this.varMap = new HashMap<String, VarEntry>();
		}

		public void setLocalVar(String name, VarEntry var) {
			this.varMap.put(name, var);
		}

		public VarEntry getLocalVar(String name) {
			VarEntry var = this.varMap.get(name);
			if(var == null) {
				System.out.println("local variable '" + name + "' is not found");
				System.exit(1);
			}
			return var;
		}
	}

	MinCamlScope scope = new MinCamlScope();

	public void pushScope() {
		this.scope = new MinCamlScope(this.scope);
	}

	public MinCamlScope popScope() {
		MinCamlScope ret = this.scope;
		this.scope = this.scope.prev;
		return ret;
	}

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
		for(MinCamlTree child : node) {
			child.generate(this);
		}
		this.cBuilder.visitEnd();
	}

	@Override
	public void generateVarDecl(MinCamlTree node) {
		MinCamlTree varNode = node.get(0);
		node.get(1).generate(this);
		this.scope.setLocalVar(varNode.getText(), this.mBuilder.createNewVarAndStore(varNode.typed.getJavaClass()));
		node.get(2).generate(this);
	}

	@Override
	public void generateVariable(MinCamlTree node) {
		this.mBuilder.loadFromVar(this.scope.getLocalVar(node.getText()));
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
		Operator op = (Operator) node.matched;
		Method method = op.getMethod();
		// this.mBuilder.invokeStatic(Type.getType(JavaOperatorApi.class),
		// method);
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
