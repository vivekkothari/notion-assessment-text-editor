package com.github.vivekkothari.editor;

import java.util.Stack;

public class TextDocument {

  enum OpType {
    UNDO,
    REDO,
    DEFAULT
  }

  private final StringBuilder document = new StringBuilder();
  final Stack<Operation> undoOperations = new Stack<>();
  final Stack<Operation> redoOperations = new Stack<>();

  TextDocument() {
    // initialize
  }

  public void applyOperation(Operation op) {
    redoOperations.clear();
    applyOperation(op, OpType.DEFAULT);
  }

  private void applyOperation(Operation op, OpType opType) {
    switch (op) {
      case DeleteFromEndOperation delete -> {
        int charsToDelete = delete.numCharsToDelete;
        if (document.length() >= charsToDelete) {
          // Normal case: delete last N characters
          var undoOperation = delete.undo(document.substring(document.length() - charsToDelete));
          document.delete(document.length() - charsToDelete, document.length());
          switch (opType) {
            case UNDO -> redoOperations.push(undoOperation);
            case DEFAULT -> undoOperations.push(undoOperation);
            case REDO -> undoOperations.push(undoOperation);
          }
        } else {
          // Edge case: delete entire document
          var undoOperation = delete.undo(document.toString()); // Save full content before deletion
          document.delete(0, document.length());
          switch (opType) {
            case UNDO -> redoOperations.push(undoOperation);
            case DEFAULT -> undoOperations.push(undoOperation);
            case REDO -> undoOperations.push(undoOperation);
          }
        }
      }
      case InsertAtEndOperation insert -> {
        document.append(insert.charsToInsert);
        switch (opType) {
          case UNDO -> redoOperations.push(insert.undo());
          case DEFAULT, REDO -> undoOperations.push(insert.undo());
        }
      }
    }
  }

  /**
   *
   *
   * <pre>
   * insert -> hello, undo:[delete(5)] "hello"
   * insert -> world, undo:[delete(5), delete(5)] "helloworld"
   * undo -> undo[delete(5)], redo(insert("world")) "hello"
   * redo -> undo[delete(5), delete(5)], redo(), "helloworld"
   * </pre>
   */
  public void undoLast() {
    if (undoOperations.isEmpty()) {
      return;
    }
    applyOperation(undoOperations.pop(), OpType.UNDO);
  }

  public void redoLast() {
    if (redoOperations.isEmpty()) {
      return;
    }
    applyOperation(redoOperations.pop(), OpType.REDO);
  }

  public String getCurrentContent() {
    return document.toString();
  }
}
