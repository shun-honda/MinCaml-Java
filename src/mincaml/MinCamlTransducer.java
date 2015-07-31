package mincaml;

import java.util.HashMap;
import java.util.Map;

public class MinCamlTransducer {
	Map<String, MinCamlType> typeMap;
	Map<String, MinCamlTypeRule> typeRuleMap;

	public MinCamlTransducer() {
		this.typeMap = new HashMap<String, MinCamlType>();
		this.typeRuleMap = new HashMap<String, MinCamlTypeRule>();
		this.setType("int", new MinCamlPrimitiveType("int"));
		this.setType("float", new MinCamlPrimitiveType("float"));
		this.setType("bool", new MinCamlPrimitiveType("bool"));
		new MinCamlLanguage(this);
	}

	public final void setType(String name, MinCamlType t) {
		this.typeMap.put(name, t);
	}

	public final MinCamlType getType(String name) {
		return this.typeMap.get(name);
	}

	public final MinCamlType getType(MinCamlTree node) {
		return this.typeMap.get(node.getTag().name);
	}

	public void setTypeRule(MinCamlTypeRule rule) {
		this.typeRuleMap.put(rule.getName(), rule);
	}

	public final MinCamlType typeCheck(MinCamlTree node) {
		node.matched = this.typeRuleMap.get(node.getRuleName());
		return node.matched.match(this, node);
	}
}
