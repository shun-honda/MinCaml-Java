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
		this.setType("int", MinCamlType.DefualtType);
		this.setType("float", new MinCamlPrimitiveType("float", double.class));
		this.setType("bool", new MinCamlPrimitiveType("bool", boolean.class));
		this.setType("unit", new MinCamlPrimitiveType("unit", void.class));
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
			mincaml = mincaml.parent;
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

	public MinCamlTypeRule getTypeRule(String name) {
		return this.typeRuleMap.get(name);
	}

	public final void setName(String name, MinCamlTree node) {
		MinCamlTransducer mincaml = this;
		while(mincaml != null) {
			if(mincaml.nameMap.containsKey(name)) {
				System.out.println("Name Error: Name '" + name + "' is re-defined ");
				return;
			}
			mincaml = mincaml.parent;
		}
		this.nameMap.put(name, node);
	}

	public final MinCamlTree getName(MinCamlTree nameNode) {
		String name = nameNode.getText();
		MinCamlTransducer mincaml = this;
		while(mincaml != null) {
			MinCamlTree tree = mincaml.nameMap.get(name);
			if(tree != null) {
				return tree;
			}
			mincaml = mincaml.parent;
		}
		System.out.println("Name Error: Name '" + name + "' is not found (pos=" + nameNode.getSourcePosition() + ")");
		System.exit(1);
		return null;
	}

	public final MinCamlType typeCheck(MinCamlTree node) {
		String rule = node.getRuleName();
		MinCamlTransducer mincaml = this;
		while(mincaml != null) {
			node.matched = mincaml.typeRuleMap.get(rule);
			if(node.matched != null) {
				return node.matched.match(this, node);
			}
			mincaml = mincaml.parent;
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
