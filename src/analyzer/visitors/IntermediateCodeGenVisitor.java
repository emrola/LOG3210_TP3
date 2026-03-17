package analyzer.visitors;

import analyzer.ast.*;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Vector;

/**
 * Ce visiteur explore l'AST et génère du code intermédiaire.
 *
 * @author Félix Brunet
 * @author Doriane Olewicki
 * @author Quentin Guidée
 * @author Raphaël Tremblay
 * @version 2025.10.23
 */
public class IntermediateCodeGenVisitor implements ParserVisitor {
    public static final String FALL = "fall";

    private final PrintWriter m_writer;

    public HashMap<String, VarType> SymbolTable = new HashMap<>();

    private int id = 0;
    private int label = 0;

    public IntermediateCodeGenVisitor(PrintWriter writer) {
        m_writer = writer;
    }

    private String newID() {
        return "_t" + id++;
    }

    private String newLabel() {
        return "_L" + label++;
    }

    @Override
    public Object visit(SimpleNode node, Object data) {
        return data;
    }

    @Override
    public Object visit(ASTProgram node, Object data) {
        node.childrenAccept(this, data);
        // TODO
        String endLabel = newLabel();
        m_writer.println(endLabel);
        return null;
    }

    @Override
    public Object visit(ASTBlock node, Object data) {
        node.childrenAccept(this, data);
        // TODO
        String nextChildData = (String) data;
        int nChildren = node.jjtGetNumChildren();

        for (int i = 0; i < nChildren; i++) {
            String nextChild = (i == nChildren - 1) ? nextChildData : newLabel();
            node.jjtGetChild(i).jjtAccept(this, nextChild);

            if (i < nChildren - 1) {
                m_writer.println(nextChild);
            }
        }
        return null;
    }

    @Override
    public Object visit(ASTIfStmt node, Object data) {
        node.childrenAccept(this, data);
        // TODO
        return null;
    }

    @Override
    public Object visit(ASTWhileStmt node, Object data) {
        node.childrenAccept(this, data);
        // TODO
        return null;
    }

    @Override
    public Object visit(ASTForStmt node, Object data) {
        node.childrenAccept(this, data);
        // TODO
        return null;
    }

    @Override
    public Object visit(ASTDeclareStmt node, Object data) {
        String identifier = ((ASTIdentifier) node.jjtGetChild(0)).getValue();
        VarType vartype = node.getValue().equals("bool") ? VarType.BOOL : VarType.INT;
        SymbolTable.put(identifier, vartype);
        // TODO
        boolean hasInit = node.jjtGetNumChildren() > 1;
        if (!hasInit) {
            m_writer.println(identifier + " = 0");
            return null;
        }

        if (vartype == VarType.INT) {
            String value = (String) node.jjtGetChild(1).jjtAccept(this, data);
            m_writer.println(identifier + " = " + value);
        }

        //else for the bool
        
        return null;
    }

    @Override
    public Object visit(ASTAssignStmt node, Object data) {
        String identifier = ((ASTIdentifier) node.jjtGetChild(0)).getValue();
        node.jjtGetChild(1).jjtAccept(this, data);
        // TODO
        return null;
    }

    @Override
    public Object visit(ASTExpr node, Object data) {
        return node.jjtGetChild(0).jjtAccept(this, data);
    }

    @Override
    public Object visit(ASTTernary node, Object data) {
        node.childrenAccept(this, data);
        // TODO
        return null;
    }

    public Object codeExtAddMul(SimpleNode node, Object data, Vector<String> ops) {
        // À noter qu'il n'est pas nécessaire de boucler sur tous les enfants.
        // La grammaire n'accepte plus que 2 enfants maximum pour certaines opérations, au lieu de plusieurs
        // dans les TPs précédents. Vous pouvez vérifier au cas par cas dans le fichier Grammaire.jjt.
        node.childrenAccept(this, data);
        // TODO
        return null;
    }

    @Override
    public Object visit(ASTAddExpr node, Object data) {
        return codeExtAddMul(node, data, node.getOps());
    }

    @Override
    public Object visit(ASTMultExpr node, Object data) {
        return codeExtAddMul(node, data, node.getOps());
    }

    @Override
    public Object visit(ASTNegExpr node, Object data) {
        node.jjtGetChild(0).jjtAccept(this, data);
        // TODO
        return null;
    }

    @Override
    public Object visit(ASTLogExpr node, Object data) {
        node.childrenAccept(this, data);
        // TODO
        return null;
    }

    @Override
    public Object visit(ASTCompExpr node, Object data) {
        node.childrenAccept(this, data);
        // TODO
        return null;
    }

    @Override
    public Object visit(ASTNotExpr node, Object data) {
        // TODO
        BoolLabel boolLabel = (BoolLabel) data;
        node.jjtGetChild(0).jjtAccept(this, boolLabel.swapped());
        return null;
    }

    @Override
    public Object visit(ASTGenValue node, Object data) {
        // TODO
        return node.jjtGetChild(0).jjtAccept(this, data);
    }

    @Override
    public Object visit(ASTBoolValue node, Object data) {
        // TODO
        BoolLabel boolLabel = (BoolLabel) data;

        if (node.getValue()) {
            m_writer.println("goto" + boolLabel.lTrue);
        } else {
            m_writer.println("goto" + boolLabel.lFalse);
        }

        return null;
    }

    @Override
    public Object visit(ASTIdentifier node, Object data) {
        // TODO
        return node.getValue();
    }

    @Override
    public Object visit(ASTIntValue node, Object data) {
        return Integer.toString(node.getValue());
    }

    public enum VarType {
        BOOL, INT
    }

    private static class BoolLabel {
        public String lTrue;
        public String lFalse;

        public BoolLabel(String lTrue, String lFalse) {
            this.lTrue = lTrue;
            this.lFalse = lFalse;
        }

        public BoolLabel swapped() {
            return new BoolLabel(lFalse, lTrue);
        }
    }
}
