/* Generated By:JJTree&JavaCC: Do not edit this line. regExpGrammar2.java */
package com.umflint.edu.regExParsePkg.generated;

public class regExpGrammar2/*@bgen(jjtree)*/implements regExpGrammar2TreeConstants, regExpGrammar2Constants {/*@bgen(jjtree)*/
  protected JJTregExpGrammar2State jjtree = new JJTregExpGrammar2State();public static void main(String args []) throws ParseException
  {
    regExpGrammar2 parser = new regExpGrammar2(System.in);
        parser.Start();
  }

  final public SimpleNode Start() throws ParseException {
 /*@bgen(jjtree) START */
  SimpleNode jjtn000 = new SimpleNode(JJTSTART);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      expr();
      jj_consume_token(0);
    jjtree.closeNodeScope(jjtn000, true);
    jjtc000 = false;
    jjtn000.jjtSetValue("START");
    {if (true) return jjtn000;}
    } catch (Throwable jjte000) {
          if (jjtc000) {
            jjtree.clearNodeScope(jjtn000);
            jjtc000 = false;
          } else {
            jjtree.popNode();
          }
          if (jjte000 instanceof RuntimeException) {
            {if (true) throw (RuntimeException)jjte000;}
          }
          if (jjte000 instanceof ParseException) {
            {if (true) throw (ParseException)jjte000;}
          }
          {if (true) throw (Error)jjte000;}
    } finally {
          if (jjtc000) {
            jjtree.closeNodeScope(jjtn000, true);
          }
    }
    throw new Error("Missing return statement in function");
  }

  final public void expr() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case OPENPAR:
    case CONSTANT:
      term();
      label_1:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case CHOICE:
        case CONCAT:
          ;
          break;
        default:
          jj_la1[0] = jj_gen;
          break label_1;
        }
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case CONCAT:
          concatExpr();
          break;
        case CHOICE:
          choiceExpr();
          break;
        default:
          jj_la1[1] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
      }

      break;
    case NOT:
      negTerm();
      break;
    default:
      jj_la1[2] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public void negTerm() throws ParseException {
 /*@bgen(jjtree) #NEGCONCAT( 2) */
  SimpleNode jjtn000 = new SimpleNode(JJTNEGCONCAT);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      jj_consume_token(NOT);
    jjtn000.jjtSetValue("NOT");
      term();
      jj_consume_token(CONCAT);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case OPENPAR:
      case CONSTANT:
        term();
        break;
      case NOT:
        negTerm();
        break;
      default:
        jj_la1[3] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    } catch (Throwable jjte000) {
      if (jjtc000) {
        jjtree.clearNodeScope(jjtn000);
        jjtc000 = false;
      } else {
        jjtree.popNode();
      }
      if (jjte000 instanceof RuntimeException) {
        {if (true) throw (RuntimeException)jjte000;}
      }
      if (jjte000 instanceof ParseException) {
        {if (true) throw (ParseException)jjte000;}
      }
      {if (true) throw (Error)jjte000;}
    } finally {
      if (jjtc000) {
        jjtree.closeNodeScope(jjtn000,  2);
      }
    }
  }

  final public void concatExpr() throws ParseException {
 /*@bgen(jjtree) #CONCAT( 2) */
  SimpleNode jjtn000 = new SimpleNode(JJTCONCAT);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      jj_consume_token(CONCAT);
          jjtn000.jjtSetValue("CONCAT");
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case OPENPAR:
      case CONSTANT:
        positiveConcatExpr();
        break;
      case NOT:
        negativeConcatExpr();
        break;
      default:
        jj_la1[4] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    } catch (Throwable jjte000) {
          if (jjtc000) {
            jjtree.clearNodeScope(jjtn000);
            jjtc000 = false;
          } else {
            jjtree.popNode();
          }
          if (jjte000 instanceof RuntimeException) {
            {if (true) throw (RuntimeException)jjte000;}
          }
          if (jjte000 instanceof ParseException) {
            {if (true) throw (ParseException)jjte000;}
          }
          {if (true) throw (Error)jjte000;}
    } finally {
          if (jjtc000) {
            jjtree.closeNodeScope(jjtn000,  2);
          }
    }
  }

  final public void positiveConcatExpr() throws ParseException {
    term();
  }

  final public void negativeConcatExpr() throws ParseException {
 /*@bgen(jjtree) #NEGCONCAT( 2) */
  SimpleNode jjtn000 = new SimpleNode(JJTNEGCONCAT);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      jj_consume_token(NOT);
    jjtn000.jjtSetValue("NOT");
      term();
      jj_consume_token(CONCAT);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case OPENPAR:
      case CONSTANT:
        term();
        break;
      case NOT:
        negativeConcatExpr();
        break;
      default:
        jj_la1[5] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    } catch (Throwable jjte000) {
      if (jjtc000) {
        jjtree.clearNodeScope(jjtn000);
        jjtc000 = false;
      } else {
        jjtree.popNode();
      }
      if (jjte000 instanceof RuntimeException) {
        {if (true) throw (RuntimeException)jjte000;}
      }
      if (jjte000 instanceof ParseException) {
        {if (true) throw (ParseException)jjte000;}
      }
      {if (true) throw (Error)jjte000;}
    } finally {
      if (jjtc000) {
        jjtree.closeNodeScope(jjtn000,  2);
      }
    }
  }

  final public void choiceExpr() throws ParseException {
 /*@bgen(jjtree) #CHOICE( 2) */
  SimpleNode jjtn000 = new SimpleNode(JJTCHOICE);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      jj_consume_token(CHOICE);
          jjtn000.jjtSetValue("CHOICE");
      term();
    } catch (Throwable jjte000) {
          if (jjtc000) {
            jjtree.clearNodeScope(jjtn000);
            jjtc000 = false;
          } else {
            jjtree.popNode();
          }
          if (jjte000 instanceof RuntimeException) {
            {if (true) throw (RuntimeException)jjte000;}
          }
          if (jjte000 instanceof ParseException) {
            {if (true) throw (ParseException)jjte000;}
          }
          {if (true) throw (Error)jjte000;}
    } finally {
          if (jjtc000) {
            jjtree.closeNodeScope(jjtn000,  2);
          }
    }
  }

  final public void term() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case CONSTANT:
      finalTerm();
      break;
    case OPENPAR:
      nestedExpr();
      break;
    default:
      jj_la1[6] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public void finalTerm() throws ParseException {
 /*@bgen(jjtree) TERM */
  SimpleNode jjtn000 = new SimpleNode(JJTTERM);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      element();
    jjtn000.jjtSetValue ("UNARY");
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case PLUS:
        jj_consume_token(PLUS);
            jjtn000.jjtSetValue ("PLUS");
        break;
      default:
        jj_la1[7] = jj_gen;
        ;
      }
    } catch (Throwable jjte000) {
      if (jjtc000) {
        jjtree.clearNodeScope(jjtn000);
        jjtc000 = false;
      } else {
        jjtree.popNode();
      }
      if (jjte000 instanceof RuntimeException) {
        {if (true) throw (RuntimeException)jjte000;}
      }
      if (jjte000 instanceof ParseException) {
        {if (true) throw (ParseException)jjte000;}
      }
      {if (true) throw (Error)jjte000;}
    } finally {
      if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
  }

  final public void nestedExpr() throws ParseException {
    jj_consume_token(OPENPAR);
    expr();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case COLON:
      aliasExpr();
      break;
    default:
      jj_la1[8] = jj_gen;
      ;
    }
    jj_consume_token(CLOSEPAR);
  }

  final public void aliasExpr() throws ParseException {
 /*@bgen(jjtree) #ALIAS( 2) */
  SimpleNode jjtn000 = new SimpleNode(JJTALIAS);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
     jjtn000.jjtSetValue("ALIAS");
      jj_consume_token(COLON);
      element();
    } catch (Throwable jjte000) {
    if (jjtc000) {
      jjtree.clearNodeScope(jjtn000);
      jjtc000 = false;
    } else {
      jjtree.popNode();
    }
    if (jjte000 instanceof RuntimeException) {
      {if (true) throw (RuntimeException)jjte000;}
    }
    if (jjte000 instanceof ParseException) {
      {if (true) throw (ParseException)jjte000;}
    }
    {if (true) throw (Error)jjte000;}
    } finally {
    if (jjtc000) {
      jjtree.closeNodeScope(jjtn000,  2);
    }
    }
  }

/* void notExpr() #NOT :
{
}
{
  < NOT > { jjtThis.jjtSetValue ("NOT"); }
  term()
} */
  final public void element() throws ParseException {
 /*@bgen(jjtree) ELEMENT */
        SimpleNode jjtn000 = new SimpleNode(JJTELEMENT);
        boolean jjtc000 = true;
        jjtree.openNodeScope(jjtn000);Token t;
    try {
      t = jj_consume_token(CONSTANT);
          jjtree.closeNodeScope(jjtn000, true);
          jjtc000 = false;
                jjtn000.jjtSetValue(t.image);
    } finally {
          if (jjtc000) {
            jjtree.closeNodeScope(jjtn000, true);
          }
    }
  }

  /** Generated Token Manager. */
  public regExpGrammar2TokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private int jj_gen;
  final private int[] jj_la1 = new int[9];
  static private int[] jj_la1_0;
  static {
      jj_la1_init_0();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x280,0x280,0x4c00,0x4c00,0x4c00,0x4c00,0x4800,0x40,0x2000,};
   }

  /** Constructor with InputStream. */
  public regExpGrammar2(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public regExpGrammar2(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new regExpGrammar2TokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 9; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jjtree.reset();
    jj_gen = 0;
    for (int i = 0; i < 9; i++) jj_la1[i] = -1;
  }

  /** Constructor. */
  public regExpGrammar2(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new regExpGrammar2TokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 9; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jjtree.reset();
    jj_gen = 0;
    for (int i = 0; i < 9; i++) jj_la1[i] = -1;
  }

  /** Constructor with generated Token Manager. */
  public regExpGrammar2(regExpGrammar2TokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 9; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(regExpGrammar2TokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jjtree.reset();
    jj_gen = 0;
    for (int i = 0; i < 9; i++) jj_la1[i] = -1;
  }

  private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }


/** Get the next Token. */
  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  private int[] jj_expentry;
  private int jj_kind = -1;

  /** Generate ParseException. */
  public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[15];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 9; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 15; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

}
