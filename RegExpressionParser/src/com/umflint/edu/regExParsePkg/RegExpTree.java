package com.umflint.edu.regExParsePkg;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;

import com.umflint.edu.regExParsePkg.generated.SimpleNode;

public class RegExpTree  extends JPanel
                      implements TreeSelectionListener {
    private JEditorPane htmlPane;
    private JTree tree;
    private static boolean DEBUG = false;
    
    public DefaultMutableTreeNode rootNode;
    private int bufferNum = 0;
    
    public RegExpTree (SimpleNode parseTree) {
    	super(new GridLayout(1,0));
    	rootNode = formTree(parseTree);
    	regExpTree1();
    }
    
    public DefaultMutableTreeNode formTree(SimpleNode n) {
    	DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		String val = n.jjtGetValue().toString();
		if (n.jjtGetNumChildren() == 0) {
			root.setUserObject(val + ":" + ++bufferNum);
			return root;
		}
		else if (val.equals("UNARY")) {
			return (formTree((SimpleNode) n.jjtGetChild(0)));
		}
		else if (val.equals("CONCAT")) {
			DefaultMutableTreeNode n1 = formTree((SimpleNode) n.jjtGetChild(0));
			DefaultMutableTreeNode n2 = formTree((SimpleNode) n.jjtGetChild(1));
			root.setUserObject("CONCAT:" + ++bufferNum);
			root.add(n1);
			root.add(n2);
			return root;
		}
		else if (val.equals("CHOICE")) {
			DefaultMutableTreeNode n1 = formTree((SimpleNode) n.jjtGetChild(0));
			DefaultMutableTreeNode n2 = formTree((SimpleNode) n.jjtGetChild(1));
			root.setUserObject("CHOICE:" + ++bufferNum);
			root.add(n1);
			root.add(n2);
			return root;
		}
		else if (val.equals("PLUS")) {
			DefaultMutableTreeNode n1 = formTree((SimpleNode) n.jjtGetChild(0));
			root.setUserObject("PLUS:" + ++bufferNum);
			root.add(n1);
			return root;
		}
		else if (val.equals("ALIAS")) {
			DefaultMutableTreeNode n1 = formTree((SimpleNode) n.jjtGetChild(0));
			String name = ((SimpleNode) n.jjtGetChild(1)).jjtGetValue().toString();
			String s = n1.getUserObject().toString();
			s = s + "," + name;
			n1.setUserObject(s);
			return n1;
		}
		else if (val.equals("NOT")) {
			DefaultMutableTreeNode n1 = formTree((SimpleNode) n.jjtGetChild(0));
			DefaultMutableTreeNode n2 = formTree((SimpleNode) n.jjtGetChild(1));
			root.setUserObject("NOT:" + ++bufferNum);
			root.add(n1);
			root.add(n2);
			return root;
		}
		else // START 
			return (formTree((SimpleNode) n.jjtGetChild(0)));
    }
    
    public void regExpTree1() {
		tree = new JTree(rootNode);
		
        tree.getSelectionModel().setSelectionMode
        (TreeSelectionModel.SINGLE_TREE_SELECTION);

        //Listen for when the selection changes.
        tree.addTreeSelectionListener(this);

        //Create the scroll pane and add the tree to it. 
        JScrollPane treeView = new JScrollPane(tree);

        //Create the HTML viewing pane.
       /* htmlPane = new JEditorPane();
        htmlPane.setEditable(false);
        JScrollPane htmlView = new JScrollPane(htmlPane);*/

        //Add the scroll panes to a split pane.
        /*JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(treeView);
        splitPane.setBottomComponent(htmlView);*/

        Dimension minimumSize = new Dimension(300, 150);
        setPreferredSize(minimumSize);
       /* htmlView.setMinimumSize(minimumSize);
        treeView.setMinimumSize(minimumSize);
        splitPane.setDividerLocation(100); 
        splitPane.setPreferredSize(new Dimension(500, 300));
*/
        //Add the split pane to this panel.
        add(treeView);
        
	}

    /** Required by TreeSelectionListener interface. */
    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                           tree.getLastSelectedPathComponent();

        if (node == null) return;

        Object nodeInfo = node.getUserObject();
/*        if (node.isLeaf()) {
            BookInfo book = (BookInfo)nodeInfo;
            displayURL(book.bookURL);
            if (DEBUG) {
                System.out.print(book.bookURL + ":  \n    ");
            }
        } else {
            displayURL(helpURL); 
        }
        if (DEBUG) {
            System.out.println(nodeInfo.toString());
        }
*/    }

    private void createAndShowGUI() {
            try {
                UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("Couldn't use system look and feel.");
            }

        //Create and set up the window.
        JFrame frame = new JFrame("TreeDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Add content to the window.
        frame.add(this);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public void displayTree() {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
    
	public static void main(String argv[]) {
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
/*        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
*/        }
	
}
