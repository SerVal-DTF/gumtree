package com.github.gumtreediff.io;

import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.tree.*;

import com.google.gson.stream.JsonWriter;

import javax.xml.namespace.QName;
import javax.xml.stream.*;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.*;
import java.util.Stack;

public final class TreeIoUtils {

    private static final QName TYPE = new QName("type");
    private static final QName LABEL = new QName("label");
    private static final QName TYPE_LABEL = new QName("typeLabel");
    private static final QName POS = new QName("pos");
    private static final QName LENGTH = new QName("length");
    private static final QName LINE_BEFORE = new QName("line_before");
    private static final QName LINE_AFTER = new QName("line_after");
    private static final QName COL_BEFORE = new QName("col_before");
    private static final QName COL_AFTER = new QName("col_after");

    private TreeIoUtils() {} // Forbids instantiation of TreeIOUtils
    
    public static TreeContext fromXml(InputStream iStream) {
        return fromXml(new InputStreamReader(iStream));
    }
    
    public static TreeContext fromXmlString(String xml) {
        return fromXml(new StringReader(xml));
    }
    
    public static TreeContext fromXmlFile(String path) {
        try {
            return fromXml(new FileReader(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static TreeContext fromXml(Reader source) {
        XMLInputFactory fact = XMLInputFactory.newInstance();
        TreeContext context = new TreeContext();
        try {
            Stack<ITree> trees = new Stack<>();
            XMLEventReader r = fact.createXMLEventReader(source);
            while (r.hasNext()) {
                XMLEvent e = r.nextEvent();
                if (e instanceof StartElement) {
                    StartElement s = (StartElement) e;
                    int type = Integer.parseInt(s.getAttributeByName(TYPE).getValue());
                    
                    ITree t = context.createTree(type, labelForAttribute(s, LABEL), labelForAttribute(s, TYPE_LABEL));
                    
                    
                    if (s.getAttributeByName(POS) != null) {
                        int pos = numberForAttribute(s, POS);
                        int length = numberForAttribute(s, LENGTH);
                        t.setPos(pos);
                        t.setLength(length);
                    }
                    
                    if (s.getAttributeByName(LINE_BEFORE) != null) {
                        int l0 = numberForAttribute(s, LINE_BEFORE);
                        int c0 = numberForAttribute(s, COL_BEFORE);
                        int l1 = numberForAttribute(s, LINE_AFTER);
                        int c1 = numberForAttribute(s, COL_AFTER);
                        t.setLcPosStart(new int[] {l0, c0});
                        t.setLcPosEnd(new int[] {l1, c1});
                    }
                    
                    if (trees.isEmpty())
                        context.setRoot(t);
                    else
                        t.setParentAndUpdateChildren(trees.peek());
                    trees.push(t);
                } else if (e instanceof EndElement)
                    trees.pop();
            }
            context.validate();
            return context;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private static String labelForAttribute(StartElement s, QName attrName) {
        Attribute attr = s.getAttributeByName(attrName);
        return attr == null ? ITree.NO_LABEL : attr.getValue();
    }

    private static int numberForAttribute(StartElement s, QName attrName) {
        return Integer.parseInt(s.getAttributeByName(attrName).getValue());
    }
    
    public static TreeSerializer toXml(TreeContext ctx) {
        return new TreeSerializer(ctx) {
            @Override
            protected TreeFormatter newFormatter(TreeContext ctx, Writer writer) throws XMLStreamException {
                return new XmlFormatter(writer, ctx);
            }
        };
    }

    public static TreeSerializer toAnnotatedXml(TreeContext ctx, boolean isSrc, MappingStore m) {
        return new TreeSerializer(ctx) {
            @Override
            protected TreeFormatter newFormatter(TreeContext ctx, Writer writer) throws XMLStreamException {
                return new XmlAnnotatedFormatter(writer, ctx, isSrc, m);
            }
        };
    }
    
    public static TreeSerializer toCompactXml(TreeContext ctx) {
        return new TreeSerializer(ctx) {
            @Override
            protected TreeFormatter newFormatter(TreeContext ctx, Writer writer) throws Exception {
                return new XmlCompactFormatter(writer, ctx);
            }
        };
    }
    
    public static TreeSerializer toJson(TreeContext ctx) {
        return new TreeSerializer(ctx) {
            @Override
            protected TreeFormatter newFormatter(TreeContext ctx, Writer writer) throws Exception {
                return new JsonFormatter(writer, ctx);
            }
        };
    }
    
    public static TreeSerializer toLisp(TreeContext ctx) {
        return new TreeSerializer(ctx) {
            @Override
            protected TreeFormatter newFormatter(TreeContext ctx, Writer writer) throws Exception {
                return new LispFormatter(writer, ctx);
            }
        };
    }
    
    public static TreeSerializer toDot(TreeContext ctx) {
        return new TreeSerializer(ctx) {
            @Override
            protected TreeFormatter newFormatter(TreeContext ctx, Writer writer) throws Exception {
                return new DotFormatter(writer, ctx);
            }
        };
    }

    public abstract static class TreeSerializer {
        TreeContext context;
        
        public TreeSerializer(TreeContext ctx) {
            context = ctx;
        }
        
        protected abstract TreeFormatter newFormatter(TreeContext ctx, Writer writer) throws Exception;
        
        public void writeTo(Writer writer) throws Exception {
            TreeFormatter formatter = newFormatter(context, writer);
            try {
                writeTree(formatter, context.getRoot());
            } finally {
                formatter.close();
            }
        }
        
        public void writeTo(OutputStream writer) throws Exception {
            OutputStreamWriter os = new OutputStreamWriter(writer);
            try {
                writeTo(os);
            } finally {
                os.close();
            }
        }
        
        public String toString() {
            StringWriter s = new StringWriter();
            try {
                writeTo(s);
                s.close(); // FIXME this is useless (do nothing) but
                // throws an exception, thus I dont' put it in the finally block where it belongs
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return s.toString();
        }
        
        public void writeTo(String file) throws Exception {
            FileWriter w = new FileWriter(file);
            try {
                writeTo(w);
            } finally {
                w.close();
            }
        }
        
        public void writeTo(File file) throws Exception {
            FileWriter w = new FileWriter(file);
            try {
                writeTo(w);
            } finally {
                w.close();
            }
        }
        
        private void forwardException(Exception e) {
            throw new FormatException(e);
        }
        
        protected void writeTree(TreeFormatter formatter, ITree root) throws Exception {
            formatter.startSerialization();
            try {
                TreeUtils.visitTree(root, new TreeUtils.TreeVisitor() {
                    
                    @Override
                    public void startTree(ITree tree) {
                        try {
                            assert formatter != null;
                            assert tree != null;
                            formatter.startTree(tree);
                        } catch (Exception e) {
                            forwardException(e);
                        }
                    }
                    
                    @Override
                    public void endTree(ITree tree) {
                        try {
                            formatter.endTree(tree);
                        } catch (Exception e) {
                            forwardException(e);
                        }
                    }
                });
            } catch (FormatException e) {
                throw e.getCause();
            }
            formatter.stopSerialization();
        }
    }

    interface TreeFormatter {
        void startSerialization() throws Exception;

        void stopSerialization() throws Exception;
        
        void startTree(ITree tree) throws Exception;

        void endTree(ITree tree) throws Exception;
        
        void close() throws Exception;
    }
    
    static class FormatException extends RuntimeException {
        private static final long serialVersionUID = 593766540545763066L;
        Exception cause;

        public FormatException(Exception cause) {
            super(cause);
            this.cause = cause;
        }

        @Override
        public Exception getCause() {
            return cause;
        }
    }
    
    static class TreeFormatterAdapter implements TreeFormatter {
        protected final TreeContext context;

        protected TreeFormatterAdapter(TreeContext ctx) {
            context = ctx;
        }

        @Override
        public void startSerialization() throws Exception { }

        @Override
        public void startTree(ITree tree) throws Exception { }

        @Override
        public void endTree(ITree tree) throws Exception { }

        @Override
        public void stopSerialization() throws Exception { }

        @Override
        public void close() throws Exception { }
    }

    abstract static class AbsXmlFormatter extends TreeFormatterAdapter {
        protected final XMLStreamWriter writer;

        protected AbsXmlFormatter(Writer w, TreeContext ctx) throws XMLStreamException {
            super(ctx);
            XMLOutputFactory f = XMLOutputFactory.newInstance();
            writer = new IndentingXMLStreamWriter(f.createXMLStreamWriter(w));
        }

        @Override
        public void startSerialization() throws XMLStreamException {
            writer.writeStartDocument();
        }

        @Override
        public void stopSerialization() throws XMLStreamException {
            writer.writeEndDocument();
        }
        
        @Override
        public void close() throws XMLStreamException {
            writer.close();
        }
    }
    
    static class XmlFormatter extends AbsXmlFormatter {
        public XmlFormatter(Writer w, TreeContext ctx) throws XMLStreamException {
            super(w, ctx);
        }

        @Override
        public void startTree(ITree tree) throws XMLStreamException {
            writer.writeStartElement("tree");
            writer.writeAttribute("type", Integer.toString(tree.getType()));
            if (tree.hasLabel()) writer.writeAttribute("label", tree.getLabel());
            if (context.hasLabelFor(tree.getType()))
                writer.writeAttribute("typeLabel", context.getTypeLabel(tree.getType()));
            if (ITree.NO_VALUE != tree.getPos()) {
                writer.writeAttribute("pos", Integer.toString(tree.getPos()));
                writer.writeAttribute("length", Integer.toString(tree.getLength()));
            }
            if (tree.getLcPosStart() != null) {
                writer.writeAttribute("line_before", Integer.toString(tree.getLcPosStart()[0]));
                writer.writeAttribute("col_before", Integer.toString(tree.getLcPosStart()[1]));
                writer.writeAttribute("line_after", Integer.toString(tree.getLcPosEnd()[0]));
                writer.writeAttribute("col_after", Integer.toString(tree.getLcPosEnd()[1]));
            }
        }

        @Override
        public void endTree(ITree tree) throws XMLStreamException {
            writer.writeEndElement();
        }
    }
    
    static class XmlAnnotatedFormatter extends XmlFormatter {
        final SearchOther searchOther;

        public XmlAnnotatedFormatter(Writer w, TreeContext ctx, boolean isSrc,
                                     MappingStore m) throws XMLStreamException {
            super(w, ctx);
            
            if (isSrc)
                searchOther = (tree) -> {
                    return m.hasSrc(tree) ? m.getDst(tree) : null; 
                };
            else
                searchOther = (tree) -> {
                    return m.hasDst(tree) ? m.getSrc(tree) : null;
                };
        }
        
        interface SearchOther {
            ITree lookup(ITree tree);
        }
        
        @Override
        public void startTree(ITree tree) throws XMLStreamException {
            super.startTree(tree);
            ITree o = searchOther.lookup(tree);
            
            if (o != null) {
                if (ITree.NO_VALUE != o.getPos()) {
                    writer.writeAttribute("other_pos", Integer.toString(o.getPos()));
                    writer.writeAttribute("other_length", Integer.toString(o.getLength()));
                }
                if (o.getLcPosStart() != null) {
                    writer.writeAttribute("other_line_before", Integer.toString(o.getLcPosStart()[0]));
                    writer.writeAttribute("other_col_before", Integer.toString(o.getLcPosStart()[1]));
                    writer.writeAttribute("other_line_after", Integer.toString(o.getLcPosEnd()[0]));
                    writer.writeAttribute("other_col_after", Integer.toString(o.getLcPosEnd()[1]));
                }
            }
        }
    }
    
    static class XmlCompactFormatter extends AbsXmlFormatter {
        public XmlCompactFormatter(Writer w, TreeContext ctx) throws XMLStreamException {
            super(w, ctx);
        }

        @Override
        public void startTree(ITree tree) throws XMLStreamException {
            writer.writeStartElement(context.getTypeLabel(tree.getType()));
            if (tree.hasLabel()) writer.writeAttribute("label", tree.getLabel());
        }

        @Override
        public void endTree(ITree tree) throws XMLStreamException {
            writer.writeEndElement();
        }
    }
    
    static class LispFormatter extends TreeFormatterAdapter {
        protected final Writer writer;
        int level = 0;

        protected LispFormatter(Writer w, TreeContext ctx) {
            super(ctx);
            writer = w;
        }
        
        @Override
        public void startSerialization() throws IOException {
            writer.write("(");
        }
        
        @Override
        public void startTree(ITree tree) throws IOException {
            if (!tree.isRoot())
                writer.write("\n");
            for (int i = 0; i < level; i ++)
                writer.write("    ");
            level ++;
            
            String pos = (ITree.NO_VALUE == tree.getPos() ? "" : String.format("(%d %d)", 
                    tree.getPos(), tree.getLength()));
            String lcpos = (tree.getLcPosStart() == null ? "" : String.format("%d %d %d %d",
                    tree.getLcPosStart()[0], tree.getLcPosStart()[1], tree.getLcPosEnd()[0], tree.getLcPosEnd()[1]));
            String matched = tree.isMatched() ? ":matched " : "";
            
            writer.write(String.format("(%d \"%s\" \"%s\" %s(%s%s%s) (",
                            tree.getType(), context.getTypeLabel(tree), tree.getLabel(),
                            matched, pos, (pos != "" && lcpos != "") ? " " : "" ,lcpos));
        }
        
        @Override
        public void endTree(ITree tree) throws IOException {
            writer.write(")");
            level --;
        }
        
        @Override
        public void stopSerialization() throws IOException {
            writer.write(")");
        }
    }
    
    static class DotFormatter extends TreeFormatterAdapter {
        protected final Writer writer;

        protected DotFormatter(Writer w, TreeContext ctx) {
            super(ctx);
            writer = w;
        }

        @Override
        public void startSerialization() throws Exception {
            writer.write("digraph G {\n");
        }

        @Override
        public void startTree(ITree tree) throws Exception {
            String label = tree.toPrettyString(context);
            if (label.contains("\"") || label.contains("\\s"))
                label = label.replaceAll("\"", "").replaceAll("\\s", "").replaceAll("\\\\", "");
            if (label.length() > 30)
                label = label.substring(0, 30);
            writer.write(tree.getId() + " [label=\"" + label + "\"");
            if (tree.isMatched())
                writer.write(",style=filled,fillcolor=cadetblue1");
            writer.write("];\n");
            
            if (tree.getParent() != null)
                writer.write(tree.getParent().getId() + " -> " + tree.getId() + ";\n");
        }

        @Override
        public void stopSerialization() throws Exception {
            writer.write("}");
        }
    }
    
    static class JsonFormatter extends TreeFormatterAdapter {
        private final JsonWriter writer;

        public JsonFormatter(Writer w, TreeContext ctx) {
            super(ctx);
            writer = new JsonWriter(w);
        }

        @Override
        public void startTree(ITree t) throws IOException {
            writer.beginObject();
            writer.name("type").value(Integer.toString(t.getType()));
            if (t.hasLabel()) writer.name("label").value(t.getLabel());
            if (context.hasLabelFor(t.getType())) writer.name("typeLabel").value(context.getTypeLabel(t.getType()));
            if (ITree.NO_VALUE != t.getPos()) {
                writer.name("pos").value(Integer.toString(t.getPos()));
                writer.name("length").value(Integer.toString(t.getLength()));
            }
            if (t.getLcPosStart() != null) {
                writer.name("line_before").value(Integer.toString(t.getLcPosStart()[0]));
                writer.name("col_before").value(Integer.toString(t.getLcPosStart()[1]));
                writer.name("line_after").value(Integer.toString(t.getLcPosEnd()[0]));
                writer.name("col_after").value(Integer.toString(t.getLcPosEnd()[1]));
            }
            writer.name("children");
            writer.beginArray();
        }

        @Override
        public void endTree(ITree tree) throws IOException {
            writer.endArray();
            writer.endObject();
        }

        @Override
        public void startSerialization() throws Exception {
            writer.setIndent("\t");                 
        }

        @Override
        public void close() throws Exception {
            writer.close();
        }
    }
}
