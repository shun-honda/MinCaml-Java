package mincaml;

import nez.konoha.KonohaTransducer;

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

	public void match(KonohaTransducer konoha, MinCamlTree node) {
		node.matched = this;
		node.typed = MinCamlType.DefualtType;
	}

}