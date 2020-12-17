package items;

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

    public void deleteNode(String valueToDelete){
        Node currentNode = head;

        if(head != null){
            if(currentNode.value.getItem().getName().equals(valueToDelete)){
                head = head.nextNode;
                tail.nextNode = head;
            }
            else{
                do{
                    Node nextNode = currentNode.nextNode;
                    if(nextNode.value.getItem().getName().equals(valueToDelete)){
                        currentNode.value = null;
                        currentNode.nextNode = nextNode.nextNode;
                        break;
                    }
                    currentNode = currentNode.nextNode;
                }
                while(currentNode != head);
            }
        }
    }

    public void traverseNodes(){
        Node currentNode = head;

        if(head != null){
            do{
                System.out.print(currentNode.value.getItem().getName() + ", ");
                currentNode = currentNode.nextNode;
            }
            while(currentNode != head);
        }
        System.out.print("\n");
    }

    /**
     * Returns the Item object that's searched by its name
     * @param itemName The name of the Item
     * @return Searched Item object
     */
    public InventorySlot getItemNodeByName(String itemName){
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

    public InventorySlot getItemNodeByID(int id){
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
}
