package mincaml;

import jvm.CodeGenerator;

public abstract class MinCamlTypeRule {
	String name;
	int size;
	MinCamlTypeRule nextChoice;

	public MinCamlTypeRule(String name, int size) {
		this.name = name;
		this.size = size;
	}

	public final String getName() {
		return this.name;
	}

	public final int size() {
		return this.size;
	}

	public MinCamlType match(MinCamlTransducer mincaml, MinCamlTree node) {
		node.matched = this;
		node.typed = MinCamlType.DefualtType;
		return node.typed;
	}

	public abstract void generate(MinCamlTree node, CodeGenerator generator);

}