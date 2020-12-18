package items;

import gui.GUIElement;
import sprites.GameSprite;

import java.util.Comparator;

public class ItemLinkedList {

    private Node head;
    private Node current;
    private Node tail;
    private int count;

    public InventorySlot start(){
        return head.value;
    }

    public void addNode(InventorySlot value){
        Node newNode = new Node(value);

        if (head == null)
        {
            head = newNode;
            tail = head;
        }
        else
        {
            newNode.nextNode = head;
            newNode.previousNode = tail;

            head.previousNode = newNode;
            tail.nextNode = newNode;

            head = newNode;
        }

        count++;

    }

    public boolean containsNode(String itemName){
        Node currentNode = head;

        if(head == null)
            return false;
        else{
            do {
                if(currentNode.value.getItem().getName().equals(itemName)){
                    return true;
                }
                currentNode = currentNode.nextNode;
            }
            while(currentNode != head);
            return false;
        }
    }

    public void deleteNode(int valueToDelete) {
        Node currentNode = head;

        if (head != null) {
            if (currentNode.value.getId() == valueToDelete) {
                head = head.nextNode;
                tail.nextNode = head;
            } else {
                do {
                    Node nextNode = currentNode.nextNode;
                    if (nextNode.value.getId() == valueToDelete) {
                        currentNode.nextNode = nextNode.nextNode;
                        count--;
                        break;
                    }
                    currentNode = currentNode.nextNode;
                } while (currentNode != head);
            }
        }
    }

    public void deleteNode(Node node){
        Node currentNode = head;

        if (head != null) {
            if (currentNode.equals(node)) {
                head = head.nextNode;
                tail.nextNode = head;
            } else {
                do {
                    Node nextNode = currentNode.nextNode;
                    if (nextNode.equals(node)) {
                        currentNode.nextNode = nextNode.nextNode;
                        count--;
                        break;
                    }
                    currentNode = currentNode.nextNode;
                } while (currentNode != head);
            }
        }
    }

    public void traverseNodes(){
        Node currentNode = head;

        if(head != null){
            do{
                System.out.print(currentNode.value.getItem().getName() + ", ");
                System.out.print(currentNode.value.getId() + ", ");
                System.out.print(currentNode.value.getBounds().x + "-" + currentNode.value.getBounds().y + ", ");
                currentNode = currentNode.nextNode;
            }
            while(currentNode != head);
        }
        System.out.print("\n");
    }

    public InventorySlot traverseInventorySlots(){
        Node currentNode = current;

        if(currentNode == null)
            currentNode = head;

        if(head != null){
            InventorySlot itemToReturn = currentNode.value;
            goRight();
            return itemToReturn;
        }
        else
            return current.value;
    }

    public InventorySlot getItemNode(String itemName){
        Node currentNode = head;

        if(head != null){
            do {
                if(currentNode.value.getItem().getName().equals(itemName)){
                    return currentNode.value;
                }
                else
                    currentNode = currentNode.nextNode;
            }
            while(currentNode != head);
        }
        return null;
    }

    public InventorySlot getItemNode(int id){
        Node currentNode = head;

        if(head != null){
            do {
                if(currentNode.value.getId() == id){
                    return currentNode.value;
                }
                else
                    currentNode = currentNode.nextNode;
            }
            while(currentNode != head);
        }
        return null;
    }

    public Node getNodeById(int id){
        Node currentNode = head;

        if(head != null){
            do {
                if(currentNode.value.getId() == id){
                    return currentNode;
                }
                else
                    currentNode = currentNode.nextNode;
            }
            while(currentNode != head);
        }
        return null;
    }

    public void goRight(){
        if(current == null)
            current = head;

        if(head != null){
            current = current.nextNode;
        }
    }

    public void goLeft(){
        if(current == null)
            current = head;
        if(head != null){
            current = current.previousNode;

        }

    }

    public void sortList(){
        //Current will point to head
        Node current = head, index = null;
        InventorySlot temp;
        if(head == null) {
            System.out.println("List is empty");
        }
        else {
            do{
            //Index will point to node next to current
                index = current.nextNode;
                while(index != head) {
                //If current node is greater than index data, swaps the data
                    if(current.value.getBoundsX() > index.value.getBoundsX()) {
                        temp = current.value;
                        current.value = index.value;
                        index.value = temp;
                    }
                    index= index.nextNode;
                }
                current = current.nextNode;
            } while(current.nextNode != head);
        }
    }


    public void addNodeAtLocation(InventorySlot item, int location){
        Node temp;
        Node newNode = new Node(item);

        temp = head;
        if(temp == null || count < 0)
            throw new IndexOutOfBoundsException("List is empty or the position is not valid");
        else{
            for(int i = 1; i < location - 1; i++){
                temp = temp.nextNode;
            }

            newNode.setNextNode(temp.nextNode);
            (temp.nextNode).previousNode = newNode;
            temp.setNextNode(newNode);
            newNode.setPreviousNode(temp);
            count++;
        }
    }

    /**
     * This is legitimately a mess
     * Will DEFINITELY need to use the swapNodes function in the future instead of this one
     * @param a
     * @param b
     */
    public void swapInfo(Node a, Node b){

        //If it's the same thing, don't do anything
        if(a.equals(b))
            return;

        //Create the temporary values that need to be switched
        InventorySlot tempA = a.value;
        InventorySlot tempB = b.value;

        //Delete the nodes from the list
        deleteNode(a);
        deleteNode(b);

        //Switch the display x coordinate between the two items so that changes would be visible in the GUI
        int tempX = tempA.getBoundsX();
        tempA.setBounds(tempB.getBoundsX());
        tempB.setBounds(tempX);

        //Add the two deleted nodes back according to their id's
        addNodeAtLocation(tempA, tempA.getId());
        addNodeAtLocation(tempB, tempB.getId());

        //Sort and reset the id's of the list, so that the changes in the GUI would be the same "under the hood"
        sortList();
        resetIds();
    }

    public void resetIds(){
        Node currentNode = head;
        int counter = 1;

        if(head != null){
            do{
                currentNode.value.setId(counter);
                counter++;
                currentNode = currentNode.nextNode;
            }
            while(currentNode != head);
        }
        System.out.print("\n");
    }

    public void swapNodes(Node a, Node b){
        if(a == b)
            return;
        if(a.nextNode == b){
            int tempX = b.value.getBoundsX();
            a.nextNode = b.nextNode;
            b.previousNode = a.previousNode;


            if (a.nextNode != null)
                a.nextNode.previousNode = a;

            if (b.previousNode != null)
                b.previousNode.nextNode = b;



            b.value.setBounds(a.value.getBounds());
            b.nextNode = a;
            a.value.setBounds(tempX);
            a.previousNode = b;
        }
        else{
            Node p = b.previousNode;
            Node n = b.nextNode;
            int tempX = b.value.getBoundsX();

            b.previousNode = a.previousNode;
            b.nextNode = a.nextNode;
            b.value.setBounds(a.value.getBoundsX());

            a.previousNode = p;
            a.nextNode = n;
            a.value.setBounds(tempX);

            if (b.nextNode != null)
                b.nextNode.previousNode = b;
            if (b.previousNode != null)
                b.previousNode.nextNode = b;

            if (a.nextNode != null)
                a.nextNode.previousNode = a;
            if (a.previousNode != null)
                a.previousNode.nextNode = a;
        }
    }

    public InventorySlot getCurrentItem() {
        if(current == null)
            current = head;

        return current.value;
    }

    public int getCount() {
        return count;
    }
}

class Node {

    InventorySlot value;
    Node nextNode;
    Node previousNode;

    public Node(InventorySlot value){
        this.value = value;
    }
    public Node(){}

    public Node getNextNode() {
        return nextNode;
    }

    public Node getPreviousNode() {
        return previousNode;
    }

    public void setNextNode(Node nextNode) {
        this.nextNode = nextNode;
    }

    public void setPreviousNode(Node previousNode) {
        this.previousNode = previousNode;
    }

    static Node getNode(){
        return new Node();
    }

}
