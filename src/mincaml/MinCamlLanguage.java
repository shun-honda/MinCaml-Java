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
		// this.defineLiteral(mincaml, "#String", "string");

		this.defineBinary(mincaml, "#Add", "int", "int", "int", "+");
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
		mincaml.setTypeRule(new Operator(tname, types));
	}

}

class TopLevel extends MinCamlTypeRule {

	public TopLevel(String name) {
		super(name, -1);
	}

	public MinCamlType match(MinCamlTransducer mincaml, MinCamlTree node) {
		for(MinCamlTree sub : node) {
			mincaml.typeCheck(sub);
		}
		node.setType(MinCamlType.DefualtType);
		return MinCamlType.DefualtType;
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

	public Operator(String name, MinCamlType[] types) {
		super(name, types.length - 1);
		this.types = types;
	}

	public MinCamlType match(MinCamlTransducer mincaml, MinCamlTree node) {
		for(MinCamlTree sub : node) {
			mincaml.typeCheck(sub);
		}
		node.setType(MinCamlType.DefualtType);
		return MinCamlType.DefualtType;
	}
}