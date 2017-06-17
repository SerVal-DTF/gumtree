package com.github.gumtreediff.gen.antlrantlr;

import com.github.gumtreediff.gen.antlr.AbstractAntlrTreeGenerator;
import com.github.gumtreediff.gen.Register;
import com.github.gumtreediff.gen.antlr.AbstractAntlrTreeGenerator;
import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenRewriteStream;
import org.antlr.runtime.tree.CommonTree;

import java.io.IOException;
import java.io.Reader;

@Register(id = "antlr-antlr", accept = "\\.[gG]$")
public class AntlrGrammarTreeGenerator extends AbstractAntlrTreeGenerator {

    @Override
    protected CommonTree getStartSymbol(Reader r) throws RecognitionException, IOException {
        ANTLRStringStream stream = new ANTLRReaderStream(r);
        ANTLRv3Lexer l = new ANTLRv3Lexer(stream);
        tokens = new TokenRewriteStream(l);
        ANTLRv3Parser p = new ANTLRv3Parser(tokens);
        return (CommonTree) p.grammarDef().getTree();
    }

    @Override
    protected final String[] getTokenNames() {
        return ANTLRv3Parser.tokenNames;
    }
}
