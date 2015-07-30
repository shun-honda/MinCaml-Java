package mincaml;

import java.io.IOException;

import nez.NezOption;
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

	public static final void main(String[] args) {
		displayVersion();
		grammar = newMinCamlGrammar();
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
				mincamlGrammar = GrammarFile.loadGrammarFile("mincaml.nez", NezOption.newDefaultOption());
			} catch (IOException e) {
				ConsoleUtils.exit(1, "can't load mincaml.nez");
			}
		}
		return mincamlGrammar.newGrammar("File");
	}

}
