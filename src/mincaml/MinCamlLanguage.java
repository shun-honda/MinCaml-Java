package mincaml;

public class MinCamlLanguage {

	public MinCamlLanguage(MinCamlTransducer mincaml) {
		mincaml.setTypeRule(new TopLevel(key("Source")));
		mincaml.setTypeRule(new VarDecl(key("VarDecl")));
		mincaml.setTypeRule(new Variable(key("Name")));

		this.defineLiteral(mincaml, "#True", "bool");
		this.defineLiteral(mincaml, "#False", "bool");
		this.defineLiteral(mincaml, "#Integer", "int");
		this.defineLiteral(mincaml, "#Float", "float");

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
	}

	private String key(String tagname) {
		return MinCamlTree.keyTag(tagname);
	}

	private void defineLiteral(MinCamlTransducer mincaml, String tname, String type) {
		MinCamlType t = mincaml.getType(type);
		mincaml.setTypeRule(new Literal(tname, t));
	}

	private void defineBinary(MinCamlTransducer mincaml, String tname, String rtype, String type1, String type2,
			String op) {
		MinCamlType rt = mincaml.getType(rtype);
		MinCamlType t1 = mincaml.getType(type1);
		MinCamlType t2 = mincaml.getType(type2);
		MinCamlType[] types = { rt, t1, t2 };
		mincaml.setTypeRule(new Operator(tname, types, op));
	}

	private void defineComp(MinCamlTransducer mincaml, String tname, String rtype, String op) {
		MinCamlType rt = mincaml.getType(rtype);
		MinCamlType[] types = { rt };
		mincaml.setTypeRule(new CompOperator(tname, types, op));
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
}

class VarDecl extends MinCamlTypeRule {

	public VarDecl(String name) {
		super(name, 2);
	}

	public MinCamlType match(MinCamlTransducer mincaml, MinCamlTree node) {
		MinCamlTree nameNode = node.get(0);
		MinCamlType type = mincaml.typeCheck(node.get(1));
		nameNode.setType(type);
		mincaml.setType(nameNode.getText(), type);
		MinCamlType retType = mincaml.typeCheck(node.get(2));
		return node.setType(retType);
	}
}

class Variable extends MinCamlTypeRule {

	public Variable(String name) {
		super(name, 0);
	}

	public MinCamlType match(MinCamlTransducer mincaml, MinCamlTree node) {
		MinCamlType type = mincaml.getType(node);
		return node.setType(type);
	}
}

class FunctionDecl extends MinCamlTypeRule {

	public FunctionDecl(String name, int size) {
		super(name, size);
	}

	public MinCamlType match(MinCamlTransducer mincaml, MinCamlTree node) {
		for(MinCamlTree sub : node) {
			mincaml.typeCheck(sub);
		}
		node.setType(MinCamlType.DefualtType);
		return MinCamlType.DefualtType;
	}
}

class FunctionCall extends MinCamlTypeRule {

	public FunctionCall(String name, int size) {
		super(name, size);
	}

	public MinCamlType match(MinCamlTransducer mincaml, MinCamlTree node) {
		for(MinCamlTree sub : node) {
			mincaml.typeCheck(sub);
		}
		node.setType(MinCamlType.DefualtType);
		return MinCamlType.DefualtType;
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
}

class Operator extends MinCamlTypeRule {
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
			if(!nodeType.equals(argType)) {
				System.out.println("Type Error: Argument" + i + 1 + " of operator '" + this.op + "' is " + argType
						+ " type, but " + nodeType + " type found" + sub + "\n");
			}
		}
		return node.setType(this.types[0]);
	}
}

class CompOperator extends Operator {

	public CompOperator(String name, MinCamlType[] types, String op) {
		super(name, types, op);
	}

	public MinCamlType match(MinCamlTransducer mincaml, MinCamlTree node) {
		MinCamlType nodeType1 = mincaml.typeCheck(node.get(0));
		MinCamlType nodeType2 = mincaml.typeCheck(node.get(1));
		if(!nodeType1.equals(nodeType2)) {
			System.out.println("Type Error: second expression has " + nodeType2
					+ " type, but second expression was expected " + nodeType1 + node + "\n");
		}
		return node.setType(this.types[0]);
	}

}