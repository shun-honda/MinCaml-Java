package mincaml;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

import jvm.ClassBuilder;
import jvm.ClassBuilder.MethodBuilder;
import jvm.ClassBuilder.VarEntry;
import jvm.CodeGenerator;
import jvm.InvocationTarget;
import jvm.JavaOperatorApi;
import jvm.JavaStaticField;
import jvm.Methods;
import jvm.UserDefinedClassLoader;

public class JVMByteCodeGenerator extends CodeGenerator {
	private Map<String, Class<?>> generatedClassMap = new HashMap<String, Class<?>>();
	private final static String packagePrefix = "mincaml/";

	private static int nameSuffix = -1;

	MinCamlTransducer mincaml;
	private ClassBuilder cBuilder;
	private UserDefinedClassLoader cLoader;
	private MethodBuilder mBuilder;
	private Stack<MethodBuilder> mBuilderStack = new Stack<MethodBuilder>();

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
		loader.setDump(true);
		return loader.definedAndLoadClass(this.cBuilder.getInternalName(), cBuilder.toByteArray());
	}

	@Override
	public void generateTopLevel(MinCamlTree node) {
		this.mBuilder = this.cBuilder.newMethodBuilder(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, void.class, "main");
		this.mBuilder.enterScope();
		for(MinCamlTree child : node) {
			child.generate(this);
		}
		this.mBuilder.exitScope();
		this.mBuilder.returnValue(); // return stack top value
		this.mBuilder.endMethod();
		this.cBuilder.visitEnd();
	}

	@Override
	public void generateVarDecl(MinCamlTree node) {
		MinCamlTree varNode = node.get(0);
		if(varNode.typed instanceof MinCamlArrayType) {
			node.get(1).generate(this);
			node.get(2).generate(this);
		} else {
			node.get(1).generate(this);
			this.scope.setLocalVar(varNode.getText(), this.mBuilder.createNewVarAndStore(varNode.typed.getJavaClass()));
			node.get(2).generate(this);
		}
	}

	@Override
	public void generateVariable(MinCamlTree node) {
		this.mBuilder.loadFromVar(this.scope.getLocalVar(node.getText()));
	}

	@Override
	public void generateFunctionDecl(MinCamlTree node) {
		this.mBuilderStack.push(this.mBuilder);
		MinCamlTree nameNode = node.get(0);
		MinCamlTree args = node.get(1);
		String name = nameNode.getText();
		MinCamlFuncType funcType = (MinCamlFuncType) nameNode.typed;
		Class<?>[] paramClasses = new Class<?>[funcType.argTypeList.size()];
		for(int i = 0; i < paramClasses.length; i++) {
			paramClasses[i] = funcType.argTypeList.get(i).getJavaClass();
		}
		this.mBuilder = this.cBuilder.newMethodBuilder(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
				funcType.retType.getJavaClass(), name, paramClasses);
		this.mBuilder.enterScope();
		this.pushScope();
		for(MinCamlTree arg : args) {
			this.scope.setLocalVar(arg.getText(), this.mBuilder.defineArgument(arg.typed.getJavaClass()));
		}
		node.get(2).generate(this);
		this.mBuilder.exitScope();
		this.popScope();
		this.mBuilder.returnValue();
		this.mBuilder.endMethod();
		this.mBuilder = this.mBuilderStack.pop();
		node.get(3).generate(this);
	}

	@Override
	public void generateFunctionCall(MinCamlTree node) {
		MinCamlFuncType funcType = (MinCamlFuncType) node.get(0).typed;
		if(funcType.standard) {
			String name = node.get(0).getText();
			JavaStaticField field = MinCamlStandardApi.getInstance(name);
			this.mBuilder.getStatic(field);
			node.get(1).generate(this);
			InvocationTarget target = MinCamlStandardApi.get(name);
			this.mBuilder.callInvocationTarget(target);
		} else {
			MinCamlTree args = node.get(1);
			Class<?>[] paramClasses = new Class<?>[args.size()];
			for(int i = 0; i < args.size(); i++) {
				args.get(i).generate(this);
				paramClasses[i] = args.get(i).typed.getJavaClass();
			}
			Method method = Methods.method(node.typed.getJavaClass(), node.get(0).getText(), paramClasses);
			this.mBuilder.invokeStatic(this.cBuilder.getTypeDesc(), method);
		}
	}

	@Override
	public void generateArguments(MinCamlTree node) {
		for(MinCamlTree child : node) {
			child.generate(this);
		}
	}

	@Override
	public void generateLiteral(MinCamlTree node) {
		Class<?> type = node.typed.getJavaClass();
		if(type == int.class) {
			this.mBuilder.push(Integer.parseInt(node.getText()));
		} else if(type == float.class) {
			this.mBuilder.push(Double.parseDouble(node.getText()));
		} else if(type == boolean.class) {
			this.mBuilder.push(Boolean.parseBoolean(node.getText()));
		}
	}

	@Override
	public void generateUnaryOperator(MinCamlTree node) {
		node.get(0).generate(this);
		Operator op = (Operator) node.matched;
		this.mBuilder.callStaticMethod(JavaOperatorApi.class, op.types[0].getJavaClass(), node.getTagName(),
				op.types[1].getJavaClass());
	}

	@Override
	public void generateBinaryOperator(MinCamlTree node) {
		node.get(0).generate(this);
		node.get(1).generate(this);
		Operator op = (Operator) node.matched;
		this.mBuilder.callStaticMethod(JavaOperatorApi.class, op.types[0].getJavaClass(), node.getTagName(),
				op.types[1].getJavaClass(), op.types[2].getJavaClass());
	}

	@Override
	public void generateCompOperator(MinCamlTree node) {
		node.get(0).generate(this);
		node.get(1).generate(this);
		CompOperator op = (CompOperator) node.matched;
		this.mBuilder.callStaticMethod(JavaOperatorApi.class, op.types[0].getJavaClass(), node.getTagName(),
				node.get(0).typed.getJavaClass(), node.get(1).typed.getJavaClass());
	}

	@Override
	public void generateIfExpression(MinCamlTree node) {
		node.get(0).generate(this);
		this.mBuilder.push(true);

		Label elseLabel = this.mBuilder.newLabel();
		Label mergeLabel = this.mBuilder.newLabel();

		this.mBuilder.ifCmp(Type.BOOLEAN_TYPE, this.mBuilder.NE, elseLabel);

		// then
		node.get(1).generate(this);
		this.mBuilder.goTo(mergeLabel);

		// else
		this.mBuilder.mark(elseLabel);
		node.get(2).generate(this);

		// merge
		this.mBuilder.mark(mergeLabel);
	}

	@Override
	public void generateArrayCreate(MinCamlTree node) {
		// type
		MinCamlArrayType arrayType = (MinCamlArrayType) node.typed;
		Class<?> arrayTypeClass = arrayType.getJavaClass();
		Class<?> elementTypeClass = arrayType.getElementType();
		Type elementType = Type.getType(elementTypeClass);

		// array size
		node.get(0).generate(this);

		// new array
		this.mBuilder.newArray(elementType);
		this.scope.setLocalVar(node.getText(), this.mBuilder.createNewVarAndStore(arrayTypeClass));

		// array initialize
		this.mBuilder.loadFromVar(this.scope.getLocalVar(node.getText()));
		node.get(1).generate(this);
		this.mBuilder.callStaticMethod(Arrays.class, void.class, "fill", arrayTypeClass, elementTypeClass);
	}

	@Override
	public void generateReadArray(MinCamlTree node) {
		// load array variable
		this.mBuilder.loadFromVar(this.scope.getLocalVar(node.get(0).getText()));

		// index
		node.get(1).generate(this);

		// load array element
		this.mBuilder.loadFromArrayVar(node.typed.getJavaClass());
	}

	@Override
	public void generateWriteArray(MinCamlTree node) {
		// TODO Auto-generated method stub

	}

}
