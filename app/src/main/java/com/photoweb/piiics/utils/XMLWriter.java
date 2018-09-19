package com.photoweb.piiics.utils;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

/**
 * Created by dnizard on 31/08/2017.
 */

public class XMLWriter extends Writer {
    private final Writer writer;
    private final ArrayList<String> stack = new ArrayList<String>();
    private String currentElement;
    private boolean indentNextClose;

    public int indent;

    public XMLWriter(Writer writer) {
        this.writer = writer;
    }

    public Writer getWriter() {
        return this.writer;
    }

    public void startXML() throws IOException {
        writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
    }

    private void indent () throws IOException {
        int count = indent;
        if (currentElement != null) count++;
        for (int i = 0; i < count; i++)
            writer.write('\t');
    }

    public XMLWriter element (String name) throws IOException {
        if (startElementContent()) writer.write('\n');
        indent();
        writer.write('<');
        writer.write(name);
        currentElement = name;
        return this;
    }

    public XMLWriter element (String name, Object text) throws IOException {
        return element(name).text(text).pop();
    }

    private boolean startElementContent () throws IOException {
        if (currentElement == null) return false;
        indent++;
        stack.add(currentElement);
        currentElement = null;
        writer.write(">");
        return true;
    }

    public XMLWriter attribute (String name, Object value) throws IOException {
        if (currentElement == null) throw new IllegalStateException();
        writer.write(' ');
        writer.write(name);
        writer.write("=\"");
        writer.write(value == null ? "null" : value.toString());
        writer.write('"');
        return this;
    }

    public XMLWriter text (Object text) throws IOException {
        startElementContent();
        String string = text == null ? "null" : text.toString();
        indentNextClose = string.length() > 64;
        if (indentNextClose) {
            writer.write('\n');
            indent();
        }
        writer.write(string);
        if (indentNextClose) writer.write('\n');
        return this;
    }

    public XMLWriter pop() throws IOException {
        if (currentElement != null) {
            writer.write("/>\n");
            currentElement = null;
        } else {
            indent = Math.max(indent - 1, 0);
            if (indentNextClose) indent();
            writer.write("</");
            writer.write(stack.remove(stack.size()-1));
            writer.write(">\n");
        }
        indentNextClose = true;
        return this;
    }

    /** Calls {@link #pop()} for each remaining open element, if any, and closes the stream. */
    public void close () throws IOException {
        while (stack.size() != 0)
            pop();
        writer.close();
    }

    public void write (char[] cbuf, int off, int len) throws IOException {
        startElementContent();
        writer.write(cbuf, off, len);
    }

    public void flush () throws IOException {
        writer.flush();
    }
}
