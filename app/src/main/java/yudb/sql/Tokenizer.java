package yudb.sql;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;

public class Tokenizer {

  private String type;
  private Object value;
  private StreamTokenizer tokenizer;

  public Tokenizer(String sql) {
    tokenizer = new StreamTokenizer(new StringReader(sql));
    tokenizer.wordChars('_', '_');
    tokenizer.wordChars('@', '@');
    tokenizer.ordinaryChar('.');
    tokenizer.lowerCaseMode(true);
    nextToken();
  }

  private void _nextToken() {
    try {
      tokenizer.nextToken();
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  private void _pushBack() {
    tokenizer.pushBack();
  }

  private void nextToken() {
    _nextToken();
    type = switch (tokenizer.ttype) {
      case '(', ')', '.', ',', '+', '-', '*', '/', '%' -> {
        yield Character.toString(tokenizer.ttype);
      }
      case '!' -> {
        _nextToken();
        if (tokenizer.ttype == '=') yield "!=";
        _pushBack();
        yield "!";
      }
      case '=' -> {
        _nextToken();
        if (tokenizer.ttype == '=') yield "==";
        _pushBack();
        yield "=";
      }
      case '<' -> {
        _nextToken();
        if (tokenizer.ttype == '=') yield "<=";
        _pushBack();
        yield "<";
      }
      case '>' -> {
        _nextToken();
        if (tokenizer.ttype == '=') yield ">=";
        _pushBack();
        yield ">";
      }
      case ';', StreamTokenizer.TT_EOF -> {
        value = null;
        yield "EOF";
      }
      case StreamTokenizer.TT_NUMBER -> {
        if (tokenizer.nval == (int) tokenizer.nval) {
          value = (int) tokenizer.nval;
          yield "integer";
        }
        value = tokenizer.nval;
        yield "double";
      }
      case '\'', '"' -> {
        value = tokenizer.sval;
        yield "string";
      }
      case StreamTokenizer.TT_WORD -> {
        value = null;
        yield switch (tokenizer.sval) {
          case "null" -> {
            value = null;
            yield "null";
          }
          case "true" -> {
            value = true;
            yield "true";
          }
          case "false" -> {
            value = false;
            yield "false";
          }
          case "in",
               "like",
               "not",
               "and",
               "or",
               "commit",
               "rollback",
               "create",
               "rename",
               "drop",
               "to",
               "alter",
               "add",
               "column",
               "table",
               "insert",
               "into",
               "values",
               "update",
               "set",
               "delete",
               "select",
               "from",
               "inner",
               "cross",
               "left",
               "right",
               "full",
               "outer",
               "join",
               "on",
               "where",
               "group",
               "by",
               "order",
               "asc",
               "desc",
               "having",
               "offset",
               "limit",
               "as",
               "min",
               "max",
               "count",
               "sum",
               "avg" -> {
            yield tokenizer.sval;
          }
          default -> {
            value = tokenizer.sval;
            yield "ident";
          }
        };
      }
      default -> throw new RuntimeException("invalid character " + tokenizer.ttype);
    };
  }

  public String peek() {
    return type;
  }

  public String pop() {
    var temp = type;
    nextToken();
    return temp;
  }

  public Object pop(String expect) {
    if (type.equals(expect)) {
      var temp = value;
      nextToken();
      return temp;
    }
    throw new RuntimeException("expected " + expect + ", got " + type);
  }

  public String popIdent() {
    return (String) pop("ident");
  }

  public Object popLiteral() {
    var temp = value;
    nextToken();
    return temp;
  }

  public boolean popIf(String expect) {
    if (type.equals(expect)) {
      nextToken();
      return true;
    }
    return false;
  }

}
