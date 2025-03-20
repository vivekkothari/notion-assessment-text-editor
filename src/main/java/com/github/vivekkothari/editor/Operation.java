package com.github.vivekkothari.editor;

public abstract sealed class Operation {}

final class InsertAtEndOperation extends Operation {
  String charsToInsert;

  InsertAtEndOperation(String charsToInsert) {
    this.charsToInsert = charsToInsert;
  }

  Operation undo() {
    return new DeleteFromEndOperation(charsToInsert.length());
  }
}

final class DeleteFromEndOperation extends Operation {
  int numCharsToDelete;

  DeleteFromEndOperation(int numCharsToDelete) {
    this.numCharsToDelete = numCharsToDelete;
  }

  Operation undo(String substringDeleted) {
    return new InsertAtEndOperation(substringDeleted);
  }
}
