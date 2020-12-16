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

    public void traverseNodes(){
        Node currentNode = head;

        if(head != null){
            do{
                System.out.print(currentNode.value.getName() + ", ");
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

    public Item getCurrentItem() {
        return current.value;
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
