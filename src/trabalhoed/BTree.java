package trabalhoed;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class BTree {

    private int m; //Grau dos nós da árvore
    private BNode root;
    private long comparacoes;

    public BTree(int m) {
        this.m = (m*2)+1;
        this.root = null;
        this.comparacoes = 0;
    }

    public void printTree() {
        if (root != null) {
            root.printNode(0, -1);
        }
    }

    public void rootOverflow() {
        ArrayList<Registros> keyList = root.getKeyList();
        int middlePoint = (keyList.size() - 1) / 2;
        BNode left = new BNode(this.m);
        BNode newRoot = new BNode(this.m);
        BNode right = new BNode(this.m);
        int i;
        for (i = 0; i < keyList.size(); i++) {
            if (i < middlePoint) {
                left.insertKey(keyList.get(i));
                if (!root.isLeaf()) {
                    left.addChild(root.getChild(i));
                    this.comparacoes += 1;
                }
            }
            if (i == middlePoint) {
                newRoot.insertKey(keyList.get(i));
                if (!root.isLeaf()) {
                    left.addChild(root.getChild(i));
                    this.comparacoes += 1;
                }
            }
            if (i > middlePoint) {
                right.insertKey(keyList.get(i));
                if (!root.isLeaf()) {
                    right.addChild(root.getChild(i));
                    this.comparacoes += 1;
                }
            }
        }
        if (!root.isLeaf()) {
            right.addChild(root.getChild(i));
            this.comparacoes += 1;
        }
        newRoot.addChild(left);
        newRoot.addChild(right);
        this.comparacoes += 2;
        root = newRoot;
    }

    public BNode getParent(BNode n) {
        BNode aux = root;
        BNode parent = null;
        while (aux != n) {
            parent = aux;
            ArrayList<Registros> auxKeyList = aux.getKeyList();
            for (int i = 0; i < auxKeyList.size(); i++) {
                ArrayList<Registros> NKeyList = n.getKeyList();
                if (compare(NKeyList.get(NKeyList.size() - 1).getId(), auxKeyList.get(i).getId())) {
                    aux = aux.getChild(i);
                    break;
                } else if (i == auxKeyList.size() - 1) {
                    aux = aux.getChild(i + 1);
                }
            }
        }
        return parent;

    }

    public void normalOverflow(BNode overNode, BNode parentNode) {
        ArrayList<Registros> keyList = overNode.getKeyList();
        int middlePoint = (keyList.size() - 1) / 2;
        BNode left = new BNode(this.m);
        BNode right = new BNode(this.m);
        int i;
        for (i = 0; i < keyList.size(); i++) {
            if (i < middlePoint) {
                left.insertKey(keyList.get(i));
                if (!overNode.isLeaf()) {
                    left.addChild(overNode.getChild(i));
                    comparacoes += 1;
                }
            }
            if (i == middlePoint) {
                parentNode.insertKey(keyList.get(i));
                parentNode.removeChild(overNode);
                if (!overNode.isLeaf()) {
                    left.addChild(overNode.getChild(i));
                    this.comparacoes += 1;
                }
            }
            if (i > middlePoint) {
                right.insertKey(keyList.get(i));
                if (!overNode.isLeaf()) {
                    right.addChild(overNode.getChild(i));
                    this.comparacoes += 1;
                }
            }
        }
        if (!overNode.isLeaf()) {
            right.addChild(overNode.getChild(i));
            this.comparacoes += 1;
        }
        parentNode.addChild(left);
        parentNode.addChild(right);
        this.comparacoes += 2;
        if (parentNode.isFull()) {
            if (parentNode == root) {
                rootOverflow();
            } else {
                normalOverflow(parentNode, getParent(parentNode));
            }
        }
    }

    private BNode auxSearch(BNode n, Registros val) {
        for (Registros i : n.getKeyList()) {
            if (i.getId().equals(val.getId())) {
                this.comparacoes += 1;
                return n;
            }
        }
        if (n.isLeaf()) {
            return null;
        }
        ArrayList<Registros> keyList = n.getKeyList();
        int i;
        for (i = 0; i < keyList.size(); i++) {
            if (compare(val.getId(), keyList.get(i).getId())) {
                return auxSearch(n.getChild(i), val);
            }

        }
        return auxSearch(n.getChild(i), val);
    }

    public BNode search(Registros val) {
        return auxSearch(root, val);
    }

    public void insert(Registros val) {
        if (root == null) {
            root = new BNode(this.m);
            root.insertKey(val);
        } else {
            boolean finished = false;
            BNode aux = root;
            BNode parentNode = null;
            while (!finished) {
                if (aux.isLeaf()) {
                    aux.insertKey(val);
                    if (aux.isFull()) {
                        if (aux == root) {
                            rootOverflow();
                            this.comparacoes += 1;
                        } else {
                            normalOverflow(aux, parentNode);
                            this.comparacoes += 1;
                        }
                    }
                    finished = true;
                } else {
                    List<Registros> keyList = aux.getKeyList();
                    for (int i = 0; i < keyList.size(); i++) {
                        if (compare(val.getId(), keyList.get(i).getId())) {
                            parentNode = aux;
                            aux = aux.getChild(i);
                            break;
                        } else if (i == keyList.size() - 1) { //Não houve key maior que a nova no nó, inserir no último filho
                            parentNode = aux;
                            aux = aux.getChild(i + 1);
                        }
                    }
                }
            }
        }
    }

    public boolean compare(BigInteger one, BigInteger two) {
        int aux = one.compareTo(two);
        comparacoes += 1;
        return aux < 0;
    }

    public long getComparacoes() {
        return comparacoes;
    }
    
}
