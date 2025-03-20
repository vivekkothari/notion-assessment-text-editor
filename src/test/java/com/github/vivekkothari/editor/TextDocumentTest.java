package com.github.vivekkothari.editor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TextDocumentTest {

  @Test
  void test() {
    TextDocument doc = new TextDocument();
    assertEquals("", doc.getCurrentContent());

    doc.undoLast();
    assertEquals("", doc.getCurrentContent());
    doc.redoLast();
    assertEquals("", doc.getCurrentContent());

    doc.applyOperation(new InsertAtEndOperation("hello"));
    assertEquals("hello", doc.getCurrentContent());
    assertInstanceOf(DeleteFromEndOperation.class, doc.undoOperations.peek());
    assertEquals(1, doc.undoOperations.size());

    doc.applyOperation(new InsertAtEndOperation("world"));
    assertEquals("helloworld", doc.getCurrentContent());
    assertInstanceOf(DeleteFromEndOperation.class, doc.undoOperations.peek());
    assertEquals(2, doc.undoOperations.size());

    doc.undoLast();
    assertEquals("hello", doc.getCurrentContent());
    assertInstanceOf(DeleteFromEndOperation.class, doc.undoOperations.peek());
    assertInstanceOf(InsertAtEndOperation.class, doc.redoOperations.peek());
    assertEquals(1, doc.undoOperations.size());
    assertEquals(1, doc.redoOperations.size());

    doc.redoLast();
    assertEquals("helloworld", doc.getCurrentContent());
    assertInstanceOf(DeleteFromEndOperation.class, doc.undoOperations.peek());
    assertEquals(2, doc.undoOperations.size());
    assertEquals(0, doc.redoOperations.size());

    doc.applyOperation(new DeleteFromEndOperation(5));
    assertEquals("hello", doc.getCurrentContent());

    doc.undoLast();
    assertEquals("helloworld", doc.getCurrentContent());

    doc.redoLast();
    assertEquals("hello", doc.getCurrentContent());

    doc.undoLast();
    assertEquals("helloworld", doc.getCurrentContent());
    doc.undoLast();
    assertEquals("hello", doc.getCurrentContent());
    doc.undoLast();
    assertEquals("", doc.getCurrentContent());
  }
}
