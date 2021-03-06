package mincaml;

import java.util.ArrayList;
import java.util.List;

import jvm.CodeGenerator;
import nez.ast.Tag;

public class MinCamlLanguage {

	public MinCamlLanguage(MinCamlTransducer mincaml) {
		mincaml.setTypeRule(new TopLevel(key("Source")));
		mincaml.setTypeRule(new VarDecl(key("VarDecl")));
		mincaml.setTypeRule(new FunctionDecl(key("FunctionDecl")));
		mincaml.setTypeRule(new FunctionCall(key("FunctionCall")));
		mincaml.setTypeRule(new Variable(key("Name")));
		mincaml.setTypeRule(new IfExpression(key("If")));
		mincaml.setTypeRule(new ArrayCreate(key("ArrayCreate"), mincaml.getType("int")));
		mincaml.setTypeRule(new ReadArray(key("ReadArrayElement")));
		mincaml.setTypeRule(new WriteArray(key("WriteArrayElement")));

		this.defineLiteral(mincaml, "#True", "bool");
		this.defineLiteral(mincaml, "#False", "bool");
		this.defineLiteral(mincaml, "#Integer", "int");
		this.defineLiteral(mincaml, "#Float", "float");

		this.defineUnary(mincaml, "#Minus", "int", "int", "-");
		this.defineUnary(mincaml, "#FMinus", "float", "float", "-.");
		this.defineUnary(mincaml, "#LogicalNot", "bool", "bool", "!");
		this.defineBinary(mincaml, "#Add", "int", "int", "int", "+");
		this.defineBinary(mincaml, "#Sub", "int", "int", "int", "-");
		this.defineBinary(mincaml, "#Mul", "int", "int", "int", "*");
		this.defineBinary(mincaml, "#Div", "int", "int", "int", "/");
		this.defineBinary(mincaml, "#FAdd", "float", "float", "float", "+.");
		this.defineBinary(mincaml, "#FSub", "float", "float", "float", "-.");
		this.defineBinary(mincaml, "#FMul", "float", "float", "float", "*.");
		this.defineBinary(mincaml, "#FDiv", "float", "float", "float", "/.");
		this.defineComp(mincaml, "#Equals", "bool", "=");
		this.defineComp(mincaml, "#LessThan", "bool", "<");
		this.defineComp(mincaml, "#MoreThan", "bool", ">");
		this.defineComp(mincaml, "#LessThanEquals", "bool", "<=");
		this.defineComp(mincaml, "#MoreThanEquals", "bool", ">=");
		this.defineComp(mincaml, "#NotEquals", "bool", "<>");

		this.defineStandardLibrary(mincaml, "print_int", "unit", "int");
		this.defineStandardLibrary(mincaml, "print_float", "unit", "float");

	}

	private String key(String tagname) {
		return MinCamlTree.keyTag(tagname);
	}

	private void defineLiteral(MinCamlTransducer mincaml, String tname, String type) {
		MinCamlType t = mincaml.getType(type);
		mincaml.setTypeRule(new Literal(tname, t));
	}

	private void defineUnary(MinCamlTransducer mincaml, String tname, String rtype, String type1, String op) {
		MinCamlType rt = mincaml.getType(rtype);
		MinCamlType t1 = mincaml.getType(type1);
		MinCamlType[] types = { rt, t1 };
		mincaml.setTypeRule(new UnaryOperator(tname, types, op));
	}

	private void defineBinary(MinCamlTransducer mincaml, String tname, String rtype, String type1, String type2,
			String op) {
		MinCamlType rt = mincaml.getType(rtype);
		MinCamlType t1 = mincaml.getType(type1);
		MinCamlType t2 = mincaml.getType(type2);
		MinCamlType[] types = { rt, t1, t2 };
		mincaml.setTypeRule(new BinaryOperator(tname, types, op));
	}

	private void defineComp(MinCamlTransducer mincaml, String tname, String rtype, String op) {
		MinCamlType rt = mincaml.getType(rtype);
		MinCamlType[] types = { rt };
		mincaml.setTypeRule(new CompOperator(tname, types, op));
	}

	private void defineStandardLibrary(MinCamlTransducer mincaml, String funcName, String rtype, String... args) {
		MinCamlTree func = new MinCamlTree(Tag.tag(key("FunctionDecl")), null, 0, 0, 2, null);
		MinCamlTree name = new MinCamlTree(Tag.tag(key("Name")), null, 0, 0, 0, funcName);
		MinCamlTree argList = new MinCamlTree(Tag.tag(key("FormalArgList")), null, 0, 0, args.length, null);
		List<MinCamlType> argTypeList = new ArrayList<MinCamlType>();
		for(int i = 0; i < args.length; i++) {
			MinCamlTree arg = new MinCamlTree(Tag.tag(key("Name")), null, 0, 0, 0, "arg" + i);
			MinCamlTypeVariable argType = new MinCamlTypeVariable(arg.getText());
			argType.setType(mincaml.getType(args[i]));
			arg.setType(argType);
			argList.set(i, arg);
			argTypeList.add(arg.typed);
		}
		MinCamlFuncType funcType = new MinCamlFuncType(funcName, true);
		funcType.setReturnType(mincaml.getType(rtype));
		funcType.setArgsType(argTypeList);
		name.setType(funcType);
		func.set(0, name);
		func.set(1, argList);
		mincaml.setTypeRule(new FunctionDecl(funcName, true));
		mincaml.setName(funcName, func);
	}

}

class TopLevel extends MinCamlTypeRule {

	public TopLevel(String name) {
		super(name, -1);
	}

	public MinCamlType match(MinCamlTransducer mincaml, MinCamlTree node) {
		MinCamlType type = MinCamlType.DefualtType;
		for(MinCamlTree sub : node) {
			type = mincaml.typeCheck(sub);
		}
		return node.setType(type);
	}

	@Override
	public void generate(MinCamlTree node, CodeGenerator generator) {
		generator.generateTopLevel(node);
	}
}

class VarDecl extends MinCamlTypeRule {

	public VarDecl(String name) {
		super(name, 2);
	}

	public MinCamlType match(MinCamlTransducer mincaml, MinCamlTree node) {
		MinCamlTree nameNode = node.get(0);
		MinCamlTree elementNode = node.get(1);
		String name = nameNode.getText();
		mincaml = new MinCamlTransducer(mincaml);
		Tag tag = elementNode.getTag();
		if(tag.equals(Tag.tag("ArrayCreate"))) {
			mincaml.setArrayName(elementNode, name);
		}
		MinCamlType type = mincaml.typeCheck(elementNode);
		mincaml = mincaml.parent;
		nameNode.setType(type);
		mincaml.setName(name, nameNode);
		MinCamlType retType = mincaml.typeCheck(node.get(2));
		return node.setType(retType);
	}

	@Override
	public void generate(MinCamlTree node, CodeGenerator generator) {
		generator.generateVarDecl(node);
	}
}

class Variable extends MinCamlTypeRule {

	public Variable(String name) {
		super(name, 0);
	}

	public MinCamlType match(MinCamlTransducer mincaml, MinCamlTree node) {
		MinCamlTree var = mincaml.getName(node);
		return node.setType(var.typed);
	}

	@Override
	public void generate(MinCamlTree node, CodeGenerator generator) {
		generator.generateVariable(node);
	}
}

class FunctionDecl extends MinCamlTypeRule {
	boolean standard;

	public FunctionDecl(String name) {
		super(name, 3);
		this.standard = false;
	}

	public FunctionDecl(String name, boolean standard) {
		super(name, 3);
		this.standard = standard;
	}

	public MinCamlType match(MinCamlTransducer mincaml, MinCamlTree node) {
		MinCamlTree nameNode = node.get(0);
		String name = nameNode.getText();
		MinCamlFuncType funcType = new MinCamlFuncType(name);
		nameNode.setType(funcType);
		mincaml.setName(name, node);
		mincaml = new MinCamlTransducer(mincaml);
		MinCamlTree argsNode = node.get(1);
		argsNode.matched = new Arguments(name, argsNode.size());
		List<MinCamlType> types = new ArrayList<MinCamlType>();
		for(MinCamlTree arg : argsNode) {
			arg.setType(new MinCamlTypeVariable(arg.getText()));
			mincaml.setName(arg.getText(), arg);
			types.add(arg.typed);
		}
		MinCamlType type = mincaml.typeCheck(node.get(2));
		MinCamlTypeVariable funcRetType = funcType.getReturnType();
		if(!funcRetType.isNull()) {
			if(type != funcRetType.type) {
				System.out.println("Type Error: function return type is expected " + type + " type, but " + funcRetType
						+ " type is found" + node + "\n");
			}
		} else {
			funcType.setReturnType(type);
		}
		funcType.setArgsType(types);
		mincaml = mincaml.parent;
		MinCamlType retType = mincaml.typeCheck(node.get(3));
		return node.setType(retType);
	}

	@Override
	public void generate(MinCamlTree node, CodeGenerator generator) {
		generator.generateFunctionDecl(node);
	}
}

class FunctionCall extends MinCamlTypeRule {
	boolean standard;

	public FunctionCall(String name) {
		super(name, 1);
	}

	public MinCamlType match(MinCamlTransducer mincaml, MinCamlTree node) {
		MinCamlTree nameNode = node.get(0);
		String name = nameNode.getText();
		MinCamlTree func = mincaml.getName(nameNode);
		MinCamlFuncType funcType = (MinCamlFuncType) func.get(0).typed;
		this.standard = funcType.standard;
		nameNode.setType(funcType);
		MinCamlTree fArgs = func.get(1);
		MinCamlTree aArgs = node.get(1);
		aArgs.matched = new Arguments(name, aArgs.size());
		if(fArgs.size() != aArgs.size()) {
			System.out.println("Argument Error: size of function '" + name + "' arguments is not match");
		}
		for(int i = 0; i < aArgs.size(); i++) {
			MinCamlTypeVariable fArgType = (MinCamlTypeVariable) fArgs.get(i).typed;
			MinCamlType aArgType = mincaml.typeCheck(aArgs.get(i));
			if(fArgType.isNull()) {
				fArgType.setType(aArgType);
				funcType.setArgType(i, aArgType);
			} else if(aArgType instanceof MinCamlTypeVariable) {
				if(!fArgType.equals(((MinCamlTypeVariable) aArgType).type)) {
					System.out.println("Type Error: Argument" + i + 1 + " of function '" + name + "' is " + fArgType
							+ " type, but " + aArgType + " type found" + aArgs + "\n");
				}
			} else {
				if(!fArgType.equalsType(aArgType)) {
					System.out.println("Type Error: Argument" + i + 1 + " of function '" + name + "' is " + fArgType
							+ " type, but " + aArgType + " type found" + aArgs + "\n");
				}
			}
		}
		if(funcType.retType.isNull()) {
			return node.setType(funcType.retType);
		}
		return node.setType(funcType.retType.type);
	}

	@Override
	public void generate(MinCamlTree node, CodeGenerator generator) {
		generator.generateFunctionCall(node);
	}
}

class Arguments extends MinCamlTypeRule {

	public Arguments(String name, int size) {
		super(name, size);
	}

	@Override
	public void generate(MinCamlTree node, CodeGenerator generator) {
		generator.generateArguments(node);
	}
}

class Literal extends MinCamlTypeRule {
	MinCamlType type;

	public Literal(String name, MinCamlType type) {
		super(name, 0);
		this.type = type;
	}

	public MinCamlType match(MinCamlTransducer mincaml, MinCamlTree node) {
		node.setType(this.type);
		return this.type;
	}

	@Override
	public void generate(MinCamlTree node, CodeGenerator generator) {
		generator.generateLiteral(node);
	}
}

abstract class Operator extends MinCamlTypeRule {
	MinCamlType[] types;
	String op;

	public Operator(String name, MinCamlType[] types, String op) {
		super(name, types.length - 1);
		this.types = types;
		this.op = op;
	}

	public MinCamlType match(MinCamlTransducer mincaml, MinCamlTree node) {
		for(int i = 0; i < node.size(); i++) {
			MinCamlTree sub = node.get(i);
			MinCamlType nodeType = mincaml.typeCheck(sub);
			MinCamlType argType = types[i + 1];
			if(nodeType == null) {
				if(sub.matched instanceof Variable) {
					sub.setType(argType);
					mincaml.getName(sub).setType(argType);
				}
			} else if(nodeType instanceof MinCamlTypeVariable) {
				((MinCamlTypeVariable) nodeType).setType(argType);
			} else {
				if(!nodeType.equals(argType)) {
					System.out.println("Type Error: Argument" + i + 1 + " of operator '" + this.op + "' is " + argType
							+ " type, but " + nodeType + " type found" + sub + "\n");
				}
			}
		}
		return node.setType(this.types[0]);
	}

	@Override
	public abstract void generate(MinCamlTree node, CodeGenerator generator);
}

class UnaryOperator extends Operator {

	public UnaryOperator(String name, MinCamlType[] types, String op) {
		super(name, types, op);
	}

	@Override
	public void generate(MinCamlTree node, CodeGenerator generator) {
		generator.generateUnaryOperator(node);
	}

}

class BinaryOperator extends Operator {

	public BinaryOperator(String name, MinCamlType[] types, String op) {
		super(name, types, op);
	}

	@Override
	public void generate(MinCamlTree node, CodeGenerator generator) {
		generator.generateBinaryOperator(node);
	}

}

class CompOperator extends Operator {
	MinCamlType argType;

	public CompOperator(String name, MinCamlType[] types, String op) {
		super(name, types, op);
	}

	public MinCamlType match(MinCamlTransducer mincaml, MinCamlTree node) {
		MinCamlTree node1 = node.get(0);
		MinCamlType nodeType1 = mincaml.typeCheck(node1);
		MinCamlTree node2 = node.get(1);
		MinCamlType nodeType2 = mincaml.typeCheck(node2);
		if(nodeType1 == null && nodeType2 == null) {
			return node.setType(this.types[0]);
		} else if(nodeType1 instanceof MinCamlTypeVariable && nodeType2 instanceof MinCamlTypeVariable) {
			return node.setType(this.types[0]);
		} else if(nodeType1 == null) {
			nodeType1 = nodeType2;
			node1.setType(nodeType2);
			mincaml.getName(node1).setType(nodeType1);
		} else if(nodeType1 instanceof MinCamlTypeVariable) {
			((MinCamlTypeVariable) nodeType1).setType(nodeType2);
		} else if(nodeType2 == null) {
			nodeType2 = nodeType1;
			node2.setType(nodeType2);
			mincaml.getName(node2).setType(nodeType2);
		} else if(nodeType2 instanceof MinCamlTypeVariable) {
			((MinCamlTypeVariable) nodeType2).setType(nodeType2);
		} else if(!nodeType1.equals(nodeType2)) {
			System.out.println("Type Error: second expression has " + nodeType2
					+ " type, but second expression was expected " + nodeType1 + node + "\n");
		}
		this.argType = nodeType1;
		return node.setType(this.types[0]);
	}

	@Override
	public void generate(MinCamlTree node, CodeGenerator generator) {
		generator.generateCompOperator(node);
	}

}

class IfExpression extends MinCamlTypeRule {

	public IfExpression(String name) {
		super(name, 3);
	}

	public MinCamlType match(MinCamlTransducer mincaml, MinCamlTree node) {
		MinCamlTree node1 = node.get(0);
		MinCamlType nodeType1 = mincaml.typeCheck(node1);
		MinCamlType bool = mincaml.getType("bool");
		if(nodeType1 instanceof MinCamlTypeVariable) {
			node1.setType(bool);
			MinCamlFuncType funcType = (MinCamlFuncType) node1.get(0).typed;
			funcType.setReturnType(bool);
		} else if(nodeType1 != bool) {
			System.out.println("Type Error: The first expr of If is " + nodeType1
					+ " type, but it is exprected of bool type" + node + "\n");
		}
		MinCamlTree node2 = node.get(1);
		MinCamlTree node3 = node.get(2);
		MinCamlType nodeType2 = mincaml.typeCheck(node2);
		MinCamlType nodeType3 = mincaml.typeCheck(node3);
		MinCamlType retType = nodeType2;
		if(nodeType2 == null && nodeType3 == null) {
			System.out.println("Type Error: can not infer type " + node + "\n");
			return MinCamlType.DefualtType;
		} else if(nodeType2 == null) {
			nodeType2 = nodeType3;
			node2.setType(nodeType3);
			mincaml.getName(node2).setType(nodeType3);
			retType = nodeType3;
		} else if(nodeType2 instanceof MinCamlTypeVariable) {
			((MinCamlTypeVariable) nodeType2).setType(nodeType3);
			retType = nodeType3;
		} else if(nodeType3 == null) {
			nodeType3 = nodeType2;
			node2.setType(nodeType2);
			mincaml.getName(node2).setType(nodeType2);
		} else if(nodeType3 instanceof MinCamlTypeVariable) {
			((MinCamlTypeVariable) nodeType3).setType(nodeType2);
			retType = nodeType2;
		} else if(!nodeType2.equals(nodeType3)) {
			System.out.println("Type Error: else expression has " + nodeType3
					+ " type, but else expression was expected " + nodeType2 + node + "\n");
		}
		return node.setType(retType);
	}

	@Override
	public void generate(MinCamlTree node, CodeGenerator generator) {
		generator.generateIfExpression(node);
	}
}

class ArrayCreate extends MinCamlTypeRule {
	int size;
	MinCamlType type;

	public ArrayCreate(String name, MinCamlType type) {
		super(name, 2);
		this.type = type;
	}

	@Override
	public MinCamlType match(MinCamlTransducer mincaml, MinCamlTree node) {
		MinCamlTree node1 = node.get(0);
		MinCamlType type1 = mincaml.typeCheck(node1);
		MinCamlType intType = mincaml.getType("int");
		if(type1 instanceof MinCamlTypeVariable) {
			((MinCamlTypeVariable) type1).setType(intType);
		} else if(!type1.equalsType(intType)) {
			System.out.println("TypeError: first argument of Array.create is expected int type, but found " + type1
					+ " type" + node1 + "\n");
		}
		MinCamlType type2 = mincaml.typeCheck(node.get(1));
		MinCamlArrayType arrayType = new MinCamlArrayType(name, type2);
		node.setValue(mincaml.getArrayName(node));
		return node.setType(arrayType);
	}

	@Override
	public void generate(MinCamlTree node, CodeGenerator generator) {
		generator.generateArrayCreate(node);
	}

}

class ReadArray extends MinCamlTypeRule {

	public ReadArray(String name) {
		super(name, 0);
	}

	@Override
	public MinCamlType match(MinCamlTransducer mincaml, MinCamlTree node) {
		MinCamlType type = mincaml.typeCheck(node.get(0));
		MinCamlArrayType arrayType = null;
		if(type instanceof MinCamlArrayType) {
			arrayType = (MinCamlArrayType) type;
		} else {
			System.out.println("TypeError: this variable is expected array type, but found " + type + " type"
					+ node.get(0) + "\n");
		}
		MinCamlType indexType = mincaml.typeCheck(node.get(1));
		if(!indexType.equalsType(mincaml.getType("int"))) {
			System.out.println("TypeError: index of read array expression is expected int type, but found " + indexType
					+ " type" + node.get(1) + "\n");
		}
		return node.setType(arrayType.type);
	}

	@Override
	public void generate(MinCamlTree node, CodeGenerator generator) {
		generator.generateReadArray(node);
	}

}

class WriteArray extends MinCamlTypeRule {

	public WriteArray(String name) {
		super(name, 0);
	}

	@Override
	public MinCamlType match(MinCamlTransducer mincaml, MinCamlTree node) {
		MinCamlType type = mincaml.typeCheck(node.get(0));
		MinCamlArrayType arrayType = null;
		if(type instanceof MinCamlArrayType) {
			arrayType = (MinCamlArrayType) type;
		} else {
			System.out.println("TypeError: this variable is expected array type, but found " + type + " type"
					+ node.get(0) + "\n");
		}
		MinCamlType indexType = mincaml.typeCheck(node.get(1));
		MinCamlType intType = mincaml.getType("int");
		if(indexType instanceof MinCamlTypeVariable) {
			((MinCamlTypeVariable) indexType).setType(intType);
		} else if(!indexType.equalsType(intType)) {
			System.out.println("TypeError: index of write array expression is expected int type, but found " + indexType
					+ " type" + node.get(1) + "\n");
		}
		MinCamlType elementType = mincaml.typeCheck(node.get(2));
		if(!elementType.equalsType(arrayType.type)) {
			System.out.println("TypeError: Array Elements are expected " + arrayType.type + " type, but found "
					+ elementType + " type" + node.get(2) + "\n");
		}
		return node.setType(mincaml.getType("unit"));
	}

	@Override
	public void generate(MinCamlTree node, CodeGenerator generator) {
		generator.generateWriteArray(node);
	}

}