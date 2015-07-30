package mincaml;

import nez.ast.Source;
import nez.ast.Tag;
import nez.ast.TreeTransducer;

public class MinCamlTreeTransducer extends TreeTransducer {

	static final Tag Expression = Tag.tag("node");

	@Override
	public Object newNode(Tag tag, Source s, long spos, long epos, int size, Object value) {
		return new MinCamlTree(tag == null ? Expression : tag, s, spos, (int) (epos - spos), size, value);
	}

	@Override
	public void link(Object node, int index, Object child) {
		((MinCamlTree) node).set(index, (MinCamlTree) child);
	}

	@Override
	public Object commit(Object node) {
		return node;
	}

	@Override
	public void abort(Object arg0) {
	}

}
