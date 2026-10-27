public class LinkedList {

    public static class Node {
        String id;
        String name;
        String pin;
        String details;
        String date;
        double amount;
        double balanceAfter;
        Node next;

        Node(String id, String name, String pin, double amount) {
            this.id = id;
            this.name = name;
            this.pin = pin;
            this.amount = amount;
            this.next = null;
        }
    }

    private Node head;
    private int size;

    public LinkedList() {
        head = null;
        size = 0;
    }

    public boolean add(Node newNode) {
        if (newNode == null || find(newNode.id) != null) {
            return false;
        }
        if (head == null) {
            head = newNode;
        } else {
            Node current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
        size++;
        return true;
    }

    public Node find(String id) {
        Node current = head;
        while (current != null) {
            if (current.id.equals(id)) {
                return current;
            }
            current = current.next;
        }
        return null;
    }

    public boolean remove(String id) {
        if (head == null) {
            return false;
        }
        if (head.id.equals(id)) {
            head = head.next;
            size--;
            return true;
        }
        Node current = head;
        while (current.next != null) {
            if (current.next.id.equals(id)) {
                current.next = current.next.next;
                size--;
                return true;
            }
            current = current.next;
        }
        return false;
    }

    public boolean update(String id, double newAmount) {
        Node found = find(id);
        if (found == null) {
            return false;
        }
        found.amount = newAmount;
        return true;
    }

    public Node getAt(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        Node current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }
}
