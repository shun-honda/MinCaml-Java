package mincaml;

import java.util.HashMap;
import java.util.Map;

import nez.main.Verbose;

public class MinCamlTransducer {
	MinCamlTransducer parent;
	Map<String, MinCamlType> typeMap;
	Map<String, MinCamlTypeRule> typeRuleMap;
	Map<String, MinCamlTree> nameMap;

	public MinCamlTransducer() {
		this.parent = null;
		this.typeMap = new HashMap<String, MinCamlType>();
		this.typeRuleMap = new HashMap<String, MinCamlTypeRule>();
		this.nameMap = new HashMap<String, MinCamlTree>();
		this.setType("int", new MinCamlPrimitiveType("int"));
		this.setType("float", new MinCamlPrimitiveType("float"));
		this.setType("bool", new MinCamlPrimitiveType("bool"));
		new MinCamlLanguage(this);
	}

	public MinCamlTransducer(MinCamlTransducer parent) {
		this.parent = parent;
		this.typeMap = new HashMap<String, MinCamlType>();
		this.typeRuleMap = new HashMap<String, MinCamlTypeRule>();
		this.nameMap = new HashMap<String, MinCamlTree>();
	}

	public final void setType(String name, MinCamlType t) {
		this.typeMap.put(name, t);
	}

	public final MinCamlType getType(String name) {
		MinCamlTransducer mincaml = this;
		while(mincaml != null) {
			MinCamlType type = mincaml.typeMap.get(name);
			if(type != null) {
				return type;
			}
		}
		System.out.println("Type Error: Type '" + name + "' is not found");
		return MinCamlType.DefualtType;
	}

	public final MinCamlType getType(MinCamlTree node) {
		return this.getType(node.getText());
	}

	public void setTypeRule(MinCamlTypeRule rule) {
		this.typeRuleMap.put(rule.getName(), rule);
	}

	public final void setName(String name, MinCamlTree node) {
		MinCamlTransducer mincaml = this;
		while(mincaml != null) {
			if(this.nameMap.containsKey(name)) {
				System.out.println("Name Error: Name '" + name + "' is re-defined ");
				return;
			}
		}
		this.nameMap.put(name, node);
	}

	public final MinCamlTree getName(String name) {
		MinCamlTransducer mincaml = this;
		while(mincaml != null) {
			MinCamlTree tree = mincaml.nameMap.get(name);
			if(tree != null) {
				return tree;
			}
		}
		System.out.println("Name Error: Name '" + name + "' is not found");
		return null;
	}

	public final MinCamlType typeCheck(MinCamlTree node) {
		String rule = node.getRuleName();
		MinCamlTransducer mincaml = this;
		while(mincaml != null) {
			node.matched = this.typeRuleMap.get(rule);
			if(node.matched != null) {
				return node.matched.match(this, node);
			}
		}
		System.out.println("undifined rule: '" + rule + "'\n" + node);
		return null;
	}

	boolean eval(MinCamlTree node) {
		if(node != null) {
			this.typeCheck(node);
			Verbose.println("typed: \n" + node);
			return true;
		}
		return false;
	}
}
