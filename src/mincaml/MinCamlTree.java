package mincaml;

import jvm.CodeGenerator;
import nez.ast.AbstractTree;
import nez.ast.Source;
import nez.ast.SourcePosition;
import nez.ast.Tag;

public class MinCamlTree extends AbstractTree<MinCamlTree>implements SourcePosition {
	MinCamlType typed = null;
	MinCamlTypeRule matched = null;

	protected MinCamlTree(Tag tag, Source source, long pos, int len, int size, Object value) {
		super(tag, source, pos, len, size > 0 ? new MinCamlTree[size] : null, value);
	}

	@Override
	public String formatDebugSourceMessage(String msg) {
		return this.source.formatDebugPositionMessage(this.getSourcePosition(), msg);
	}

	@Override
	protected MinCamlTree dupImpl() {
		return new MinCamlTree(this.getTag(), this.getSource(), this.getSourcePosition(), this.getLength(), this.size(),
				getValue());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		this.stringfy("", sb);
		return sb.toString();
	}

	public void stringfy(String indent, StringBuilder sb) {
		super.stringfy(indent, sb);
		if(typed != null) {
			sb.append(": ");
			sb.append(typed.toString());
		}
	}

	public final static String keyTag(String name) {
		return "#" + name;
	}

	public final static String keyTag(Tag t) {
		return keyTag(t.getName());
	}

	public final String getRuleName() {
		return keyTag(this.getTag());
	}

	public final String getTagName() {
		return this.getTag().getName();
	}

	public final MinCamlType setType(MinCamlType type) {
		this.typed = type;
		return type;
	}

	public final MinCamlType getType(MinCaml type) {
		return this.typed;
	}

	public final void setValue(String name) {
		this.value = name;
	}

	public void generate(CodeGenerator generator) {
		this.matched.generate(this, generator);
	}

}
