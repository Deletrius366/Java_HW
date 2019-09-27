package expression.parser;

import exceptions.*;
import expression.*;
import operations.Operation;

public class ExpressionParser<T> implements Parser<T> {
    private Operation<T> oper;

    public ExpressionParser(Operation<T> op) {
        oper = op;
    }

    private Token now;
    private T nowNum;
    private String nowVar;
    private String expr;
    private int ind;

    public TripleExpression<T> parse(String expression) throws ParseExceptions {
        expr = expression;
        ind = 0;
        now = Token.BEGIN;
        return addAndSubOp();
    }

    private TripleExpression<T> unaryOp() throws ParseExceptions {
        TripleExpression<T> ans = null;
        nextToken();
        switch (now) {
            case OPEN:
                ans = addAndSubOp();
                if (now != Token.CLOSE) {
                    throw new BracketException(ind);
                }
                nextToken();
                break;
            case NUM:
                getNum();
                ans = new Const<>(nowNum);
                nextToken();
                break;
            case VAR:
                ans = new Variable<T>(nowVar);
                nextToken();
                break;
            case NOT:
                ans = new CheckedNegate<>(unaryOp(), oper);
                break;
            case SQUARE:
                ans = new CheckedSquare<>(unaryOp(), oper);
                break;
            case ABS:
                ans = new CheckedAbs<>(unaryOp(), oper);
                break;
            default:
                throw new ArgumentsParsingException(ind);
        }
        return ans;
    }

    private TripleExpression<T> mulAndDivOp() throws ParseExceptions {
        TripleExpression<T> ans = unaryOp();
        while (true) {
            switch (now) {
                case MUL:
                    ans = new CheckedMultiply<T>(ans, unaryOp(), oper);
                    break;
                case DIV:
                    ans = new CheckedDivide<T>(ans, unaryOp(), oper);
                    break;
                case MOD:
                    ans = new CheckedMod<T>(ans, unaryOp(), oper);
                    break;
                default:
                    return ans;
            }
        }
    }

    private TripleExpression<T> addAndSubOp() throws ParseExceptions {
        TripleExpression<T> ans = mulAndDivOp();
        do {
            switch (now) {
                case ADD:
                    ans = new CheckedAdd<>(ans, mulAndDivOp(), oper);
                    break;
                case SUB:
                    ans = new CheckedSubtract<T>(ans, mulAndDivOp(), oper);
                    break;
                default:
                    return ans;
            }
        } while (true);
    }

    private void getNum() throws ParseConstException {
        int left = ind - 1;
        while (ind < expr.length() && Character.isDigit(expr.charAt(ind))) {
            ind++;
        }
        try {
            nowNum = oper.parseNum(expr.substring(left, ind));
        } catch (Exception e) {
            throw new ParseConstException(left);
        }
    }

    private Token nextToken() throws WrongOperationException {
        Token prev = now;
        while (ind < expr.length() && Character.isWhitespace(expr.charAt(ind))) {
            ind++;
        }
        if (ind >= expr.length()) {
            now = Token.END;
            return now;
        }
        char nowSymb = expr.charAt(ind);
        switch (nowSymb) {
            case '+':
                now = Token.ADD;
                break;
            case '*':
                now = Token.MUL;
                break;
            case '/':
                now = Token.DIV;
                break;
            case '-':
                if (prev == Token.NUM || prev == Token.VAR || prev == Token.CLOSE) {
                    now = Token.SUB;
                } else if (ind + 1 < expr.length() && Character.isDigit(expr.charAt(ind + 1))) {
                    now = Token.NUM;
                } else {
                    now = Token.NOT;
                }
                break;
            case '(':
                now = Token.OPEN;
                break;
            case ')':
                now = Token.CLOSE;
                break;
            case 'x':
            case 'y':
            case 'z':
                now = Token.VAR;
                nowVar = "" + nowSymb;
                break;
            default:
                if (Character.isDigit(nowSymb)) {
                    now = Token.NUM;
                } else {
                    String longId;
                    int left = ind;
                    while (ind < expr.length() && Character.isLetter(expr.charAt(ind))) {
                        ind++;
                    }
                    longId = expr.substring(left, ind);
                    if (longId.equals("square")) {
                        now = Token.SQUARE;
                    } else if (longId.equals("abs")) {
                        now = Token.ABS;
                    } else if (longId.equals("mod")) {
                        now = Token.MOD;
                    } else {
                        throw new WrongOperationException(left);
                    }
                }
        }
        ind++;
        return now;
    }
}

enum Token {
    SQUARE, ABS, MOD, NUM, VAR, ADD, SUB, MUL, DIV, LOG2, POW2, LOW, HIGH, OPEN, CLOSE, END, NOT, AND, XOR, OR, TIL, COUNT, SHR, SHL, BEGIN, ERROR
}