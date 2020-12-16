package items;

public class ItemLinkedList {

    private Node head;
    private Node current;
    private Node tail;

    public void addNode(Item value){
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

    }

    public boolean containsNode(String itemName){
        Node currentNode = head;

        if(head == null)
            return false;
        else{
            do {
                if(currentNode.value.getName().equals(itemName)){
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
            if(currentNode.value.getName().equals(valueToDelete)){
                head = head.nextNode;
                tail.nextNode = head;
            }
            else{
                do{
                    Node nextNode = currentNode.nextNode;
                    if(nextNode.value.getName().equals(valueToDelete)){
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

    /**
     * Returns the Item object that's searched by its name
     * @param itemName The name of the Item
     * @return Searched Item object
     */
    public Item getItemNode(String itemName){
        Node currentNode = head;

        if(head != null){
            do {
                if(currentNode.value.getName().equals(itemName)){
                    return currentNode.value;
                }
                else
                    currentNode = currentNode.nextNode;
            }
            while(currentNode != head);
        }
        return null;
    }

    public Item goRight(){
        if(current == null)
            current = head;

        if(head != null){
            Item itemToReturn = current.value;
            current = current.nextNode;
            return itemToReturn;
        }
        return null;
    }

    public Item goLeft(){
        if(current == null)
            current = head;
        if(head != null){
            Item itemToReturn = current.value;
            current = current.previousNode;
            return itemToReturn;
        }
        return null;
    }

    public Node getCurrent() {
        return current;
    }
}

class Node {

    Item value;
    Node nextNode;
    Node previousNode;

    public Node(Item value){
        this.value = value;
    }

}
