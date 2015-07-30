package mincaml;

import java.io.IOException;

import nez.NezOption;
import nez.SourceContext;
import nez.lang.Grammar;
import nez.lang.GrammarFile;
import nez.util.ConsoleUtils;

public class MinCaml {
	public final static String ProgName = "MinCaml-Java";
	public final static String CodeName = "yokohama";
	public final static int MajorVersion = 0;
	public final static int MinerVersion = 9;
	public final static int PatchLevel = 0;
	public final static String Version = "" + MajorVersion + "." + MinerVersion + "." + PatchLevel;
	public final static String Copyright = "Copyright (c) 2015, Shun Honda";
	public final static String License = "BSD-License Open Source";

	private static Grammar grammar;
	private static MinCamlTreeTransducer treeTransducer;

	public static final void main(String[] args) {
		displayVersion();
		grammar = newMinCamlGrammar();
		treeTransducer = new MinCamlTreeTransducer();
		shell();
	}

	public final static void displayVersion() {
		System.out.println(
				ProgName + "-" + Version + " (" + CodeName + ") on Java JVM-" + System.getProperty("java.version"));
		ConsoleUtils.println(Copyright);
	}

	private static GrammarFile mincamlGrammar = null;

	public final static Grammar newMinCamlGrammar() {
		if(mincamlGrammar == null) {
			try {
				mincamlGrammar = GrammarFile.loadGrammarFile("grammar/mincaml.nez", NezOption.newDefaultOption());
			} catch (IOException e) {
				ConsoleUtils.exit(1, "can't load mincaml.nez");
			}
		}
		return mincamlGrammar.newGrammar("File");
	}

	public final static MinCamlTree parse(String urn, int linenum, String text) {
		SourceContext source = SourceContext.newStringSourceContext(urn, linenum, text);
		MinCamlTree node = (MinCamlTree) grammar.parse(source, treeTransducer);
		if(node == null) {
			ConsoleUtils.println(source.getSyntaxErrorMessage());
		}
		System.out.println("parsed:\n" + node + "\n");
		return node;
	}

	private static void shell() {
		int linenum = 1;
		String command = null;
		while((command = readLine()) != null) {
			parse("<stdio>", linenum, command);
			linenum += (command.split("\n").length);
		}
	}

	private static String readLine() {
		ConsoleUtils.println("\n>>>");
		Object console = ConsoleUtils.getConsoleReader();
		StringBuilder sb = new StringBuilder();
		while(true) {
			String line = ConsoleUtils.readSingleLine(console, "   ");
			if(line == null) {
				return null;
			}
			if(line.equals("")) {
				return sb.toString();
			}
			ConsoleUtils.addHistory(console, line);
			sb.append(line);
			sb.append("\n");
		}
	}

}
