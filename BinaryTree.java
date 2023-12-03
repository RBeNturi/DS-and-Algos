package comp2402a3;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

public class BinaryTree<Node extends BinaryTree.BTNode<Node>> {

	public static class BTNode<Node extends BTNode<Node>> {
		public Node left;
		public Node right;
		public Node parent;
		public int depth;
	}

	/**
	 * An extension of BTNode that you can actually instantiate.
	 */
	protected static class EndNode extends BTNode<EndNode> {
			public EndNode() {
				this.parent = this.left = this.right = null;
			}
	}

	/**
	 * Used to make a mini-factory
	 */
	protected Node sampleNode;

	/**
	 * The root of this tree
	 */
	protected Node r;

	/**
	 * This tree's "null" node
	 */
	protected Node nil;

	/**
	 * Create a new instance of this class
	 * @param sampleNode - a sample of a node that can be used
	 * to create a new node in newNode()
	 * @param nil - a node that will be used in place of null
	 */
	public BinaryTree(Node sampleNode, Node nil) {
		this.sampleNode = sampleNode;
		this.nil = nil;
		r = nil;
	}

	/**
	 * Create a new instance of this class
	 * @param sampleNode - a sample of a node that can be used
	 * to create a new node in newNode()
	 */
	public BinaryTree(Node sampleNode) {
		this.sampleNode = sampleNode;
	}

	/**
	 * Allocate a new node for use in this tree
	 * @return newly created node
	 */
	@SuppressWarnings({"unchecked"})
	protected Node newNode() {
		try {
			Node u = (Node)sampleNode.getClass().getDeclaredConstructor().newInstance();
			u.parent = u.left = u.right = nil;
			return u;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Construct a random binary tree
	 * @return an n-node BinaryTree that has the shape of a random
	 * binary search tree.
	 */
	public static BinaryTree<EndNode> randomBST(int n) {
		Random rand = new Random();
		EndNode sample = new EndNode();
		BinaryTree<EndNode> t = new BinaryTree<EndNode>(sample);
		t.r = randomBSTHelper(n, rand);
		return t;
	}

	protected static EndNode randomBSTHelper(int n, Random rand) {
		if (n == 0) {
			return null;
		}
		EndNode r = new EndNode();
		int ml = rand.nextInt(n);
		int mr = n - ml - 1;
		if (ml > 0) {
			r.left = randomBSTHelper(ml, rand);
			r.left.parent = r;
		}
		if (mr > 0) {
			r.right = randomBSTHelper(mr, rand);
			r.right.parent = r;
		}
		return r;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		toStringHelper(sb, r);
		return sb.toString();
	}

	protected void toStringHelper(StringBuilder sb, Node u) {
		if (u == null) {
			return;
		}
		sb.append('(');
		toStringHelper(sb, u.left);
		toStringHelper(sb, u.right);
		sb.append(')');
	}

	/**
	 * Tree empty or not
	 * @return true if empty, false otherwise
	 */
	public boolean isEmpty() {
		return r == nil;
	}

	/**
	 * Make this tree into the empty tree
	 */
	public void clear() {
		r = nil;
	}

	/**
	 * Compute the depth (distance to the root) of u
	 * @param u
	 * @return the distanct between u and the root, r
	 */
	public int depth(Node u) {
		int d = 0;
		while (u != r) {
			u = u.parent;
			d++;
		}
		return d;
	}

	/**
	 * Demonstration of a recursive traversal
	 * @param u
	 */
	public void traverse(Node u) {
		if (u == nil) return;
		traverse(u.left);
		traverse(u.right);
	}

	/**
	 * Demonstration of a non-recursive traversal
	 */
	public void traverse2() {
		Node u = r, prev = nil, next;
		while (u != nil) {
			if (prev == u.parent) {
				if (u.left != nil) {
					next = u.left;
				} else if (u.right != nil) {
					next = u.right;
				}	else {
					next = u.parent;
				}
			} else if (prev == u.left) {
				if (u.right != nil) {
					next = u.right;
				} else {
					next = u.parent;
				}
			} else {
				next = u.parent;
			}
			prev = u;
			u = next;
		}
	}

	/**
	 * Demonstration of a breadth-first traversal
	 */
	public void bfTraverse() {
		Queue<Node> q = new LinkedList<Node>();
		if (r != nil) q.add(r);
		while (!q.isEmpty()) {
			Node u = q.remove();
			if (u.left != nil) q.add(u.left);
			if (u.right != nil) q.add(u.right);
		}
	}

	/**
	 * Compute the size (number of nodes) of this tree
	 * @warning uses recursion so could cause a stack overflow
	 * @return the number of nodes in this tree
	 */
	public int size() {
		return size(r);
	}

	/**
	 * @return the size of the subtree rooted at u
	 */
	protected int size(Node u) {
		if (u == nil) return 0;
		return 1 + size(u.left) + size(u.right);
	}

	/**
	 * Compute the maximum depth of any node in this tree
	 * @return the maximum depth of any node in this tree
	 */
	public int height() {
		return height(r);
	}

	/**
	 * @return the height of the subtree rooted at u
	 */
	protected int height(Node u) {
		if (u == nil) return -1;
		return 1 + Math.max(height(u.left), height(u.right));
	}

	public int leafAndOnlyLeaf() {
		if (r == nil) return 0;

		int count = 0;
		Stack<Node> stack = new Stack<>();
		stack.push(r);

		while (!stack.isEmpty()) {
			Node u = stack.pop();
			if (u.left == nil && u.right == nil) {
				count++;
			}
			if (u.right != nil) {
				stack.push(u.right);
			}
			if (u.left != nil) {
				stack.push(u.left);
			}
		}

		return count;
	}


	protected int leafAndOnlyLeafHelper(Node u) {
		if(u == nil)return 0;
		if(u.left == nil && u.right == nil)return 1;
		return leafAndOnlyLeafHelper(u.left)+ leafAndOnlyLeafHelper(u.right);
	}


	//Utilizes alterative method of using a queue and the while loop
    public int dawnOfSpring() {
        if (r == null)
            return -1;

        Queue<Node> queue = new LinkedList<>();
        queue.add(r);

        int depth = -1;
        Node u = null;
        Node prev = null;
        Node next;

        while (!queue.isEmpty()) {
            u = queue.poll();

            if (u.left == null && u.right == null) {
                return u.depth;
            }

            if (prev == u.parent) {
                if (u.left != null) {
                    next = u.left;
                    u.left.depth = u.depth + 1;
                } else if (u.right != null) {
                    next = u.right;
                    u.right.depth = u.depth + 1;
                } else {
                    next = u.parent;
                }
            } else if (prev == u.left) {
                if (u.right != null) {
                    next = u.right;
                    u.right.depth = u.depth + 1;
                } else {
                    next = u.parent;
                }
            } else {
                next = u.parent;
            }

            if (u.left != null) {
                queue.add(u.left);
            }
            if (u.right != null) {
                queue.add(u.right);
            }

            prev = u;
            u = next;
        }

        return -1; // Return -1 if there are no leaves in the tree.
    }

    protected int dawnOfSpringHelper(Node u, int d) {
        if (u.left == nil && u.right == nil)
            return d;
        else if (u.left == nil)
            return dawnOfSpringHelper(u.right, d + 1);
        else if (u.right == nil)
            return dawnOfSpringHelper(u.left, d + 1);
        else
            return Math.min(dawnOfSpringHelper(u.left, d + 1), dawnOfSpringHelper(u.right, d + 1));
    }

    public int monkeyLand() {
        int h = height();
        int max_width = 0;

        // Initialize variables required for non-recursive traversal
        Node u = r;
        Node prev = null;
        Node next;
        int d = 0;

        while (u != null) {
            // Check if we should traverse down the left subtree or the right subtree
            if (prev == u.parent || prev == null) {
                if (u.left != null) {
                    next = u.left;
                } else if (u.right != null) {
                    next = u.right;
                } else {
                    next = u.parent;
                }
            } else if (prev == u.left) {
                if (u.right != null) {
                    next = u.right;
                } else {
                    next = u.parent;
                }
            } else {
                next = u.parent;
            }

            // Check if we've reached the target depth
            if (d == h) {
                // Update the maximum width if necessary
                int width = 1;
                Node temp = u;
                while (temp.parent != null) {
                    temp = temp.parent;
                    width++;
                }
                if (width > max_width) {
                    max_width = width;
                }
            }

            // Update variables for the next iteration
            prev = u;
            u = next;
            d++;
        }

        return max_width;
    }



    public String bracketSequence() {
        if (r == null) {
            return ".";
        }

        Stack<Node> stack = new Stack<>();
        StringBuilder sb = new StringBuilder();

        stack.push(r);
        sb.append('(');

        while (!stack.isEmpty()) {
            Node node = stack.pop();

            if (node.right != null) {
                stack.push(node.right);
                sb.append('.');
            }

            if (node.left != null) {
                stack.push(node.left);
                sb.append('(');
            } else {
                sb.append(')');
            }
        }

        return sb.toString();
    }

}
