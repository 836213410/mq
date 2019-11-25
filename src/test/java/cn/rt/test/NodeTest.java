package cn.rt.test;

public class NodeTest {

	public static void main(String[] args) {
		Node node1 = new Node(1);
		Node node2 = new Node(2);
		Node node3 = new Node(3);
		node1.next = node2;
		node2.next = node3;
		Node revesList = revesList(node1);
	}
	
	public static Node revesList(Node node) {
		Node pre = null;
		Node next = null;
		while(node!=null) {
			next = node.next;
			node.next = pre;
			pre = node;
			node = next;
		}
		
		return pre;
	}
	
	
	static class Node{
		int data;
		Node next;
		public Node(int data) {
			super();
			this.data = data;
		}
		@Override
		public String toString() {
			return "Node [data=" + data + "]";
		}
	}
}
